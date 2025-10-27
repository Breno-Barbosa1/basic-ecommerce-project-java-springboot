package br.com.breno_barbosa1.basic_ecommerce.services;

import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.ProductDTO;
import br.com.breno_barbosa1.basic_ecommerce.model.Product;
import br.com.breno_barbosa1.basic_ecommerce.repository.ProductRepository;
import br.com.breno_barbosa1.basic_ecommerce.unittests.mapper.mocks.MockProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    MockProduct input;

    @InjectMocks
    private ProductService service;

    @Mock
    ProductRepository repository;

    @BeforeEach
    void setUp() {
        input = new MockProduct();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById() {
        Product product = input.mockEntity(1);
        product.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        var result = service.findById(1L);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getName());
        assertNotNull(result.getDescription());
        assertNotNull(result.getPrice());
        assertNotNull(result.getStockQuantity());

        assertEquals("Product Name1", result.getName());
        assertEquals("Product Description1", result.getDescription());
        assertEquals(100, result.getPrice());
        assertEquals(10, result.getStockQuantity());
    }

    @Test
    void create() {
        Product product = input.mockEntity(1);
        Product persisted = product;
        persisted.setId(1L);

        when(repository.save(product)).thenReturn(persisted);

        ProductDTO dto = input.mockDTO(1);
        dto.setId(1L);

        var result = service.create(dto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getName());
        assertNotNull(result.getDescription());
        assertNotNull(result.getPrice());
        assertNotNull(result.getStockQuantity());

        assertEquals("Product Name1", result.getName());
        assertEquals("Product Description1", result.getDescription());
        assertEquals(100, result.getPrice());
        assertEquals(10, result.getStockQuantity());
    }

    @Test
    void update() {
        Product product = input.mockEntity(1);
        Product persisted = product;
        persisted.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(repository.save(product)).thenReturn(persisted);

        ProductDTO dto = input.mockDTO(1);
        dto.setId(1L);
        dto.setName("Product Name2");
        dto.setDescription("Product Description2");
        dto.setPrice(200.0);
        dto.setStockQuantity(20);

        var result = service.update(dto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getName());
        assertNotNull(result.getDescription());
        assertNotNull(result.getPrice());
        assertNotNull(result.getStockQuantity());

        assertEquals("Product Name2", result.getName());
        assertEquals("Product Description2", result.getDescription());
        assertEquals(200, result.getPrice());
        assertEquals(20, result.getStockQuantity());
    }

    @Test
    void delete() {
        Product product = input.mockEntity(1);
        product.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(product));

        service.delete(product.getId());

        verify(repository, times(1)).findById(anyLong());
        verify(repository, times(1)).delete(any(Product.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findAll() {
        List<Product> productsList = input.mockEntityList();

        when(repository.findAll()).thenReturn(productsList);

        List<ProductDTO> productsDTOList = input.mockDTOList();

        var result = service.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(productsList.size(), productsDTOList.size());

        ProductDTO firstProduct = productsDTOList.getFirst();
        assertEquals(1, firstProduct.getId());
        assertEquals("Product Name1", firstProduct.getName());
        assertEquals("Product Description1", firstProduct.getDescription());
        assertEquals(100, firstProduct.getPrice());
        assertEquals(10, firstProduct.getStockQuantity());

        ProductDTO fourthProduct = productsDTOList.get(3);
        assertEquals(4, fourthProduct.getId());
        assertEquals("Product Name4", fourthProduct.getName());
        assertEquals("Product Description4", fourthProduct.getDescription());
        assertEquals(100, fourthProduct.getPrice());
        assertEquals(10, fourthProduct.getStockQuantity());
    }
}