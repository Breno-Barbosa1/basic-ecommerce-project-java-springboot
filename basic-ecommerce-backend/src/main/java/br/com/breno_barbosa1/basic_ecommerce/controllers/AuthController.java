package br.com.breno_barbosa1.basic_ecommerce.controllers;

import br.com.breno_barbosa1.basic_ecommerce.controllers.docs.AuthControllerDocs;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.UserDTO;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.security.CredentialsDTO;
import br.com.breno_barbosa1.basic_ecommerce.exceptions.RequiredObjectIsNullException;
import br.com.breno_barbosa1.basic_ecommerce.services.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Endpoints for User Authentication")
public class AuthController implements AuthControllerDocs {

    @Autowired
    AuthService service;

    @PostMapping(value = "/register",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        },
        consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        }
    )
    public ResponseEntity<?> register(@RequestBody UserDTO user) {
        if (user == null) throw new RequiredObjectIsNullException("Empty User information!");

        return service.register(user);
    }

    @PostMapping(value = "/login",
        produces = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        },
        consumes = {
            MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_XML_VALUE
        }
    )
    public ResponseEntity<?> login(@RequestBody CredentialsDTO credentialsDTO) {
        if (checkForNullCredentials(credentialsDTO)) return ResponseEntity.status(HttpStatusCode.valueOf(403)).body("Invalid credentials!");

        var token = service.login(credentialsDTO);

        if (token == null) return ResponseEntity.status(HttpStatusCode.valueOf(403)).body("Invalid token!");

        return ResponseEntity.ok(token);
    }

    @PostMapping(value = "/refresh/{email}",
        produces = {
                MediaType.APPLICATION_JSON_VALUE,
                MediaType.APPLICATION_XML_VALUE
        })
    public ResponseEntity<?> refreshToken(@PathVariable("email") String email,
                                      @RequestHeader("Authorization") String token) {

        if(checkForNullParameters(email, token)) return ResponseEntity.status(HttpStatusCode.valueOf(403)).body("Invalid request!");
        var refreshToken = service.refreshToken(email, token);

        if (refreshToken == null) return ResponseEntity.status(HttpStatusCode.valueOf(403)).body("Invalid token!");

        return ResponseEntity.ok(refreshToken);
    }

    private static boolean checkForNullParameters(String email, String token) {
        return email == null || token == null;
    }

    private static boolean checkForNullCredentials(CredentialsDTO credentialsDTO) {
        return credentialsDTO.getEmail() == null || credentialsDTO.getPassword() == null;
    }
}