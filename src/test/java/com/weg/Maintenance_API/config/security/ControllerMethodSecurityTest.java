package com.weg.Maintenance_API.config.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ControllerMethodSecurityTest {

    @Autowired
    @Qualifier("requestMappingHandlerMapping")
    private RequestMappingHandlerMapping handlerMapping;

    @Test
    void everyApplicationEndpointDeclaresPreAuthorize() {
        List<String> unsecuredMethods = handlerMapping.getHandlerMethods().values().stream()
                .filter(this::isApplicationHandler)
                .filter(handler -> AnnotatedElementUtils.findMergedAnnotation(
                        handler.getMethod(),
                        PreAuthorize.class
                ) == null)
                .map(handler -> handler.getBeanType().getSimpleName()
                        + "#" + handler.getMethod().getName())
                .sorted()
                .toList();

        assertThat(unsecuredMethods)
                .as("Todos os endpoints da aplicacao devem declarar @PreAuthorize")
                .isEmpty();
    }

    private boolean isApplicationHandler(HandlerMethod handler) {
        Package handlerPackage = handler.getBeanType().getPackage();
        return handlerPackage != null
                && handlerPackage.getName().startsWith("com.weg.Maintenance_API");
    }
}
