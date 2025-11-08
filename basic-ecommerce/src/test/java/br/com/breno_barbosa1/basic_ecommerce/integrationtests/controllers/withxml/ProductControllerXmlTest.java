package br.com.breno_barbosa1.basic_ecommerce.integrationtests.controllers.withxml;

import br.com.breno_barbosa1.basic_ecommerce.config.TestConfig;
import br.com.breno_barbosa1.basic_ecommerce.integrationtests.dto.ProductDTO;
import br.com.breno_barbosa1.basic_ecommerce.integrationtests.testcontainers.AbstractIntegrationTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductControllerXmlTest extends AbstractIntegrationTest {

    private static RequestSpecification specification;
    private static XmlMapper objectMapper;

    private static ProductDTO product1;
    private static ProductDTO product2;

    @BeforeAll
    public static void setUp() {
        objectMapper = new XmlMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        product1 = new ProductDTO();
        product2 = new ProductDTO();

        specification = new RequestSpecBuilder()
            .addHeader(TestConfig.HEADER_PARAM_ORIGIN, TestConfig.ORIGIN_LOCAL)
            .setBasePath("/api/products")
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
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .accept(MediaType.APPLICATION_XML_VALUE)
            .body(product1)
            .when()
            .post()
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .extract()
            .body()
            .asString();

        ProductDTO createdProduct1 = objectMapper.readValue(content1, ProductDTO.class);
        product1 = createdProduct1;

        var content2 = given(specification)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .accept(MediaType.APPLICATION_XML_VALUE)
            .body(product2)
            .when()
            .post()
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .extract()
            .body()
            .asString();

        ProductDTO createdProduct2 = objectMapper.readValue(content2, ProductDTO.class);
        product2 = createdProduct2;

        assertNotNull(createdProduct1.getId());
        assertEquals("Intel Pc", createdProduct1.getName());
        assertEquals("Nvidia powered pc", createdProduct1.getDescription());
        assertEquals(500.0, createdProduct1.getPrice());
        assertEquals(10, createdProduct1.getStockQuantity());

        assertNotNull(createdProduct2.getId());
        assertEquals("Bluetooth Mouse", createdProduct2.getName());
        assertEquals("Logitech Bluetooth Gaming Mouse", createdProduct2.getDescription());
        assertEquals(50.0, createdProduct2.getPrice());
        assertEquals(100, createdProduct2.getStockQuantity());
    }

    @Test
    @Order(2)
    void update() throws IOException {
        product1.setDescription("AMD powered pc");

        var content = given(specification)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .accept(MediaType.APPLICATION_XML_VALUE)
                .pathParam("id", product1.getId())
            .body(product1)
            .when()
            .put("{id}")
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_XML_VALUE)
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
    void findById() throws IOException {
        var content = given(specification)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .accept(MediaType.APPLICATION_XML_VALUE)
            .pathParam("id", product1.getId())
            .when()
            .get("{id}")
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .extract()
            .body()
            .asString();

        ProductDTO foundProduct = objectMapper.readValue(content, ProductDTO.class);
        product1 = foundProduct;

        assertNotNull(foundProduct.getId());
        assertEquals("Intel Pc", foundProduct.getName());
        assertEquals("AMD powered pc", foundProduct.getDescription());
        assertEquals(500.0, foundProduct.getPrice());
        assertEquals(10, foundProduct.getStockQuantity());
    }

    @Test
    @Order(4)
    void findByName() throws IOException {
        var content = given(specification)
            .accept(MediaType.APPLICATION_XML_VALUE)
            .queryParam("name", product1.getName())
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
            product.getDescription().equals("AMD powered pc") &&
            product.getPrice().equals(500.0) &&
            product.getStockQuantity().equals(10)
        ), "could not find any match for this product!");
    }

    @Test
    @Order(5)
    void findAll() throws JsonProcessingException {

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
        assertTrue(foundProducts.size() >= 2);

        assertTrue(foundProducts.stream().anyMatch(product ->
            product.getName().equals("Intel Pc") &&
                product.getDescription().equals("AMD powered pc") &&
                product.getPrice().equals(500.0) &&
                product.getStockQuantity().equals(10)
        ), "could not find any match for Intel Pc");

        assertTrue(foundProducts.stream().anyMatch(product ->
            product.getName().equals("Bluetooth Mouse") &&
                product.getDescription().equals("Logitech Bluetooth Gaming Mouse") &&
                product.getPrice().equals(50.0) &&
                product.getStockQuantity().equals(100)
        ), "could not find any match for Bluetooth Mouse");
    }

    @Test
    @Order(6)
    void delete() {
        given(specification)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .accept(MediaType.APPLICATION_XML_VALUE)
            .pathParam("id", product1.getId())
            .when()
            .delete("{id}")
            .then()
            .statusCode(204);

        given(specification)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .pathParam("id", product1.getId())
            .when()
            .get("{id}")
            .then()
            .statusCode(404);
    }

    public static void mockProduct() {
        product1.setName("Intel Pc");
        product1.setDescription("Nvidia powered pc");
        product1.setPrice(500.0);
        product1.setStockQuantity(10);

        product2.setName("Bluetooth Mouse");
        product2.setDescription("Logitech Bluetooth Gaming Mouse");
        product2.setPrice(50.0);
        product2.setStockQuantity(100);
    }
}