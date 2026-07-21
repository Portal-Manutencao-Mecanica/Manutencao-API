package com.weg.Maintenance_API.config.security;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class LoginRateLimitFilterTest {

    @Test
    void shouldBlockLoginAfterConfiguredNumberOfRequestsFromSameIp() throws Exception {
        LoginRateLimitFilter filter = new LoginRateLimitFilter(5, 60);
        FilterChain chain = mock(FilterChain.class);

        for (int attempt = 0; attempt < 5; attempt++) {
            filter.doFilter(request("192.168.0.10"), new MockHttpServletResponse(), chain);
        }

        MockHttpServletResponse blockedResponse = new MockHttpServletResponse();
        filter.doFilter(request("192.168.0.10"), blockedResponse, chain);

        assertEquals(429, blockedResponse.getStatus());
        assertTrue(blockedResponse.getHeader("Retry-After") != null);
        assertTrue(blockedResponse.getContentAsString().contains("Too many login attempts"));
        verify(chain, times(5)).doFilter(any(), any());
    }

    @Test
    void shouldKeepSeparateLimitPerIp() throws Exception {
        LoginRateLimitFilter filter = new LoginRateLimitFilter(1, 60);
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request("192.168.0.10"), new MockHttpServletResponse(), chain);
        filter.doFilter(request("192.168.0.11"), new MockHttpServletResponse(), chain);

        verify(chain, times(2)).doFilter(any(), any());
    }

    @Test
    void shouldNotLimitOtherEndpoints() throws Exception {
        LoginRateLimitFilter filter = new LoginRateLimitFilter(1, 60);
        FilterChain chain = mock(FilterChain.class);
        MockHttpServletRequest otherEndpoint = request("192.168.0.10");
        otherEndpoint.setRequestURI("/api/auth/me");

        filter.doFilter(otherEndpoint, new MockHttpServletResponse(), chain);
        filter.doFilter(otherEndpoint, new MockHttpServletResponse(), chain);

        verify(chain, times(2)).doFilter(any(), any());
    }

    private MockHttpServletRequest request(String ip) {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/auth/login");
        request.setRemoteAddr(ip);
        return request;
    }
}