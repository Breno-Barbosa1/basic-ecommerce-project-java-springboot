package br.com.breno_barbosa1.basic_ecommerce.services;

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

@Service
public class UserService {

    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    UserRepository repository;

    public List<UserDTO> findAll() {

        logger.info("Finding all users!");

        return parseListObjects(repository.findAll(), UserDTO.class);
    }

    public UserDTO findById(Long id) {

        var entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        logger.info("Find by id!");

        return parseObject(entity, UserDTO.class);
    }

    public UserDTO create(UserDTO user) {

        if (user == null) throw new RequiredObjectIsNullException();

        logger.info("Creating an user!");

        var entity = parseObject(user, User.class);

        return parseObject(repository.save(entity), UserDTO.class);
    }

    public UserDTO update(@RequestBody UserDTO user) {

        if (user == null) throw new RequiredObjectIsNullException();

        logger.info("Updating an user!");

        User entity = repository.findById(user.getId())
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        entity.setEmail(user.getEmail());
        entity.setAddress(user.getAddress());

        return parseObject(repository.save(entity), UserDTO.class);

    }

    public void delete(Long id) {

        logger.info("Deleting one User!");

        User entity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        repository.delete(entity);
    }
}