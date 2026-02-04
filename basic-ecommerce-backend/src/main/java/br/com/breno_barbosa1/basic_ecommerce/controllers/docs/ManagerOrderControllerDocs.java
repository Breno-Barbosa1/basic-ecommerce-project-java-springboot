package br.com.breno_barbosa1.basic_ecommerce.controllers.docs;

import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.OrderResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface ManagerOrderControllerDocs {
    @Operation(summary = "Finds all Orders",
        description = "Finds all Orders",
        tags = "Orders",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200",
                content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = OrderResponseDTO.class))
                    )
                }),
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    List<OrderResponseDTO> getAllOrders();

    @Operation(summary = "Find order by ID",
        description = "Finds an order by their ID",
        tags = "Orders",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = OrderResponseDTO.class))
            ),
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    OrderResponseDTO findOrderById(@PathVariable("id") Long id) throws Exception;
}