package br.com.breno_barbosa1.basic_ecommerce.controllers.docs;

import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface UserControllerDocs {
    @Operation(summary = "Finds all Users",
        description = "Finds all Users",
        tags = "Users",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200",
                content = {
                    @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        array = @ArraySchema(schema = @Schema(implementation = UserDTO.class))
                    )
                }),
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    List<UserDTO> findAll();

    @Operation(summary = "Find User by email",
        description = "Finds a User by their email",
        tags = "Users",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = UserDTO.class))
            ),
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    List<UserDTO> findByEmail(@PathVariable("email") String email);

    @Operation(summary = "Find User by ID",
        description = "Finds a User by their ID",
        tags = "Users",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = UserDTO.class))
            ),
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    UserDTO findById(@PathVariable("id") Long id);

    @Operation(summary = "Creates a new User",
        description = "Creates a new User using JSON or XML representation",
        tags = "Users",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = UserDTO.class))
            ),
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    UserDTO create(@RequestBody UserDTO user);

    @Operation(summary = "Updates user information",
        description = "Updates user information using JSON or XML representation",
        tags = "Users",
        responses = {
            @ApiResponse(
                description = "Success",
                responseCode = "200",
                content = @Content(schema = @Schema(implementation = UserDTO.class))
            ),
            @ApiResponse(description = "No Content", responseCode = "204", content = @Content),
            @ApiResponse(description = "Bad Request", responseCode = "400", content = @Content),
            @ApiResponse(description = "Unauthorized", responseCode = "401", content = @Content),
            @ApiResponse(description = "Not Found", responseCode = "404", content = @Content),
            @ApiResponse(description = "Internal Server Error", responseCode = "500", content = @Content)
        }
    )
    UserDTO update(@RequestBody UserDTO user);

    @Operation(summary = "Deletes a User",
        description = "Deletes a User by their ID",
        tags = {"Users"},
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
    ResponseEntity<?> delete(@PathVariable("id") Long id);
}
