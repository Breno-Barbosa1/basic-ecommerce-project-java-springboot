package br.com.breno_barbosa1.basic_ecommerce.integrationtests.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class OrderItemRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long productId;
    private Integer quantity;

    public OrderItemRequestDTO() {}

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemRequestDTO that = (OrderItemRequestDTO) o;
        return Objects.equals(getProductId(), that.getProductId()) && Objects.equals(getQuantity(), that.getQuantity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProductId(), getQuantity());
    }
}