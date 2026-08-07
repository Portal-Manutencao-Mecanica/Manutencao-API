package com.weg.Maintenance_API.organization.entity;

import com.weg.Maintenance_API.enums.OrganizationType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrganizationTest {

    @Test
    void shouldAcceptSenaiEducationEmailDomainForSenaiOrganization() {
        Organization organization = new Organization(
                "SENAI",
                OrganizationType.SENAI,
                "senai-industria.org.br"
        );

        assertThat(organization.acceptsEmail("aluno@edu.sc.senai.br")).isTrue();
    }

    @Test
    void shouldNotAcceptSenaiEducationEmailDomainForOtherOrganizationType() {
        Organization organization = new Organization(
                "Outra organizacao",
                OrganizationType.OTHER,
                "example.com"
        );

        assertThat(organization.acceptsEmail("aluno@edu.sc.senai.br")).isFalse();
    }
}
