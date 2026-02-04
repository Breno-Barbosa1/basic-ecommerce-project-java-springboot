package br.com.breno_barbosa1.basic_ecommerce.services;

import br.com.breno_barbosa1.basic_ecommerce.controllers.AdminProductController;
import br.com.breno_barbosa1.basic_ecommerce.controllers.ManagerProductController;
import br.com.breno_barbosa1.basic_ecommerce.controllers.ProductController;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.ProductDTO;
import br.com.breno_barbosa1.basic_ecommerce.exceptions.RequiredObjectIsNullException;
import br.com.breno_barbosa1.basic_ecommerce.exceptions.ResourceNotFoundException;
import br.com.breno_barbosa1.basic_ecommerce.model.Product;
import br.com.breno_barbosa1.basic_ecommerce.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static br.com.breno_barbosa1.basic_ecommerce.mapper.ObjectMapper.parseListObjects;
import static br.com.breno_barbosa1.basic_ecommerce.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class ProductService {

    Logger logger = LoggerFactory.getLogger(ProductService.class);
    
    @Autowired
    ProductRepository repository;

    public List<ProductDTO> findAll() {

        logger.info("Finding all products!");

        var products = parseListObjects(repository.findAll(), ProductDTO.class);
        products.forEach(this::addHateoasLinks);
        return products;
    }

    public List<ProductDTO> findByName(String name) {
        logger.info("Find by name!");

        String nameQuery = "%" + name + "%";

        var products = parseListObjects(repository.findByName(nameQuery), ProductDTO.class);
        products.forEach(this::addHateoasLinks);
        return products;
    }

    public ProductDTO findById(Long id) {
        logger.info("Find by id!");

        var entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        var dto = parseObject(entity, ProductDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public ProductDTO create(ProductDTO product) {

        if (product == null) throw new RequiredObjectIsNullException();

        logger.info("Creating an product!");

        var entity = parseObject(product, Product.class);

        var dto = parseObject(repository.save(entity), ProductDTO.class);
        addHateoasLinks(dto);

        return dto;
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

        var dto = parseObject(repository.save(entity), ProductDTO.class);
        addHateoasLinks(dto);
        return dto;

    }

    public void delete(Long id) {

        logger.info("Deleting one product!");

        Product entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        repository.delete(entity);
    }

    private void addHateoasLinks(ProductDTO dto) {
        dto.add(linkTo(methodOn(ManagerProductController.class).findProductById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(ProductController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(AdminProductController.class).createProduct(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(AdminProductController.class).updateProduct(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(AdminProductController.class).deleteProduct(dto.getId())).withRel("delete").withType("DELETE"));
    }
}