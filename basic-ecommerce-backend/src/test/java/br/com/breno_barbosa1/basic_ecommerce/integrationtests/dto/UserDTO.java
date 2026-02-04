package br.com.breno_barbosa1.basic_ecommerce.integrationtests.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@XmlRootElement
@JsonIgnoreProperties({"links"})
public class UserDTO extends RepresentationModel<UserDTO> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String email;
    private String password;
    private String address;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        UserDTO dto = (UserDTO) o;
        return Objects.equals(getId(), dto.getId()) && Objects.equals(getEmail(), dto.getEmail()) && Objects.equals(getPassword(), dto.getPassword()) && Objects.equals(getAddress(), dto.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getEmail(), getPassword(), getAddress());
    }
}