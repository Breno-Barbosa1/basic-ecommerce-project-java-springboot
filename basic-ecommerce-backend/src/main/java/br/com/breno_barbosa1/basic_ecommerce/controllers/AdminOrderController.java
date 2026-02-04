package br.com.breno_barbosa1.basic_ecommerce.controllers;

import br.com.breno_barbosa1.basic_ecommerce.controllers.docs.AdminOrderControllerDocs;
import br.com.breno_barbosa1.basic_ecommerce.services.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/orders")
@Tag(name = "Orders", description = "Endpoints for orders management")
public class AdminOrderController implements AdminOrderControllerDocs {

    @Autowired
    OrderService orderService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable("id") Long id) {

        orderService.delete(id);

        return ResponseEntity.noContent().build();
    }
}