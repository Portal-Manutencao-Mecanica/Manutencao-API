package com.weg.Maintenance_API.config.idempotency;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.Duration;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

public final class IdempotencyFilter extends OncePerRequestFilter {

    public static final String IDEMPOTENCY_KEY_HEADER = "Idempotency-Key";
    public static final String IDEMPOTENCY_REPLAYED_HEADER = "Idempotency-Replayed";

    private static final Pattern VALID_KEY = Pattern.compile("[A-Za-z0-9._:-]{8,128}");
    private static final Duration DEFAULT_RETENTION = Duration.ofMinutes(10);
    private static final Duration PROCESSING_TIMEOUT = Duration.ofMinutes(2);
    private static final int DEFAULT_MAX_ENTRIES = 10_000;
    private static final int DEFAULT_MAX_REPLAY_BODY_BYTES = 1024 * 1024;
    private static final Set<String> EXCLUDED_RESPONSE_HEADERS = Set.of(
            "connection",
            "content-length",
            "date",
            "keep-alive",
            "transfer-encoding"
    );

    private final Duration retention;
    private final int maxEntries;
    private final int maxReplayBodyBytes;
    private final ConcurrentMap<String, RequestEntry> requests = new ConcurrentHashMap<>();

    public IdempotencyFilter() {
        this(DEFAULT_RETENTION, DEFAULT_MAX_ENTRIES, DEFAULT_MAX_REPLAY_BODY_BYTES);
    }

    IdempotencyFilter(Duration retention, int maxEntries, int maxReplayBodyBytes) {
        if (retention.isNegative() || retention.isZero()) {
            throw new IllegalArgumentException("Idempotency retention must be positive");
        }
        if (maxEntries <= 0 || maxReplayBodyBytes <= 0) {
            throw new IllegalArgumentException("Idempotency limits must be positive");
        }
        this.retention = retention;
        this.maxEntries = maxEntries;
        this.maxReplayBodyBytes = maxReplayBodyBytes;
    }

