package br.com.breno_barbosa1.basic_ecommerce.integrationtests.controllers.withxml;

import br.com.breno_barbosa1.basic_ecommerce.config.TestConfig;
import br.com.breno_barbosa1.basic_ecommerce.integrationtests.dto.CredentialsDTO;
import br.com.breno_barbosa1.basic_ecommerce.integrationtests.dto.ProductDTO;
import br.com.breno_barbosa1.basic_ecommerce.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.breno_barbosa1.basic_ecommerce.model.Product;
import br.com.breno_barbosa1.basic_ecommerce.model.User;
import br.com.breno_barbosa1.basic_ecommerce.model.auth.Permission;
import br.com.breno_barbosa1.basic_ecommerce.repository.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductControllerXmlTest extends AbstractIntegrationTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    private RequestSpecification specification;
    private XmlMapper objectMapper;
    private Product product;

    @BeforeAll
    public void setUp() {
        objectMapper = new XmlMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();

        CredentialsDTO credentials =  new CredentialsDTO("breno@Gmail.com", "admin123");

        Permission userRole = permissionRepository.findByDescription("COMMON_USER");

        var userPermissions = new ArrayList<Permission>();
        userPermissions.add(userRole);

        User adminUser = new User();
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

        specification = new RequestSpecBuilder()
            .addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_LOCAL)
            .addHeader(TestConfig.HEADER_AUTHORIZATION, "Bearer " + token)
            .setBasePath("/api/products")
            .setPort(TestConfig.SERVER_PORT)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();
    }

    @Test
    @Order(1)
    void findByName() throws IOException {
        var content = given(specification)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .accept(MediaType.APPLICATION_XML_VALUE)
            .queryParam("name", product.getName())
            .when()
            .get("search/byName")
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .extract()
            .body()
            .asString();

        List<ProductDTO> foundProducts = objectMapper.readValue(content, new TypeReference<List<ProductDTO>>() {});

        assertNotNull(foundProducts);
        assertFalse(foundProducts.isEmpty());

        assertTrue(foundProducts.stream().anyMatch(product ->
            product.getName().equals("Intel Pc") &&
                product.getDescription().equals("Nvidia Intel Pc") &&
                product.getPrice().equals(500.0) &&
                product.getStockQuantity().equals(10)
        ), "could not find any match for this product!");
    }

    @Test
    @Order(2)
    void findAll() throws IOException {
        var content = given(specification)
            .accept(MediaType.APPLICATION_XML_VALUE)
            .when()
            .get()
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .extract()
            .body()
            .asString();

        List<ProductDTO> foundProducts = objectMapper.readValue(content, new TypeReference<List<ProductDTO>>() {});

        assertNotNull(foundProducts);
        assertFalse(foundProducts.isEmpty());

        assertTrue(foundProducts.stream().anyMatch(product ->
            product.getName().equals("Intel Pc") &&
                product.getDescription().equals("Nvidia Intel Pc") &&
                product.getPrice().equals(500.0) &&
                product.getStockQuantity().equals(10)
        ), "could not find any match for Intel Pc");
    }
}