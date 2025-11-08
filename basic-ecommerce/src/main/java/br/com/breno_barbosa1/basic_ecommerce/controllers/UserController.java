package br.com.breno_barbosa1.basic_ecommerce.controllers;

import br.com.breno_barbosa1.basic_ecommerce.controllers.docs.UserControllerDocs;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.UserDTO;
import br.com.breno_barbosa1.basic_ecommerce.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Endpoints for user management")
public class UserController implements UserControllerDocs {

    @Autowired
    UserService service;

    @GetMapping(produces = {
        MediaType.APPLICATION_JSON_VALUE,
        MediaType.APPLICATION_XML_VALUE
    }
    )
    @Override
    public List<UserDTO> findAll() {
        return service.findAll();
    }

    @GetMapping(value = "/email/{email}",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        }
    )
    @Override
    public List<UserDTO> findByEmail(@PathVariable("email") String email) {
        return service.findByEmail(email);
    }

    @GetMapping(value = "/{id}",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        }
    )
    @Override
    public UserDTO findById(@PathVariable("id") Long id) {
        return service.findById(id);
    }

    @PostMapping(
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        },
        consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        }
    )
    @Override
    public UserDTO create(@RequestBody UserDTO user) {
        return service.create(user);
    }

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
    @Override
    public UserDTO update(@RequestBody UserDTO user) {
        return service.update(user);
    }

    @DeleteMapping(value = "/{id}")
    @Override
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.delete(id);

        return ResponseEntity.noContent().build();
    }
}