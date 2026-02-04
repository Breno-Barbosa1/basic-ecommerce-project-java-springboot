package br.com.breno_barbosa1.basic_ecommerce.repository;

import br.com.breno_barbosa1.basic_ecommerce.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {

    Optional<Cart> findByUserEmail(String email);
}