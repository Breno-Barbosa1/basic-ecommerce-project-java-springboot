package br.com.breno_barbosa1.basic_ecommerce.unittests.mapper.mocks;

import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.ProductDTO;
import br.com.breno_barbosa1.basic_ecommerce.model.Product;

import java.util.ArrayList;
import java.util.List;

public class MockProduct {

    public List<Product> mockEntityList() {
        List<Product> mockEntityList = new ArrayList<>();

        for (int i = 1; i < 14; i++) {
            Product product = mockEntity(i);
            mockEntityList.add(product);
        }

        return mockEntityList;
    }

    public List<ProductDTO> mockDTOList() {
        List<ProductDTO> mockDtoList = new ArrayList<>();

        for (int i = 1; i < 14; i++) {
            ProductDTO product = mockDTO(i);
            mockDtoList.add(product);
        }

        return mockDtoList;
    }

    public Product mockEntity(Integer number) {
        Product product = new Product();

        product.setId(number.longValue());
        product.setName("Product Name" + number);
        product.setDescription("Product Description" + number);
        product.setPrice(100.0);
        product.setStockQuantity(10);

        return product;
    }

    public ProductDTO mockDTO(Integer number) {

        ProductDTO product = new ProductDTO();

        product.setId(number.longValue());
        product.setName("Product Name" + number);
        product.setDescription("Product Description" + number);
        product.setPrice(100.0);
        product.setStockQuantity(10);

        return product;
    }
}