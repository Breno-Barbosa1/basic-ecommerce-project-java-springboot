package br.com.breno_barbosa1.basic_ecommerce.controllers;

import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.ProductDTO;
import br.com.breno_barbosa1.basic_ecommerce.services.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
@Tag(name = "Products", description = "Endpoints for products management")
public class AdminProductController {

    @Autowired
    ProductService productService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping( produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        },
        consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        }
    )
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO product) {
        var dto = productService.create(product);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/{id}",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        },
        consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        }
    )
    public ResponseEntity<ProductDTO> updateProduct(@RequestBody ProductDTO product) {
        var dto = productService.update(product);

        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable("id") Long id) {

        productService.delete(id);

        return ResponseEntity.noContent().build();
    }
}