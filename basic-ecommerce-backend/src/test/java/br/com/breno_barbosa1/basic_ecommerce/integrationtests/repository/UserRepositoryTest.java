package br.com.breno_barbosa1.basic_ecommerce.integrationtests.repository;

import br.com.breno_barbosa1.basic_ecommerce.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.breno_barbosa1.basic_ecommerce.model.User;
import br.com.breno_barbosa1.basic_ecommerce.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @Test
    public void createUserAndFindByIdTest() {
        User newUser = new User();

        newUser.setEmail("daniel@gmail.com");
        newUser.setPassword("12345");
        newUser.setAddress("San Francisco - USA");
        newUser.setCreatedDate(LocalDateTime.now());

        var createdUser = userRepository.save(newUser);

        Optional<User> retrievedUser = userRepository.findById(createdUser.getId());

        assertNotNull(retrievedUser);
        assertTrue(retrievedUser.get().getId() > 0);
        assertEquals("daniel@gmail.com", retrievedUser.get().getEmail());
        assertEquals("San Francisco - USA", retrievedUser.get().getAddress());
        assertEquals("12345", retrievedUser.get().getPassword());
    }
}