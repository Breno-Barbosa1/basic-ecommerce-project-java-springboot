package br.com.breno_barbosa1.basic_ecommerce.data.dto.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@JsonIgnoreProperties({"links"})
public class UserDTO extends RepresentationModel<UserDTO> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String email;
    private String password;
    private String address;
    private LocalDateTime createdDate;

    public UserDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(getId(), userDTO.getId()) && Objects.equals(getEmail(), userDTO.getEmail()) && Objects.equals(getPassword(), userDTO.getPassword()) && Objects.equals(getAddress(), userDTO.getAddress()) && Objects.equals(getCreatedDate(), userDTO.getCreatedDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getEmail(), getPassword(), getAddress(), getCreatedDate());
    }
}