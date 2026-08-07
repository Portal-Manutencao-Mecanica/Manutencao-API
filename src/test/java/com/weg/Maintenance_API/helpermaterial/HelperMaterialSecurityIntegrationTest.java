package com.weg.Maintenance_API.helpermaterial;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HelperMaterialSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "PROFESSOR")
    void professorCanCreateUpdateAndDeleteSupportMaterials() throws Exception {
        MvcResult createResult = mockMvc.perform(post("/material-apoio")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Manual de segurança",
                                  "description": "Orientações para operação segura.",
                                  "url": "https://example.test/manual-seguranca",
                                  "type": "MANUAL"
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode createdMaterial = objectMapper.readTree(
                createResult.getResponse().getContentAsString()
        );
        String materialId = createdMaterial.get("id").asText();

        mockMvc.perform(put("/material-apoio/{id}", materialId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Manual de segurança atualizado",
                                  "description": "Orientações revisadas para operação segura.",
                                  "url": "https://example.test/manual-seguranca-atualizado",
                                  "type": "TECNICO"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/material-apoio/{id}", materialId))
                .andExpect(status().isNoContent());
    }
}
