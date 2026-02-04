package br.com.breno_barbosa1.basic_ecommerce.controllers;

import br.com.breno_barbosa1.basic_ecommerce.controllers.docs.ManagerUserControllerDocs;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.UserDTO;
import br.com.breno_barbosa1.basic_ecommerce.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager/users")
@Tag(name = "Users", description = "Endpoints for users management")
public class ManagerUserController implements ManagerUserControllerDocs {

    @Autowired
    UserService userService;

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @GetMapping(produces = {
        MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE
    }
    )
    public List<UserDTO> getUsers() {
        return userService.findAll();
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @GetMapping(value = "/search/byEmail",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        }
    )
    public UserDTO findUserByEmail(@RequestParam("email") String email) {
        return userService.findByEmail(email);
    }

    @PreAuthorize("hasAnyAuthority('MANAGER', 'ADMIN')")
    @GetMapping(value = "/{id}",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        }
    )
    public UserDTO findUserById(@PathVariable("id") Long id) {
        return userService.findById(id);
    }
}