package br.com.breno_barbosa1.basic_ecommerce.controllers.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface AdminOrderControllerDocs {

    @Operation(summary = "Deletes an order",
        description = "Deletes an order by their ID",
        tags = {"Orders"},
        responses = {
            @ApiResponse(
                description = "No Content",
                responseCode = "204", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    @DeleteMapping(value = "/{id}")
    ResponseEntity<?> deleteOrder(@PathVariable("id") Long id);
}