    // Applies idempotency only to business creation requests that provide a key.
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!HttpMethod.POST.matches(request.getMethod())) return true;

        String idempotencyKey = request.getHeader(IDEMPOTENCY_KEY_HEADER);
        return idempotencyKey == null
                || idempotencyKey.isBlank()
                || applicationPath(request).startsWith("/auth/");
    }

    // Executes the first request and replays its result for duplicate keys.
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String idempotencyKey = request.getHeader(IDEMPOTENCY_KEY_HEADER).trim();
        if (!VALID_KEY.matcher(idempotencyKey).matches()) {
            writeError(
                    response,
                    HttpServletResponse.SC_BAD_REQUEST,
                    "INVALID_IDEMPOTENCY_KEY",
                    "A chave de idempotencia informada e invalida."
            );
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            filterChain.doFilter(request, response);
            return;
        }

        long now = System.currentTimeMillis();
        removeExpiredEntries(now);
        String requestKey = scopedRequestKey(request, authentication.getName(), idempotencyKey);

        while (true) {
            RequestEntry existingEntry = requests.get(requestKey);
            if (existingEntry != null) {
                if (existingEntry.isExpired(now) && requests.remove(requestKey, existingEntry)) {
                    continue;
                }
                replayExistingResponse(existingEntry, response);
                return;
            }

            if (requests.size() >= maxEntries) {
                writeError(
                        response,
                        HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                        "IDEMPOTENCY_CAPACITY_REACHED",
                        "Nao foi possivel proteger a solicitacao contra duplicidade agora."
                );
                return;
            }

            RequestEntry newEntry = new RequestEntry(now + PROCESSING_TIMEOUT.toMillis());
            if (requests.putIfAbsent(requestKey, newEntry) == null) {
                processFirstRequest(requestKey, newEntry, request, response, filterChain);
                return;
            }
        }
    }

    // Captures the first response so subsequent requests do not execute the controller again.
    private void processFirstRequest(
            String requestKey,
            RequestEntry entry,
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        ContentCachingResponseWrapper responseWrapper =
                new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(request, responseWrapper);
            byte[] responseBody = responseWrapper.getContentAsByteArray();
            CachedResponse cachedResponse = new CachedResponse(
                    responseWrapper.getStatus(),
                    responseHeaders(responseWrapper),
                    responseBody.length <= maxReplayBodyBytes ? responseBody : null
            );
            entry.complete(cachedResponse, System.currentTimeMillis() + retention.toMillis());

            if (responseWrapper.getStatus() >= HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
                requests.remove(requestKey, entry);
            }
        } catch (ServletException | IOException | RuntimeException exception) {
            entry.completeExceptionally(exception);
            requests.remove(requestKey, entry);
            throw exception;
        } finally {
            responseWrapper.copyBodyToResponse();
        }
    }

    // Waits for an in-flight request or immediately replays a completed response.
    private void replayExistingResponse(
            RequestEntry entry,
            HttpServletResponse response
    ) throws IOException {
        try {
            CachedResponse cachedResponse = entry.response()
                    .get(PROCESSING_TIMEOUT.toSeconds(), TimeUnit.SECONDS);
            if (cachedResponse.body() == null) {
                response.setHeader(IDEMPOTENCY_REPLAYED_HEADER, "true");
                writeError(
                        response,
                        HttpServletResponse.SC_CONFLICT,
                        "IDEMPOTENCY_RESPONSE_NOT_REPLAYABLE",
                        "A solicitacao ja foi processada e nao sera executada novamente."
                );
                return;
            }

            response.setStatus(cachedResponse.status());
            cachedResponse.headers().forEach((name, values) ->
                    values.forEach(value -> response.addHeader(name, value)));
            response.setHeader(IDEMPOTENCY_REPLAYED_HEADER, "true");
            response.getOutputStream().write(cachedResponse.body());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            writeError(
                    response,
                    HttpServletResponse.SC_CONFLICT,
                    "IDEMPOTENCY_INTERRUPTED",
                    "A solicitacao original ainda esta em processamento."
            );
        } catch (TimeoutException exception) {
            response.setHeader("Retry-After", "1");
            writeError(
                    response,
                    HttpServletResponse.SC_CONFLICT,
                    "IDEMPOTENCY_IN_PROGRESS",
                    "A solicitacao original ainda esta em processamento."
            );
        } catch (ExecutionException exception) {
            writeError(
                    response,
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "IDEMPOTENCY_ORIGINAL_REQUEST_FAILED",
                    "A solicitacao original falhou antes de ser concluida."
            );
        }
    }

    // Copies replay-safe response headers from the original request.
    private Map<String, List<String>> responseHeaders(HttpServletResponse response) {
        Map<String, List<String>> headers = new LinkedHashMap<>();
        for (String headerName : response.getHeaderNames()) {
            if (!EXCLUDED_RESPONSE_HEADERS.contains(headerName.toLowerCase())) {
                Collection<String> values = response.getHeaders(headerName);
                headers.put(headerName, List.copyOf(values));
            }
        }
        return Map.copyOf(headers);
    }

    // Removes completed or abandoned entries after their retention window.
    private void removeExpiredEntries(long now) {
        requests.entrySet().removeIf(entry -> entry.getValue().isExpired(now));
    }

    // Scopes a key to the authenticated user and exact endpoint.
    private String scopedRequestKey(
            HttpServletRequest request,
            String principal,
            String idempotencyKey
    ) {
        String query = request.getQueryString() == null ? "" : request.getQueryString();
        return principal + '|' + request.getMethod() + '|'
                + request.getRequestURI() + '?' + query + '|' + idempotencyKey;
    }

    // Removes the application context path before matching public auth routes.
    private String applicationPath(HttpServletRequest request) {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isBlank() && path.startsWith(contextPath)) {
            return path.substring(contextPath.length());
        }
        return path;
    }

    // Writes an API-compatible error response without invoking a controller.
    private void writeError(
            HttpServletResponse response,
            int status,
            String errorCode,
            String message
    ) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
                "{\"status\":" + status
                        + ",\"error\":\"" + errorCode
                        + "\",\"message\":\"" + message
                        + "\",\"errors\":{}}"
        );
    }

    private record CachedResponse(
            int status,
            Map<String, List<String>> headers,
            byte[] body
    ) {
    }

    private static final class RequestEntry {
        private final CompletableFuture<CachedResponse> response = new CompletableFuture<>();
        private volatile long expiresAt;

        private RequestEntry(long expiresAt) {
            this.expiresAt = expiresAt;
        }

        private CompletableFuture<CachedResponse> response() {
            return response;
        }

        private boolean isExpired(long now) {
            return expiresAt <= now;
        }

        private void complete(CachedResponse cachedResponse, long completedExpiresAt) {
            expiresAt = completedExpiresAt;
            response.complete(cachedResponse);
        }

        private void completeExceptionally(Throwable throwable) {
            response.completeExceptionally(throwable);
        }
    }
}
