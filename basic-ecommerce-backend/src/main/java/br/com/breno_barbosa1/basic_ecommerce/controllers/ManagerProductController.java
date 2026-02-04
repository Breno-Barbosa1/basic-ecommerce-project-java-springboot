package br.com.breno_barbosa1.basic_ecommerce.controllers;

import br.com.breno_barbosa1.basic_ecommerce.controllers.docs.ManagerProductControllerDocs;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.ProductDTO;
import br.com.breno_barbosa1.basic_ecommerce.services.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/manager/products")
@Tag(name = "Products", description = "Endpoints for products management")
public class ManagerProductController implements ManagerProductControllerDocs {

    @Autowired
    ProductService productService;

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @GetMapping(value = "/{id}",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        }
    )
    public ProductDTO findProductById(@PathVariable("id") Long id) {
        return productService.findById(id);
    }
}