package br.com.breno_barbosa1.basic_ecommerce.repository;

import br.com.breno_barbosa1.basic_ecommerce.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
