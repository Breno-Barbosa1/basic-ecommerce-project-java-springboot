package br.com.breno_barbosa1.basic_ecommerce.services;

import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.ProductDTO;
import br.com.breno_barbosa1.basic_ecommerce.exceptions.RequiredObjectIsNullException;
import br.com.breno_barbosa1.basic_ecommerce.exceptions.ResourceNotFoundException;
import br.com.breno_barbosa1.basic_ecommerce.model.Product;
import br.com.breno_barbosa1.basic_ecommerce.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static br.com.breno_barbosa1.basic_ecommerce.mapper.ObjectMapper.parseListObjects;
import static br.com.breno_barbosa1.basic_ecommerce.mapper.ObjectMapper.parseObject;

@Service
public class ProductService {

    Logger logger = LoggerFactory.getLogger(ProductService.class);
    
    @Autowired
    ProductRepository repository;

    public List<ProductDTO> findAll() {

        logger.info("Finding all products!");

        return parseListObjects(repository.findAll(), ProductDTO.class);
    }

    public ProductDTO findById(Long id) {

        var entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        logger.info("Find by id!");

        return parseObject(entity, ProductDTO.class);
    }

    public ProductDTO create(ProductDTO product) {

        if (product == null) throw new RequiredObjectIsNullException();

        logger.info("Creating an product!");

        var entity = parseObject(product, Product.class);

        return parseObject(repository.save(entity), ProductDTO.class);
    }

    public ProductDTO update(@RequestBody ProductDTO product) {

        if (product == null) throw new RequiredObjectIsNullException();

        logger.info("Updating an product!");

        Product entity = repository.findById(product.getId())
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice());
        entity.setStockQuantity(product.getStockQuantity());

        return parseObject(repository.save(entity), ProductDTO.class);

    }

    public void delete(Long id) {

        logger.info("Deleting one product!");

        Product entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        repository.delete(entity);
    }
}