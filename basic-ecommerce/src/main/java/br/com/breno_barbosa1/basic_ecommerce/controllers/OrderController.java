package br.com.breno_barbosa1.basic_ecommerce.controllers;

import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.OrderRequestDTO;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.OrderResponseDTO;
import br.com.breno_barbosa1.basic_ecommerce.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService service;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<OrderResponseDTO> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/{id}")
    public OrderResponseDTO findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @PostMapping(
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public OrderResponseDTO create(@RequestBody OrderRequestDTO request) throws Exception {
        return service.create(request);

    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {

        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}