package br.com.breno_barbosa1.basic_ecommerce.integrationtests.controllers.withjson;

import br.com.breno_barbosa1.basic_ecommerce.config.TestConfig;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.security.CredentialsDTO;
import br.com.breno_barbosa1.basic_ecommerce.integrationtests.dto.UserDTO;
import br.com.breno_barbosa1.basic_ecommerce.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.breno_barbosa1.basic_ecommerce.model.User;
import br.com.breno_barbosa1.basic_ecommerce.model.auth.Permission;
import br.com.breno_barbosa1.basic_ecommerce.repository.PermissionRepository;
import br.com.breno_barbosa1.basic_ecommerce.repository.UserRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminUserControllerJsonTest extends AbstractIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private RequestSpecification specification1;
    private RequestSpecification specification2;
    private ObjectMapper objectMapper;

    private User adminUser;
    private UserDTO user;

    @BeforeAll
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        Permission userRole = permissionRepository.findByDescription("ADMIN");

        var userPermissions = new ArrayList<Permission>();
        userPermissions.add(userRole);

        userRepository.deleteAll();

        adminUser = new User();
        adminUser.setEmail("breno@gmail.com");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setAddress("Campina Grande - Brazil");
        adminUser.setCreatedDate(LocalDateTime.now());
        adminUser.setPermissions(userPermissions);
        userRepository.save(adminUser);

        user = new UserDTO();
        user.setEmail("bob@gmail.com");
        user.setPassword(passwordEncoder.encode("bob123"));
        user.setAddress("New York - USA");

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

        var content1 = given(specification1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(user)
            .when()
            .post()
            .then()
            .statusCode(201)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .extract()
            .body()
            .asString();

        UserDTO createdUser = objectMapper.readValue(content1, UserDTO.class);
        user = createdUser;

        assertNotNull(createdUser.getId());
        assertEquals("bob@gmail.com", user.getEmail());
        assertEquals("New York - USA", user.getAddress());
    }

    @Test
    @Order(2)
    void update() throws IOException {
        user.setAddress("New Orleans - USA");

        var content = given(specification2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", user.getId())
            .body(user)
            .when()
            .put("{id}")
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .extract()
            .body()
            .asString();

        UserDTO updatedUser = objectMapper.readValue(content, UserDTO.class);
        user = updatedUser;

        assertNotNull(updatedUser.getId());
        assertEquals("bob@gmail.com", user.getEmail());
        assertEquals("New Orleans - USA", user.getAddress());
    }

    @Test
    @Order(3)
    void delete() {
        given(specification2)
            .pathParam("id", user.getId())
            .when()
            .delete("{id}")
            .then()
            .statusCode(204);
    }
}