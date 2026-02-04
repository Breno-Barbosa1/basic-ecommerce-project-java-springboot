package br.com.breno_barbosa1.basic_ecommerce.integrationtests.controllers.withxml;

import br.com.breno_barbosa1.basic_ecommerce.config.TestConfig;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.security.CredentialsDTO;
import br.com.breno_barbosa1.basic_ecommerce.integrationtests.dto.UserDTO;
import br.com.breno_barbosa1.basic_ecommerce.integrationtests.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminUserControllerXmlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification1;
    private static RequestSpecification specification2;
    private static XmlMapper objectMapper;

    private static UserDTO user1;

    @BeforeAll
    public static void setUp() {
        objectMapper = new XmlMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        user1 = new UserDTO();

        CredentialsDTO credentials =  new CredentialsDTO("breno@gmail.com", "admin123");

        var token = given()
            .basePath("/auth/login")
            .port(TestConfig.SERVER_PORT)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(credentials)
            .when()
            .post()
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getString("body.accessToken");

        specification1 = new RequestSpecBuilder()
            .addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_LOCAL)
            .setBasePath("/auth/register")
            .setPort(TestConfig.SERVER_PORT)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

        specification2 = new RequestSpecBuilder()
            .addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_LOCAL)
            .addHeader(TestConfig.HEADER_AUTHORIZATION, "Bearer " + token)
            .setBasePath("/api/admin/users")
            .setPort(TestConfig.SERVER_PORT)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();
    }

    @Test
    @Order(1)
    void register() throws IOException {
        mockUser();

        var content1 = given(specification1)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .accept(MediaType.APPLICATION_XML_VALUE)
            .body(user1)
            .when()
            .post()
            .then()
            .statusCode(201)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .extract()
            .body()
            .asString();

        UserDTO createdUser1 = objectMapper.readValue(content1, UserDTO.class);
        user1 = createdUser1;

        assertNotNull(createdUser1.getId());
        assertEquals("bob@gmail.com", createdUser1.getEmail());
        assertEquals("New York - USA", createdUser1.getAddress());
    }

    @Test
    @Order(2)
    void update() throws IOException {
        user1.setAddress("New Orleans - USA");

        var content = given(specification2)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .accept(MediaType.APPLICATION_XML_VALUE)
            .pathParam("id", user1.getId())
            .body(user1)
            .when()
            .put("{id}")
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .extract()
            .body()
            .asString();

        UserDTO updatedUser = objectMapper.readValue(content, UserDTO.class);
        user1 = updatedUser;

        assertNotNull(updatedUser.getId());
        assertEquals("bob@gmail.com", updatedUser.getEmail());
        assertEquals("New Orleans - USA", updatedUser.getAddress());
    }

    @Test
    @Order(3)
    void delete() {
        given(specification2)
            .pathParam("id", user1.getId())
            .when()
            .delete("{id}")
            .then()
            .statusCode(204);
    }

    public void mockUser() {
        user1.setEmail("bob@gmail.com");
        user1.setPassword("New Password1");
        user1.setAddress("New York - USA");
    }
}