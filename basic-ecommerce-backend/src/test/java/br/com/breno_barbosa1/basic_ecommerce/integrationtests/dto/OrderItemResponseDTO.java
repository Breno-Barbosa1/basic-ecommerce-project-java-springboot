package br.com.breno_barbosa1.basic_ecommerce.integrationtests.dto;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@XmlRootElement
public class OrderItemResponseDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Double priceAtPurchase;
    private Double total;

    public OrderItemResponseDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPriceAtPurchase() {
        return priceAtPurchase;
    }

    public void setPriceAtPurchase(Double priceAtPurchase) {
        this.priceAtPurchase = priceAtPurchase;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrderItemResponseDTO that = (OrderItemResponseDTO) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getProductId(), that.getProductId()) && Objects.equals(getProductName(), that.getProductName()) && Objects.equals(getQuantity(), that.getQuantity()) && Objects.equals(getPriceAtPurchase(), that.getPriceAtPurchase()) && Objects.equals(getTotal(), that.getTotal());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getProductId(), getProductName(), getQuantity(), getPriceAtPurchase(), getTotal());
    }
}