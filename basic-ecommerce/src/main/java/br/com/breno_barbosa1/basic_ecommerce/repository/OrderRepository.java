package br.com.breno_barbosa1.basic_ecommerce.repository;

import br.com.breno_barbosa1.basic_ecommerce.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // loads all fields correctly
    @Query("SELECT o FROM Order o JOIN FETCH o.user u JOIN FETCH o.items item JOIN FETCH item.product")
    List<Order> findAllOrdersWithDetails();

    @Query("SELECT o FROM Order o JOIN FETCH o.user u JOIN FETCH o.items item JOIN FETCH item.product WHERE o.id = :orderId")
    Optional<Order> findOrderByIdWithDetails(@Param("orderId") Long orderId);
}