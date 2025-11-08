package br.com.breno_barbosa1.basic_ecommerce.services;

import br.com.breno_barbosa1.basic_ecommerce.controllers.UserController;
import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.UserDTO;
import br.com.breno_barbosa1.basic_ecommerce.exceptions.RequiredObjectIsNullException;
import br.com.breno_barbosa1.basic_ecommerce.exceptions.ResourceNotFoundException;
import br.com.breno_barbosa1.basic_ecommerce.model.User;
import br.com.breno_barbosa1.basic_ecommerce.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static br.com.breno_barbosa1.basic_ecommerce.mapper.ObjectMapper.parseListObjects;
import static br.com.breno_barbosa1.basic_ecommerce.mapper.ObjectMapper.parseObject;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class UserService {

    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepository repository;

    public List<UserDTO> findAll() {
        logger.info("Finding all users!");

        var users = parseListObjects(repository.findAll(), UserDTO.class);
        users.forEach(this::addHateoasLinks);
        return users;
    }

    public UserDTO findById(Long id) {
        logger.info("Find by id!");

        var entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        var dto = parseObject(entity, UserDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public List<UserDTO> findByEmail(String email) {
        logger.info("Find by email!");

        String emailQuery = "%" + email + "%";

        var users = parseListObjects(repository.findByEmail(emailQuery), UserDTO.class);
        users.forEach(this::addHateoasLinks);
        return users;
    }

    public UserDTO create(UserDTO user) {
        if (user == null) throw new RequiredObjectIsNullException();

        logger.info("Creating an user!");

        var entity = parseObject(user, User.class);

        var dto = parseObject(repository.save(entity), UserDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public UserDTO update(@RequestBody UserDTO user) {
        if (user == null) throw new RequiredObjectIsNullException();

        logger.info("Updating an user!");

        User entity = repository.findById(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        entity.setEmail(user.getEmail());
        entity.setAddress(user.getAddress());

        var dto = parseObject(repository.save(entity), UserDTO.class);
        addHateoasLinks(dto);
        return dto;
    }

    public void delete(Long id) {
        logger.info("Deleting one User!");

        User entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        repository.delete(entity);
    }

    private void addHateoasLinks(UserDTO dto) {
        dto.add(linkTo(methodOn(UserController.class).findById(dto.getId())).withSelfRel().withType("GET"));
        dto.add(linkTo(methodOn(UserController.class).findAll()).withRel("findAll").withType("GET"));
        dto.add(linkTo(methodOn(UserController.class).create(dto)).withRel("create").withType("POST"));
        dto.add(linkTo(methodOn(UserController.class).update(dto)).withRel("update").withType("PUT"));
        dto.add(linkTo(methodOn(UserController.class).delete(dto.getId())).withRel("delete").withType("DELETE"));
    }
}