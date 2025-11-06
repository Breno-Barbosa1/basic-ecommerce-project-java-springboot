package br.com.breno_barbosa1.basic_ecommerce.integrationtests.repository;

import br.com.breno_barbosa1.basic_ecommerce.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.breno_barbosa1.basic_ecommerce.model.Product;
import br.com.breno_barbosa1.basic_ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    ProductRepository productRepository;

    @Test
    public void createProductAndFindByIdTest() {
        Product newProduct = new Product();

        newProduct.setName("Kitchen Knife");
        newProduct.setDescription("Tramontina Kitchen Knife");
        newProduct.setPrice(40.0);
        newProduct.setStockQuantity(10);

        var createdUser = productRepository.save(newProduct);

        Optional<Product> retrievedProduct = productRepository.findById(createdUser.getId());

        assertNotNull(retrievedProduct);
        assertTrue(retrievedProduct.get().getId() > 0);
        assertEquals("Kitchen Knife", retrievedProduct.get().getName());
        assertEquals("Tramontina Kitchen Knife", retrievedProduct.get().getDescription());
        assertEquals(40.0, retrievedProduct.get().getPrice());
        assertEquals(10, retrievedProduct.get().getStockQuantity());
    }
}