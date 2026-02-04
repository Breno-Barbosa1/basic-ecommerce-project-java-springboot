package br.com.breno_barbosa1.basic_ecommerce.integrationtests.controllers.withjson;

import br.com.breno_barbosa1.basic_ecommerce.config.TestConfig;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.security.CredentialsDTO;
import br.com.breno_barbosa1.basic_ecommerce.integrationtests.dto.ProductDTO;
import br.com.breno_barbosa1.basic_ecommerce.integrationtests.testcontainers.AbstractIntegrationTest;
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

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminProductControllerJsonTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static ObjectMapper objectMapper;

    private static ProductDTO product1;

    @BeforeAll
    public static void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        product1 = new ProductDTO();

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

        specification = new RequestSpecBuilder()
            .addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_LOCAL)
            .addHeader(TestConfig.HEADER_AUTHORIZATION, "Bearer " + token)
            .setBasePath("/api/admin/products")
            .setPort(TestConfig.SERVER_PORT)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();
    }

    @Test
    @Order(1)
    void create() throws IOException {
        mockProduct();

        var content1 = given(specification)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(product1)
            .when()
            .post()
            .then()
            .statusCode(201)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .extract()
            .body()
            .asString();

        ProductDTO createdProduct1 = objectMapper.readValue(content1, ProductDTO.class);
        product1 = createdProduct1;


        assertNotNull(createdProduct1.getId());
        assertEquals("Intel Pc", createdProduct1.getName());
        assertEquals("Nvidia powered pc", createdProduct1.getDescription());
        assertEquals(500.0, createdProduct1.getPrice());
        assertEquals(10, createdProduct1.getStockQuantity());
    }

    @Test
    @Order(2)
    void update() throws IOException {
        product1.setDescription("AMD powered pc");

        var content = given(specification)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .pathParam("id", product1.getId())
            .body(product1)
            .when()
            .put("{id}")
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .extract()
            .body()
            .asString();

        ProductDTO updatedProduct = objectMapper.readValue(content, ProductDTO.class);
        product1 = updatedProduct;

        assertNotNull(updatedProduct.getId());
        assertEquals("Intel Pc", updatedProduct.getName());
        assertEquals("AMD powered pc", updatedProduct.getDescription());
        assertEquals(500.0, updatedProduct.getPrice());
        assertEquals(10, updatedProduct.getStockQuantity());
    }

    @Test
    @Order(3)
    void delete() {
        given(specification)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .pathParam("id", product1.getId())
            .when()
            .delete("{id}")
            .then()
            .statusCode(204);
    }

    public static void mockProduct() {
        product1.setName("Intel Pc");
        product1.setDescription("Nvidia powered pc");
        product1.setPrice(500.0);
        product1.setStockQuantity(10);
    }
}