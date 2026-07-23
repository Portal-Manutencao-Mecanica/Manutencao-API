
package com.weg.Maintenance_API.config.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "app.security.login-rate-limit.capacity=2",
        "app.security.login-rate-limit.refill-seconds=60"
})
class LoginRateLimitIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnTooManyRequestsAfterLimit() throws Exception {
        String loginJson = """                                                                                                                                                                                                    
                  {
                      "email": "usuario@email.com",
                      "password": "senha-invalida"
                  }
                  """;

        realizarLogin(loginJson, "192.168.0.50");
        realizarLogin(loginJson, "192.168.0.50");

        mockMvc.perform(
                        post("/auth/login")
                                .with(request -> {
                                    request.setRemoteAddr("192.168.0.50");
                                    return request;
                                })
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(loginJson)
                )
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("Retry-After"));
    }

    private void realizarLogin(String loginJson, String ip) throws Exception {
        mockMvc.perform(
                post("/auth/login")
                        .with(request -> {
                            request.setRemoteAddr(ip);
                            return request;
                        })
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
        );
    }
}