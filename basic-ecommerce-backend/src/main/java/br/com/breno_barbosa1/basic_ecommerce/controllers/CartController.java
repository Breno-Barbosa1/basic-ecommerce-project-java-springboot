package br.com.breno_barbosa1.basic_ecommerce.controllers;

import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.*;
import br.com.breno_barbosa1.basic_ecommerce.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
public class CartController {

    @Autowired
    CartService cartService;

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'COMMON_USER')")
    @GetMapping( value = "/{email}",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        }
    )
    public ResponseEntity<CartDTO> getCart(@PathVariable("email") String email) {
        var cart = cartService.getCart(email);

        return ResponseEntity.ok(cart);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'COMMON_USER')")
    @PutMapping(value = "/update",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        },
        consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        }
    )
    public ResponseEntity<CartDTO> updateCart(@RequestBody CartUpdateRequestDTO request) {
        var cart = cartService.updateCart(request.getEmail(), request.getItem());

        return ResponseEntity.ok(cart);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'COMMON_USER')")
    @PostMapping(value = "/checkout/{email}",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        },
        consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        }
    )
    public ResponseEntity<OrderResponseDTO> placeOrder(@PathVariable("email") String email,
           @RequestBody OrderRequestDTO requestDTO) {
        var cart = cartService.placeOrder(email, requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(cart);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'COMMON_USER')")
    @DeleteMapping(value = "/remove/{email}/{cartItemId}")
    public CartDTO removeItem(@PathVariable String email,
            @PathVariable Long cartItemId) {
        return cartService.removeItemFromCart(email, cartItemId);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'MANAGER', 'COMMON_USER')")
    @DeleteMapping(value = "/{email}")
    public ResponseEntity<CartDTO> clearCart(@PathVariable("email") String email) {

        return ResponseEntity.ok(cartService.clearCart(email));
    }
}