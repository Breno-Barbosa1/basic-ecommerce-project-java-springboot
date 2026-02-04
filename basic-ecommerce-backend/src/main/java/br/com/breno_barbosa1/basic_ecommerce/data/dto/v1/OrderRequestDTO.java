package br.com.breno_barbosa1.basic_ecommerce.data.dto.v1;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class OrderRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String email;
    private List<OrderItemRequestDTO> items;

    public OrderRequestDTO() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<OrderItemRequestDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequestDTO> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderRequestDTO that = (OrderRequestDTO) o;
        return Objects.equals(getEmail(), that.getEmail()) && Objects.equals(getItems(), that.getItems());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEmail(), getItems());
    }
}