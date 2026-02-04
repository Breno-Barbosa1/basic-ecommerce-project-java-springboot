package br.com.breno_barbosa1.basic_ecommerce.controllers;

import br.com.breno_barbosa1.basic_ecommerce.controllers.docs.ManagerOrderControllerDocs;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.OrderResponseDTO;
import br.com.breno_barbosa1.basic_ecommerce.services.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/manager/orders")
@Tag(name = "Orders", description = "Endpoints for orders management")
public class ManagerOrderController implements ManagerOrderControllerDocs {

    @Autowired
    OrderService orderService;

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @GetMapping(produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        })
    public List<OrderResponseDTO> getAllOrders() {
        return ResponseEntity.ok(orderService.findAll()).getBody();
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @GetMapping(value = "/{id}",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        })
    public OrderResponseDTO findOrderById(@PathVariable("id") Long id) throws Exception {
        return ResponseEntity.ok( orderService.findById(id)).getBody();
    }
}