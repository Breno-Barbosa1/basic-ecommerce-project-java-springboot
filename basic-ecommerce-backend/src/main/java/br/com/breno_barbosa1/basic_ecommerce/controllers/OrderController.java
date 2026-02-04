package br.com.breno_barbosa1.basic_ecommerce.controllers;

import br.com.breno_barbosa1.basic_ecommerce.controllers.docs.OrderControllerDocs;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.OrderRequestDTO;
import br.com.breno_barbosa1.basic_ecommerce.services.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Endpoints for orders management")
public class OrderController implements OrderControllerDocs {

    @Autowired
    OrderService service;

    @PreAuthorize("hasAnyAuthority('COMMON_USER', 'MANAGER', 'ADMIN')")
    @PostMapping(
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        },
        consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        }
    )
    @Override
    public ResponseEntity<?> create(@RequestBody OrderRequestDTO request) throws Exception {
        var dto = service.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }
}