package com.weg.Maintenance_API.config.idempotency;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IdempotencyFilterTest {

    private static final String IDEMPOTENCY_KEY =
            "8f66c22a-796a-4d09-bfc4-9e6f7d989c87";

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldReplayCompletedResponseWithoutExecutingControllerAgain() throws Exception {
        IdempotencyFilter filter = new IdempotencyFilter(Duration.ofMinutes(1), 100, 1024);
        AtomicInteger executions = new AtomicInteger();
        FilterChain chain = (servletRequest, response) -> {
            executions.incrementAndGet();
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_CREATED);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write("{\"id\":\"created-once\"}");
        };

        authenticate();
        MockHttpServletResponse firstResponse = new MockHttpServletResponse();
        filter.doFilter(request(IDEMPOTENCY_KEY), firstResponse, chain);

        MockHttpServletResponse repeatedResponse = new MockHttpServletResponse();
        filter.doFilter(request(IDEMPOTENCY_KEY), repeatedResponse, chain);

        assertEquals(1, executions.get());
        assertEquals(HttpServletResponse.SC_CREATED, repeatedResponse.getStatus());
        assertEquals(firstResponse.getContentAsString(), repeatedResponse.getContentAsString());
        assertEquals(
                "true",
                repeatedResponse.getHeader(IdempotencyFilter.IDEMPOTENCY_REPLAYED_HEADER)
        );
    }

    @Test
    void shouldProtectConcurrentRequestsWithTheSameKey() throws Exception {
        IdempotencyFilter filter = new IdempotencyFilter(Duration.ofMinutes(1), 100, 1024);
        AtomicInteger executions = new AtomicInteger();
        CountDownLatch controllerStarted = new CountDownLatch(1);
        CountDownLatch finishController = new CountDownLatch(1);
        FilterChain chain = (servletRequest, response) -> {
            executions.incrementAndGet();
            controllerStarted.countDown();
            try {
                assertTrue(finishController.await(5, TimeUnit.SECONDS));
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                throw new IOException(exception);
            }
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpServletResponse.SC_CREATED);
            httpResponse.getWriter().write("created-once");
        };

        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Future<MockHttpServletResponse> first = executor.submit(() ->
                    executeAuthenticated(filter, chain, IDEMPOTENCY_KEY));
            assertTrue(controllerStarted.await(5, TimeUnit.SECONDS));

            Future<MockHttpServletResponse> repeated = executor.submit(() ->
                    executeAuthenticated(filter, chain, IDEMPOTENCY_KEY));
            finishController.countDown();

            MockHttpServletResponse firstResponse = first.get(5, TimeUnit.SECONDS);
            MockHttpServletResponse repeatedResponse = repeated.get(5, TimeUnit.SECONDS);

            assertEquals(1, executions.get());
            assertEquals(firstResponse.getContentAsString(), repeatedResponse.getContentAsString());
            assertEquals(
                    "true",
                    repeatedResponse.getHeader(IdempotencyFilter.IDEMPOTENCY_REPLAYED_HEADER)
            );
        }
    }

    @Test
    void shouldLeaveRequestsWithoutIdempotencyKeyUnchanged() throws Exception {
        IdempotencyFilter filter = new IdempotencyFilter(Duration.ofMinutes(1), 100, 1024);
        AtomicInteger executions = new AtomicInteger();
        FilterChain chain = (servletRequest, servletResponse) -> executions.incrementAndGet();

        authenticate();
        filter.doFilter(request(null), new MockHttpServletResponse(), chain);
        filter.doFilter(request(null), new MockHttpServletResponse(), chain);

        assertEquals(2, executions.get());
    }

    @Test
    void shouldKeepTheSameKeyIsolatedPerAuthenticatedUser() throws Exception {
        IdempotencyFilter filter = new IdempotencyFilter(Duration.ofMinutes(1), 100, 1024);
        AtomicInteger executions = new AtomicInteger();
        FilterChain chain = (servletRequest, servletResponse) -> executions.incrementAndGet();

        authenticate("first.user@example.com");
        filter.doFilter(request(IDEMPOTENCY_KEY), new MockHttpServletResponse(), chain);

        authenticate("second.user@example.com");
        filter.doFilter(request(IDEMPOTENCY_KEY), new MockHttpServletResponse(), chain);

        assertEquals(2, executions.get());
    }

    private MockHttpServletResponse executeAuthenticated(
            IdempotencyFilter filter,
            FilterChain chain,
            String idempotencyKey
    ) throws Exception {
        try {
            authenticate();
            MockHttpServletResponse response = new MockHttpServletResponse();
            filter.doFilter(request(idempotencyKey), response, chain);
            return response;
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private void authenticate() {
        authenticate("user@example.com");
    }

    private void authenticate(String username) {
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(username, null, "ROLE_ADMIN")
        );
    }

    private MockHttpServletRequest request(String idempotencyKey) {
        MockHttpServletRequest request =
                new MockHttpServletRequest("POST", "/api/maquinas");
        request.setContextPath("/api");
        request.setContentType("application/json");
        request.setContent("{\"name\":\"Torno\"}".getBytes());
        if (idempotencyKey != null) {
            request.addHeader(IdempotencyFilter.IDEMPOTENCY_KEY_HEADER, idempotencyKey);
        }
        return request;
    }
}
