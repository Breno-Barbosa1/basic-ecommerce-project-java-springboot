package br.com.breno_barbosa1.basic_ecommerce.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidJWTAuthenticationException extends RuntimeException {
    public InvalidJWTAuthenticationException() {
        super("Invalid or Expired JWT Token!");
    }

    public InvalidJWTAuthenticationException(String message) {
        super(message);
    }
}