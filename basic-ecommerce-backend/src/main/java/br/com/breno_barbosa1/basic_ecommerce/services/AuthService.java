package br.com.breno_barbosa1.basic_ecommerce.services;

import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.UserDTO;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.security.CredentialsDTO;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.security.TokenDTO;
import br.com.breno_barbosa1.basic_ecommerce.exceptions.ObjectAlreadyExistsException;
import br.com.breno_barbosa1.basic_ecommerce.exceptions.RequiredObjectIsNullException;
import br.com.breno_barbosa1.basic_ecommerce.model.User;
import br.com.breno_barbosa1.basic_ecommerce.model.auth.Permission;
import br.com.breno_barbosa1.basic_ecommerce.repository.PermissionRepository;
import br.com.breno_barbosa1.basic_ecommerce.repository.UserRepository;
import br.com.breno_barbosa1.basic_ecommerce.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static br.com.breno_barbosa1.basic_ecommerce.mapper.ObjectMapper.parseObject;

@Service
public class AuthService {

    Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository repository;

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider provider;

    public ResponseEntity<?> register(@RequestBody UserDTO user) {
        logger.info("Registering one User!");

        if (user == null) throw new RequiredObjectIsNullException("Empty User information!");

        var searchedUser = repository.findByEmail(user.getEmail());

        if (searchedUser != null) throw new ObjectAlreadyExistsException("User already exists!");

        var entity = parseObject(user, User.class);

        Permission userRole = permissionRepository.findByDescription("COMMON_USER");

        var userPermissions = new ArrayList<Permission>();
        userPermissions.add(userRole);

        entity.setCreatedDate(LocalDateTime.now());
        entity.setPermissions(userPermissions);
        entity.setPassword(passwordEncoder.encode(user.getPassword()));

        var savedUser = repository.save(entity);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();

        return ResponseEntity.created(uri).body(parseObject(savedUser, UserDTO.class));
    }

    public ResponseEntity<?> login(CredentialsDTO credentialsDTO) {
        logger.info("Logging in one user!");

        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    credentialsDTO.getEmail(),
                    credentialsDTO.getPassword()
                )
            );
        } catch (UsernameNotFoundException e) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password!");
        }

        User user = repository.findByEmail(credentialsDTO.getEmail());

        if (user == null) throw new ResponseStatusException(
            HttpStatus.UNAUTHORIZED,
            "Invalid email or password!");

        var permissions = user.getPermissions()
            .stream()
            .map(Permission::getDescription)
            .toList();

        var token = provider.generateAccessToken(
            credentialsDTO.getEmail(),
            permissions
        );

        return ResponseEntity.ok(token);
    }

    public ResponseEntity<?> refreshToken(String email, String token) {
        logger.info("Refreshing user token!");

        TokenDTO dto;
        var user = repository.findByEmail(email);
        if (user == null) throw new UsernameNotFoundException(email + "not found!");
        dto = provider.refreshToken(token);

        return ResponseEntity.ok(dto);
    }
}