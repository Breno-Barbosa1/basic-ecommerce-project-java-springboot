package br.com.breno_barbosa1.basic_ecommerce.integrationtests.controllers.withxml;

import br.com.breno_barbosa1.basic_ecommerce.config.TestConfig;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.security.CredentialsDTO;
import br.com.breno_barbosa1.basic_ecommerce.integrationtests.dto.OrderItemRequestDTO;
import br.com.breno_barbosa1.basic_ecommerce.integrationtests.dto.OrderRequestDTO;
import br.com.breno_barbosa1.basic_ecommerce.integrationtests.dto.OrderResponseDTO;
import br.com.breno_barbosa1.basic_ecommerce.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.breno_barbosa1.basic_ecommerce.model.Product;
import br.com.breno_barbosa1.basic_ecommerce.model.User;
import br.com.breno_barbosa1.basic_ecommerce.model.auth.Permission;
import br.com.breno_barbosa1.basic_ecommerce.repository.PermissionRepository;
import br.com.breno_barbosa1.basic_ecommerce.repository.ProductRepository;
import br.com.breno_barbosa1.basic_ecommerce.repository.UserRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminOrderControllerXmlTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RequestSpecification specification1;
    private RequestSpecification specification2;
    private XmlMapper objectMapper;
    private OrderItemRequestDTO orderItem;
    private OrderRequestDTO orderRequest;
    private OrderResponseDTO orderResponseDTO;
    
    private Product product;
    private User adminUser;

    @BeforeAll
    public void setUp() {

        objectMapper = new XmlMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.registerModule(new JavaTimeModule());

        Permission userRole = permissionRepository.findByDescription("ADMIN");

        var userPermissions = new ArrayList<Permission>();
        userPermissions.add(userRole);

        userRepository.deleteAll();
        productRepository.deleteAll();

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

        orderResponseDTO = new OrderResponseDTO();

        orderItem = new OrderItemRequestDTO();
        orderItem.setProductId(product.getId());
        orderItem.setQuantity(2);

        orderRequest = new OrderRequestDTO();
        orderRequest.setEmail(adminUser.getEmail());
        orderRequest.setItems(List.of(orderItem));
        
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
            .addHeader(TestConfig.HEADER_AUTHORIZATION, "Bearer " + token)
            .setBasePath("/api/orders")
            .setPort(TestConfig.SERVER_PORT)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();

        specification2 = new RequestSpecBuilder()
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
    void create() throws IOException {
        String xmlBody = objectMapper.writeValueAsString(orderRequest);

        var content1 = given(specification1)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .accept(MediaType.APPLICATION_XML_VALUE)
            .body(xmlBody)
            .when()
            .post()
            .then()
            .statusCode(201)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .extract()
            .body()
            .asString();

        OrderResponseDTO createdOrder = objectMapper.readValue(content1, OrderResponseDTO.class);
        orderResponseDTO = createdOrder;

        assertNotNull(orderResponseDTO.getId());
        assertNotNull(orderResponseDTO.getUserId());
        assertNotNull(orderResponseDTO.getEmail());
        assertNotNull(orderResponseDTO.getCreatedDate());
        assertNotNull(orderResponseDTO.getTotal());
        assertNotNull(orderResponseDTO.getItems());

        assertEquals(1, orderResponseDTO.getItems().size());
        assertEquals(1000 , orderResponseDTO.getTotal());
        assertEquals( "breno@gmail.com" , orderResponseDTO.getEmail());
        assertEquals( adminUser.getId() , orderResponseDTO.getUserId());
    }

    @Test
    @Order(2)
    void delete() {
        given(specification2)
            .pathParam("id", orderResponseDTO.getId())
            .when()
            .delete("{id}")
            .then()
            .statusCode(204);
    }
}