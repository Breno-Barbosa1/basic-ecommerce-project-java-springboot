package br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.security;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class CredentialsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;

    public CredentialsDTO() {}

    public CredentialsDTO(String email, String password) {
        this.email = email;
        this.password = password;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CredentialsDTO that = (CredentialsDTO) o;
        return Objects.equals(getEmail(), that.getEmail()) && Objects.equals(getPassword(), that.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail(), getPassword());
    }
}