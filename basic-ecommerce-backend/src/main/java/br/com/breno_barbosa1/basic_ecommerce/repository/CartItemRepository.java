package br.com.breno_barbosa1.basic_ecommerce.repository;

import br.com.breno_barbosa1.basic_ecommerce.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem,Long> {
}
