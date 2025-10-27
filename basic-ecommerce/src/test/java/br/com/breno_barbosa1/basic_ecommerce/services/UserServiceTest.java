package br.com.breno_barbosa1.basic_ecommerce.services;

import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.UserDTO;
import br.com.breno_barbosa1.basic_ecommerce.model.User;
import br.com.breno_barbosa1.basic_ecommerce.repository.UserRepository;
import br.com.breno_barbosa1.basic_ecommerce.unittests.mapper.mocks.MockUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    MockUser input;

    @InjectMocks
    private UserService service;

    @Mock
    UserRepository repository;

    @BeforeEach
    void setUp() {
        input = new MockUser();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findById() {
        User user = input.mockEntity(1);

        user.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(user));

        var result = service.findById(1L);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getEmail());
        assertNotNull(result.getAddress());

        assertEquals("Email Test1", result.getEmail());
        assertEquals("Address Test1", result.getAddress());
    }

    @Test
    void create() {
        User user = input.mockEntity(1);
        User persisted = user;
        persisted.setId(1L);

        UserDTO dto = input.MockDTO(1);

        when(repository.save(user)).thenReturn(persisted);

        var result = service.create(dto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getEmail());
        assertNotNull(result.getAddress());

        assertEquals("Email Test1", result.getEmail());
        assertEquals("Address Test1", result.getAddress());
    }

    @Test
    void update() {
        User user = input.mockEntity(1);
        User persisted = user;
        persisted.setId(1L);

        UserDTO dto = new UserDTO();
        dto.setId(1L);
        dto.setEmail("Email Test2");
        dto.setAddress("Address Test2");

        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.save(user)).thenReturn(persisted);

        var result = service.update(dto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getEmail());
        assertNotNull(result.getAddress());

        assertEquals("Email Test2", result.getEmail());
        assertEquals("Address Test2", result.getAddress());
    }

    @Test
    void delete() {
        User user = input.mockEntity(1);
        user.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(user));

        service.delete(user.getId());

        verify(repository, times(1)).findById(anyLong());
        verify(repository, times(1)).delete(any(User.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findAll() {
        List<User> usersList = input.mockEntityList();

        when(repository.findAll()).thenReturn(usersList);

        List<UserDTO> dtoList = input.mockDTOList();

        var result = service.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(usersList.size(), dtoList.size());

        UserDTO firstUser = dtoList.getFirst();

        assertEquals(1, firstUser.getId());
        assertEquals("Email Test1", firstUser.getEmail());
        assertEquals("Address Test1", firstUser.getAddress());

        UserDTO fourthUser = dtoList.get(3);

        assertEquals(4, fourthUser.getId());
        assertEquals("Email Test4", fourthUser.getEmail());
        assertEquals("Address Test4", fourthUser.getAddress());
    }
}