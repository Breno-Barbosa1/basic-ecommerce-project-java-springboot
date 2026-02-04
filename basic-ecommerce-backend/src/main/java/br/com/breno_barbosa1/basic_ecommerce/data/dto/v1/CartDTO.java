package br.com.breno_barbosa1.basic_ecommerce.data.dto.v1;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class CartDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String email;
    private List<CartItemDTO> items;

    public CartDTO() {
    }

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

    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CartDTO cartDTO = (CartDTO) o;
        return Objects.equals(getId(), cartDTO.getId()) && Objects.equals(getEmail(), cartDTO.getEmail()) && Objects.equals(getItems(), cartDTO.getItems());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getEmail(), getItems());
    }
}