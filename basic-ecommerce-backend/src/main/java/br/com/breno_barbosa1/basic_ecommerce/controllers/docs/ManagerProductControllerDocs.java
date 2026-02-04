package br.com.breno_barbosa1.basic_ecommerce.controllers.docs;

import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.ProductDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.PathVariable;

public interface ManagerProductControllerDocs {
    @Operation(summary = "Find Product by ID",
        description = "Finds a Product by their ID",
        tags = "Products",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = ProductDTO.class))
            ),
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    ProductDTO findProductById(@PathVariable("id") Long id);
}