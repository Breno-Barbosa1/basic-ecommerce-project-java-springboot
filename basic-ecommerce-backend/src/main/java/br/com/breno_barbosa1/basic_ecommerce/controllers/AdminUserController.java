package br.com.breno_barbosa1.basic_ecommerce.controllers;

import br.com.breno_barbosa1.basic_ecommerce.controllers.docs.AdminUserControllerDocs;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.UserDTO;
import br.com.breno_barbosa1.basic_ecommerce.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "Users", description = "Endpoints for users management")
public class AdminUserController implements AdminUserControllerDocs {

    @Autowired
    UserService userService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping(value = "/{id}",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        },
        consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        }
    )
    public UserDTO updateUser(@RequestBody UserDTO user) {
        return userService.update(user);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable("id") Long id) {
        userService.delete(id);

        return ResponseEntity.noContent().build();
    }
}