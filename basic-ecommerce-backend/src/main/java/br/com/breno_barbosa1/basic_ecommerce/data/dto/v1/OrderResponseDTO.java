package br.com.breno_barbosa1.basic_ecommerce.data.dto.v1;

import org.springframework.hateoas.RepresentationModel;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class OrderResponseDTO extends RepresentationModel<OrderResponseDTO>  implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long userId;
    private String email;
    private LocalDateTime createdDate;
    private Double total;
    private List<OrderItemResponseDTO> items;

    public OrderResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public List<OrderItemResponseDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponseDTO> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OrderResponseDTO that = (OrderResponseDTO) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getUserId(), that.getUserId()) && Objects.equals(getEmail(), that.getEmail()) && Objects.equals(getCreatedDate(), that.getCreatedDate()) && Objects.equals(getTotal(), that.getTotal()) && Objects.equals(getItems(), that.getItems());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getId(), getUserId(), getEmail(), getCreatedDate(), getTotal(), getItems());
    }
}
