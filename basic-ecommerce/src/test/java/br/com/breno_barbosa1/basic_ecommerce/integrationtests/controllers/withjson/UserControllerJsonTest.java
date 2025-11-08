package br.com.breno_barbosa1.basic_ecommerce.integrationtests.controllers.withjson;

import br.com.breno_barbosa1.basic_ecommerce.config.TestConfig;
import br.com.breno_barbosa1.basic_ecommerce.integrationtests.dto.UserDTO;
import br.com.breno_barbosa1.basic_ecommerce.integrationtests.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerJsonTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;

    private static UserDTO user1;
    private static UserDTO user2;

    @BeforeAll
    public static void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        user1 = new UserDTO();
        user2 = new UserDTO();

        specification = new RequestSpecBuilder()
            .addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_LOCAL)
            .setBasePath("/api/users")
            .setPort(TestConfig.SERVER_PORT)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();
    }

    @Test
    @Order(1)
    void create() throws IOException {
        mockUser();

        var content1 = given(specification)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(user1)
            .when()
            .post()
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .extract()
            .body()
            .asString();

        UserDTO createdUser1 = objectMapper.readValue(content1, UserDTO.class);
        user1 = createdUser1;

        var content2 = given(specification)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(user2)
            .when()
            .post()
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .extract()
            .body()
            .asString();

        UserDTO createdUser2 = objectMapper.readValue(content2, UserDTO.class);
        user2 = createdUser2;


        assertNotNull(createdUser1.getId());
        assertEquals("john@gmail.com", createdUser1.getEmail());
        assertEquals("New York - USA", createdUser1.getAddress());

        assertNotNull(createdUser2.getId());
        assertEquals("breno@gmail.com", createdUser2.getEmail());
        assertEquals("Georgia - USA", createdUser2.getAddress());
    }

    @Test
    @Order(2)
    void update() throws IOException {
        user1.setAddress("New Orleans - USA");

        var content = given(specification)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", user1.getId())
            .body(user1)
            .when()
            .put("{id}")
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .extract()
            .body()
            .asString();

        UserDTO updatedUser = objectMapper.readValue(content, UserDTO.class);
        user1 = updatedUser;

        assertNotNull(updatedUser.getId());
        assertEquals("john@gmail.com", updatedUser.getEmail());
        assertEquals("New Orleans - USA", updatedUser.getAddress());
    }

    @Test
    @Order(3)
    void findById() throws IOException {
        var content = given(specification)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .pathParam("id", user1.getId())
            .when()
            .get("{id}")
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .extract()
            .body()
            .asString();

        UserDTO foundUser = objectMapper.readValue(content, UserDTO.class);
        user1 = foundUser;

        assertNotNull(foundUser.getId());
        assertEquals("john@gmail.com", foundUser.getEmail());
        assertEquals("New Orleans - USA", foundUser.getAddress());
    }

    @Test
    @Order(4)
    void findByEmail() throws IOException {
        var content = given(specification)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .queryParam("email", user1.getEmail())
            .when()
            .get("search/byEmail")
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .extract()
            .body()
            .asString();

        List<UserDTO> foundUsers = objectMapper.readValue(content, new TypeReference<List<UserDTO>>() {});

        assertNotNull(foundUsers);
        assertFalse(foundUsers.isEmpty());

        assertTrue(foundUsers.stream().anyMatch(user ->
            user.getEmail().equals("john@gmail.com") &&
            user.getAddress().equals("New Orleans - USA")
        ), "could not find any match for john@gmail.com");
    }

    @Test
    @Order(5)
    void findAll() throws IOException {
        var content = given(specification)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get()
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .extract()
            .body()
            .asString();

        List<UserDTO> foundUsers = objectMapper.readValue(content, new TypeReference<List<UserDTO>>() {});

        assertNotNull(foundUsers);
        assertTrue(foundUsers.size() >= 2);

        assertTrue(foundUsers.stream().anyMatch(user ->
            user.getEmail().equals("john@gmail.com") &&
            user.getAddress().equals("New Orleans - USA")
        ), "could not find any match for john@gmail.com");

        assertTrue(foundUsers.stream().anyMatch(user ->
            user.getEmail().equals("breno@gmail.com") &&
            user.getAddress().equals("Georgia - USA")
        ), "could not find any match for breno@gmail.com");
    }

    @Test
    @Order(6)
    void delete() {
        given(specification)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .pathParam("id", user1.getId())
            .when()
            .delete("{id}")
            .then()
            .statusCode(204);
    }

    public void mockUser() {
        user1.setEmail("john@gmail.com");
        user1.setAddress("New York - USA");

        user2.setEmail("breno@gmail.com");
        user2.setAddress("Georgia - USA");
    }
}