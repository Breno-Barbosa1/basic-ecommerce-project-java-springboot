package br.com.breno_barbosa1.basic_ecommerce.integrationtests.controllers.withjson;

import br.com.breno_barbosa1.basic_ecommerce.config.TestConfig;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.security.CredentialsDTO;
import br.com.breno_barbosa1.basic_ecommerce.integrationtests.dto.UserDTO;
import br.com.breno_barbosa1.basic_ecommerce.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.breno_barbosa1.basic_ecommerce.model.User;
import br.com.breno_barbosa1.basic_ecommerce.model.auth.Permission;
import br.com.breno_barbosa1.basic_ecommerce.repository.PermissionRepository;
import br.com.breno_barbosa1.basic_ecommerce.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ManagerUserControllerJsonTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PermissionRepository permissionRepository;

    private RequestSpecification specification1;
    private RequestSpecification specification2;
    private ObjectMapper objectMapper;

    private UserDTO user1;
    private UserDTO user2;
    private User adminUser;

    @BeforeAll
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        Permission userRole = permissionRepository.findByDescription("ADMIN");

        userRepository.deleteAll();

        var userPermissions = new ArrayList<Permission>();
        userPermissions.add(userRole);

        adminUser = new User();
        adminUser.setEmail("breno@gmail.com");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setAddress("Campina Grande - Brazil");
        adminUser.setCreatedDate(LocalDateTime.now());
        adminUser.setPermissions(userPermissions);
        userRepository.save(adminUser);

        user1 = new UserDTO();
        user2 = new UserDTO();

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
            .setBasePath("/api/manager/users")
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
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(user1)
            .when()
            .post()
            .then()
            .statusCode(201)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .extract()
            .body()
            .asString();

        UserDTO createdUser1 = objectMapper.readValue(content1, UserDTO.class);
        user1 = createdUser1;

        var content2 = given(specification1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(user2)
            .when()
            .post()
            .then()
            .statusCode(201)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .extract()
            .body()
            .asString();

        UserDTO createdUser2 = objectMapper.readValue(content2, UserDTO.class);
        user2 = createdUser2;

        assertNotNull(createdUser1.getId());
        assertEquals("john@gmail.com", createdUser1.getEmail());
        assertEquals("New Orleans - USA", createdUser1.getAddress());

        assertNotNull(createdUser2.getId());
        assertEquals("carlos@gmail.com", createdUser2.getEmail());
        assertEquals("Georgia - USA", createdUser2.getAddress());
    }

    @Test
    @Order(2)
    void findAll() throws JsonProcessingException {
        var content = given(specification2)
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
        assertTrue(foundUsers.size() >= 3);

        assertTrue(foundUsers.stream().anyMatch(user ->
            user.getEmail().equals("john@gmail.com") &&
            user.getAddress().equals("New Orleans - USA")
        ), "could not find any match for john@gmail.com");

        assertTrue(foundUsers.stream().anyMatch(user ->
            user.getEmail().equals("carlos@gmail.com") &&
            user.getAddress().equals("Georgia - USA")
        ), "could not find any match for carlos@gmail.com");
    }

    @Test
    @Order(3)
    void findById() throws IOException {
        var content = given(specification2)
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
        var content = given(specification2)
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

        UserDTO foundUser = objectMapper.readValue(content, UserDTO.class);

        assertNotNull(foundUser);
        assertNotNull(foundUser.getId());
        assertNotNull(foundUser.getEmail());
        assertNotNull(foundUser.getPassword());

        assertEquals("john@gmail.com", foundUser.getEmail());
        assertEquals("New Orleans - USA", foundUser.getAddress());
    }

    public void mockUser() {
        user1.setEmail("john@gmail.com");
        user1.setPassword("New Password1");
        user1.setAddress("New Orleans - USA");

        user2.setEmail("carlos@gmail.com");
        user2.setPassword("New Password2");
        user2.setAddress("Georgia - USA");
    }
}