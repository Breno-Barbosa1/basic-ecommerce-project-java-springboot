package br.com.breno_barbosa1.basic_ecommerce.integrationtests.controllers.withjson;

import br.com.breno_barbosa1.basic_ecommerce.config.TestConfig;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.OrderItemRequestDTO;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.OrderRequestDTO;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.OrderResponseDTO;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.security.CredentialsDTO;
import br.com.breno_barbosa1.basic_ecommerce.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.breno_barbosa1.basic_ecommerce.model.Product;
import br.com.breno_barbosa1.basic_ecommerce.model.User;
import br.com.breno_barbosa1.basic_ecommerce.model.auth.Permission;
import br.com.breno_barbosa1.basic_ecommerce.repository.OrderRepository;
import br.com.breno_barbosa1.basic_ecommerce.repository.PermissionRepository;
import br.com.breno_barbosa1.basic_ecommerce.repository.ProductRepository;
import br.com.breno_barbosa1.basic_ecommerce.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ManagerOrderControllerJsonTest extends AbstractIntegrationTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private RequestSpecification specification1;
    private RequestSpecification specification2;
    private ObjectMapper objectMapper;

    private OrderRequestDTO orderRequest;
    private OrderItemRequestDTO orderItem;
    private OrderResponseDTO responseDTO;
    private Product product;
    private User adminUser;


    @BeforeAll
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.registerModule(new JavaTimeModule());

        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        Permission userRole = permissionRepository.findByDescription("ADMIN");

        var userPermissions = new ArrayList<Permission>();
        userPermissions.add(userRole);

        adminUser = new User();
        adminUser.setEmail("breno@gmail.com");
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setAddress("Campina Grande - Brazil");
        adminUser.setCreatedDate(LocalDateTime.now());
        adminUser.setPermissions(userPermissions);
        userRepository.save(adminUser);

        product = new Product();
        product.setName("Intel Pc");
        product.setDescription("Nvidia Intel Pc");
        product.setPrice(500.0);
        product.setStockQuantity(10);
        productRepository.save(product);

        responseDTO = new OrderResponseDTO();

        CredentialsDTO credentials = new CredentialsDTO("breno@gmail.com", "admin123");

        orderItem = new OrderItemRequestDTO();
        orderItem.setProductId(productRepository.findByName(product.getName()).get(0).getId());
        orderItem.setQuantity(2);

        orderRequest = new OrderRequestDTO();
        orderRequest.setEmail(adminUser.getEmail());
        orderRequest.setItems(List.of(orderItem));

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
            .addHeader(TestConfig.HEADER_AUTHORIZATION, "Bearer " + token)
            .setBasePath("/api/orders")
            .setPort(TestConfig.SERVER_PORT)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

        specification2 = new RequestSpecBuilder()
            .addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_LOCAL)
            .addHeader(TestConfig.HEADER_AUTHORIZATION, "Bearer " + token)
            .setBasePath("/api/manager/orders")
            .setPort(TestConfig.SERVER_PORT)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();
    }

    @Test
    @Order(1)
    void createOrder() throws JsonProcessingException {
        var content = given(specification1)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(orderRequest)
            .when()
            .post()
            .then()
            .statusCode(201)
            .extract()
            .body()
            .asString();

        OrderResponseDTO response = objectMapper.readValue(content, OrderResponseDTO.class);
        responseDTO = response;

        assertNotNull(responseDTO);
        assertEquals(1, responseDTO.getItems().size());
        assertEquals(1000, responseDTO.getTotal());
    }

    @Test
    @Order(2)
    void findOrderById() throws JsonProcessingException {
        var content = given(specification2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .pathParam("id", responseDTO.getId())
            .when()
            .get("/{id}")
            .then()
            .statusCode(200)
            .extract()
            .body()
            .asString();

        OrderResponseDTO response = objectMapper.readValue(content, OrderResponseDTO.class);
        responseDTO = response;

        assertNotNull(responseDTO);
        assertEquals(1, responseDTO.getItems().size());
        assertEquals(1000, responseDTO.getTotal());
    }

    @Test
    @Order(3)
    void getAllOrders() throws JsonProcessingException {
        var content = given(specification2)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .get()
            .then()
            .statusCode(200)
            .extract()
            .body()
            .asString();

        List<OrderResponseDTO> foundOrders = objectMapper.readValue(content, new TypeReference<List<OrderResponseDTO>>(){});

        assertNotNull(foundOrders);

        assertTrue(foundOrders.size() <= 1);
        assertTrue(foundOrders.stream().anyMatch(order -> order.getUserId().equals(adminUser.getId())));
    }
}