package br.com.breno_barbosa1.basic_ecommerce.integrationtests.controllers.withxml;

import br.com.breno_barbosa1.basic_ecommerce.config.TestConfig;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.security.CredentialsDTO;
import br.com.breno_barbosa1.basic_ecommerce.integrationtests.dto.ProductDTO;
import br.com.breno_barbosa1.basic_ecommerce.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.breno_barbosa1.basic_ecommerce.model.Product;
import br.com.breno_barbosa1.basic_ecommerce.repository.OrderItemRepository;
import br.com.breno_barbosa1.basic_ecommerce.repository.OrderRepository;
import br.com.breno_barbosa1.basic_ecommerce.repository.ProductRepository;
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

import java.io.IOException;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ManagerProductControllerXmlTest extends AbstractIntegrationTest {
    
    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

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
        
        product = new Product();
        product.setName("Intel Pc");
        product.setDescription("Nvidia Intel Pc");
        product.setPrice(500.0);
        product.setStockQuantity(10);
        productRepository.save(product);

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
            .setBasePath("/api/manager/products")
            .setPort(TestConfig.SERVER_PORT)
            .addFilter(new RequestLoggingFilter(LogDetail.ALL))
            .addFilter(new ResponseLoggingFilter(LogDetail.ALL))
            .build();
    }

    @Test
    @Order(1)
    void findById() throws IOException {
        var content = given(specification)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .accept(MediaType.APPLICATION_XML_VALUE)
            .pathParam("id", product.getId())
            .when()
            .get("/{id}")
            .then()
            .statusCode(200)
            .contentType(MediaType.APPLICATION_XML_VALUE)
            .extract()
            .body()
            .asString();

        ProductDTO foundProduct = objectMapper.readValue(content, ProductDTO.class);

        assertNotNull(foundProduct.getId());
        assertEquals("Intel Pc", foundProduct.getName());
        assertEquals("Nvidia Intel Pc", foundProduct.getDescription());
        assertEquals(500, foundProduct.getPrice());
        assertEquals(10, foundProduct.getStockQuantity());
    }
}