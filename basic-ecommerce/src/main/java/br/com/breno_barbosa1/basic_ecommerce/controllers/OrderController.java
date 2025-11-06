package br.com.breno_barbosa1.basic_ecommerce.controllers;

import br.com.breno_barbosa1.basic_ecommerce.controllers.docs.OrderControllerDocs;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.OrderRequestDTO;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.OrderResponseDTO;
import br.com.breno_barbosa1.basic_ecommerce.services.OrderService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "Orders", description = "Endpoints for order management")
public class OrderController implements OrderControllerDocs {

    @Autowired
    OrderService service;

    @GetMapping(produces = {
        MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE
    })
    @Override
    public List<OrderResponseDTO> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        })
    @Override
    public OrderResponseDTO findById(@PathVariable("id") Long id) throws Exception {
        return service.findById(id);
    }

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
    public OrderResponseDTO create(@RequestBody OrderRequestDTO request) throws Exception {
        return service.create(request);

    }

    @Override
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}