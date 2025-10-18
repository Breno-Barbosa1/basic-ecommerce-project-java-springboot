package br.com.breno_barbosa1.basic_ecommerce.exceptions;

import java.util.Date;

public record ExceptionResponse(Date timestamp, String message, String details) {}