package br.com.breno_barbosa1.basic_ecommerce.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientStockQuantityException extends RuntimeException {


    public InsufficientStockQuantityException() {
        super("Insufficient stock quantity!");
    }

    public InsufficientStockQuantityException(String message) {
        super(message);
    }
}