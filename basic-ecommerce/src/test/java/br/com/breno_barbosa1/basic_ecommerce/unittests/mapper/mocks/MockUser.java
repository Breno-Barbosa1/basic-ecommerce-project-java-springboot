package br.com.breno_barbosa1.basic_ecommerce.unittests.mapper.mocks;

import br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.UserDTO;
import br.com.breno_barbosa1.basic_ecommerce.model.User;

import java.util.ArrayList;
import java.util.List;

public class MockUser {

    public List<User> mockEntityList() {
        List<User> mockEntityList = new ArrayList<>();

        for (int i = 1; i < 14; i++) {
            User user = mockEntity(i);
            mockEntityList.add(user);
        }

        return mockEntityList;
    }

    public List<UserDTO> mockDTOList() {
        List<UserDTO> mockDtoList = new ArrayList<>();

        for (int i = 1; i < 14; i++) {
            UserDTO user = MockDTO(i);
            mockDtoList.add(user);
        }

        return mockDtoList;
    }

    public User mockEntity(Integer number) {

        User user = new User();

        user.setId(number.longValue());
        user.setEmail("Email Test" + number);
        user.setAddress("Address Test" + number);

        return user;
    }

    public UserDTO MockDTO(Integer number) {

        UserDTO user = new UserDTO();

        user.setId(number.longValue());
        user.setEmail("Email Test" + number);
        user.setAddress("Address Test" + number);

        return user;
    }
}