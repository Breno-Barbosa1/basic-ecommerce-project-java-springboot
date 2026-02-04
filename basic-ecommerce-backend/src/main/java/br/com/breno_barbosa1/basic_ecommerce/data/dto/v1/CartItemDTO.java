package br.com.breno_barbosa1.basic_ecommerce.data.dto.v1;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class CartItemDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private ProductDTO productDTO;
    private Integer quantity;

    public CartItemDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductDTO getProductDTO() {
        return productDTO;
    }

    public void setProductDTO(ProductDTO productDTO) {
        this.productDTO = productDTO;
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
        CartItemDTO that = (CartItemDTO) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getProductDTO(), that.getProductDTO()) && Objects.equals(getQuantity(), that.getQuantity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getProductDTO(), getQuantity());
    }
}