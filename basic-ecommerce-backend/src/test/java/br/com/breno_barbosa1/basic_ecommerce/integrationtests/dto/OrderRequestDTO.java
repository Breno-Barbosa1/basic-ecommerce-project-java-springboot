package br.com.breno_barbosa1.basic_ecommerce.integrationtests.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@JacksonXmlRootElement(localName = "orderRequestDTO")
public class OrderRequestDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String email;

    @JacksonXmlElementWrapper(localName = "items")
    @JacksonXmlProperty(localName = "items")
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