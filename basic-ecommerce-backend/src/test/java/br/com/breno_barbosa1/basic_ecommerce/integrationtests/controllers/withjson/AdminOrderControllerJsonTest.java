package br.com.breno_barbosa1.basic_ecommerce.integrationtests.controllers.withjson;

import br.com.breno_barbosa1.basic_ecommerce.config.TestConfig;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.OrderItemRequestDTO;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.OrderRequestDTO;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.OrderResponseDTO;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.security.CredentialsDTO;
import br.com.breno_barbosa1.basic_ecommerce.integrationtests.dto.UserDTO;
import br.com.breno_barbosa1.basic_ecommerce.integrationtests.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminOrderControllerJsonTest extends AbstractIntegrationTest {

    private static RequestSpecification specification1;
    private static RequestSpecification specification2;
    private static RequestSpecification specification3;
    private static ObjectMapper objectMapper;
    private static OrderItemRequestDTO orderItem;
    private static OrderRequestDTO orderRequest;
    private static OrderResponseDTO orderResponse;
    private static UserDTO user;

    @BeforeAll
    public static void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.registerModule(new JavaTimeModule());
        
        CredentialsDTO credentials =  new CredentialsDTO("breno@gmail.com", "admin123");
        
        orderItem = new OrderItemRequestDTO();
        orderRequest = new OrderRequestDTO();
        orderResponse = new OrderResponseDTO();
        user = new UserDTO();

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
            .setBasePath("/api/orders")
            .setPort(TestConfig.SERVER_PORT)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

        specification3 = new RequestSpecBuilder()
            .addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_LOCAL)
            .addHeader(TestConfig.HEADER_AUTHORIZATION, "Bearer " + token)
            .setBasePath("/api/admin/orders")
            .setPort(TestConfig.SERVER_PORT)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();
    }

    @Test
    @Order(1)
    void register() throws IOException {
        mockUser();

        var content = given(specification1)
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

        UserDTO createdUser = objectMapper.readValue(content, UserDTO.class);
        user = createdUser;

        assertNotNull(createdUser.getId());
        assertEquals("bob@gmail.com", createdUser.getEmail());
        assertEquals("New York - USA", createdUser.getAddress());
    }

    @Test
    @Order(2)
    void create() throws IOException {
        mockOrderItem();
        mockOrderRequest();

        var content1 = given(specification2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(orderRequest)
            .when()
            .post()
            .then()
            .statusCode(201)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .extract()
            .body()
            .asString();

        OrderResponseDTO createdOrder = objectMapper.readValue(content1, OrderResponseDTO.class);
        orderResponse = createdOrder;

        assertNotNull(orderResponse.getId());
        assertNotNull(orderResponse.getUserId());
        assertNotNull(orderResponse.getEmail());
        assertNotNull(orderResponse.getCreatedDate());
        assertNotNull(orderResponse.getTotal());
        assertNotNull(orderResponse.getItems());

        assertEquals(1, orderResponse.getItems().size());
        assertEquals(2000 , orderResponse.getTotal());
        assertEquals( "bob@gmail.com" , orderResponse.getEmail());
        assertEquals( user.getId() , orderResponse.getUserId());
    }

    @Test
    @Order(2)
    void delete() {
        given(specification3)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .pathParam("id", orderResponse.getId())
            .when()
            .delete("{id}")
            .then()
            .statusCode(204);
    }

    public void mockUser() {
        user.setEmail("bob@gmail.com");
        user.setPassword("New Password1");
        user.setAddress("New York - USA");
    }

    public void mockOrderItem () {
        orderItem.setProductId(1L);
        orderItem.setQuantity(2);
    }

    public void mockOrderRequest () {
        orderRequest.setEmail(user.getEmail());
        orderRequest.setItems(Collections.singletonList(orderItem));
    }
}