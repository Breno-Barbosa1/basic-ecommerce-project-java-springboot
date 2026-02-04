package br.com.breno_barbosa1.basic_ecommerce.data.dto.v1;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class CartUpdateRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String email;
    private CartItemDTO item;

    public CartUpdateRequestDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CartItemDTO getItem() {
        return item;
    }

    public void setItem(CartItemDTO item) {
        this.item = item;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CartUpdateRequestDTO that = (CartUpdateRequestDTO) o;
        return Objects.equals(getEmail(), that.getEmail()) && Objects.equals(getItem(), that.getItem());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail(), getItem());
    }
}