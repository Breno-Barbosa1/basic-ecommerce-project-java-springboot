package br.com.breno_barbosa1.basic_ecommerce.integrationtests.repository;

import br.com.breno_barbosa1.basic_ecommerce.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.breno_barbosa1.basic_ecommerce.model.Order;
import br.com.breno_barbosa1.basic_ecommerce.model.OrderItem;
import br.com.breno_barbosa1.basic_ecommerce.model.Product;
import br.com.breno_barbosa1.basic_ecommerce.model.User;
import br.com.breno_barbosa1.basic_ecommerce.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderRepositoryTest extends AbstractIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void createOrderAndFindById() {

        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        userRepository.deleteAll();
        productRepository.deleteAll();

        userRepository.flush();

        User user = new User();
        user.setEmail("breno@gmail.com");
        user.setPassword("password123");
        user.setAddress("Campina Grande - Brazil");
        user.setCreatedDate(LocalDateTime.now());
        userRepository.saveAndFlush(user);

        Product product = new Product();
        product.setName("Intel Pc");
        product.setDescription("Nvidia Intel Pc");
        product.setPrice(500.0);
        product.setStockQuantity(10);
        productRepository.save(product);

        User savedUser = userRepository.findByEmail("breno@gmail.com");

        Order order = new Order();
        order.setUser(savedUser);

        OrderItem orderItem = new OrderItem(2, product.getPrice(), order, product);

        order.setItems(List.of(orderItem));
        order.setCreatedDate(LocalDateTime.now());
        order.setTotal(orderItem.getQuantity() * orderItem.getProduct().getPrice());
        orderRepository.save(order);

        assertNotNull(order);
        assertNotNull(order.getId());
        assertEquals(1000.0, order.getTotal());
        assertEquals("breno@gmail.com", order.getUser().getEmail());
        assertEquals("Intel Pc", order.getItems().getFirst().getProduct().getName());

        var foundOrder = orderRepository.findById(order.getId());

        assertNotNull(foundOrder);
        assertEquals(1000.0, foundOrder.get().getTotal());
        assertEquals("breno@gmail.com", foundOrder.get().getUser().getEmail());
        assertEquals("Intel Pc", foundOrder.get().getItems().getFirst().getProduct().getName());
    }
}