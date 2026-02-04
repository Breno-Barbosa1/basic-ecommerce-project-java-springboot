package br.com.breno_barbosa1.basic_ecommerce.data.dto.v1.security;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

public class TokenDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String email;
    private Boolean authenticated;
    private Date created;
    private Date expiration;
    private String accessToken;
    private String refreshToken;

    public TokenDTO() {}

    public TokenDTO(String email, Boolean authenticated, Date created,
            Date expiration, String accessToken, String refreshToken) {
        this.email = email;
        this.authenticated = authenticated;
        this.created = created;
        this.expiration = expiration;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(Boolean authenticated) {
        this.authenticated = authenticated;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        TokenDTO tokenDTO = (TokenDTO) o;
        return Objects.equals(getEmail(), tokenDTO.getEmail()) && Objects.equals(getAuthenticated(), tokenDTO.getAuthenticated()) && Objects.equals(getCreated(), tokenDTO.getCreated()) && Objects.equals(getExpiration(), tokenDTO.getExpiration()) && Objects.equals(getAccessToken(), tokenDTO.getAccessToken()) && Objects.equals(getRefreshToken(), tokenDTO.getRefreshToken());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail(), getAuthenticated(), getCreated(), getExpiration(), getAccessToken(), getRefreshToken());
    }
}