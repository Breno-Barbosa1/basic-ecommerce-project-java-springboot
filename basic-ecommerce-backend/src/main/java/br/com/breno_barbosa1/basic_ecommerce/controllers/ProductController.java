package br.com.breno_barbosa1.basic_ecommerce.controllers;

import br.com.breno_barbosa1.basic_ecommerce.controllers.docs.ProductControllerDocs;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.ProductDTO;
import br.com.breno_barbosa1.basic_ecommerce.services.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Endpoints for products management")
public class ProductController implements ProductControllerDocs {
    
    @Autowired
    ProductService service;

    @PreAuthorize("hasAnyAuthority('COMMON_USER', 'MANAGER', 'ADMIN')")
    @GetMapping(produces = {
        MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE
    })
    @Override
    public List<ProductDTO> findAll() {
        return service.findAll();
    }

    @PreAuthorize("hasAnyAuthority('COMMON_USER', 'MANAGER', 'ADMIN')")
    @GetMapping(value = "/search/byName",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        }
    )
    @Override
    public List<ProductDTO> findByName(@RequestParam("name") String name) {
        return service.findByName(name);
    }
}