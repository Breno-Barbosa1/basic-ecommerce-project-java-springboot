package br.com.breno_barbosa1.basic_ecommerce.repository;

import br.com.breno_barbosa1.basic_ecommerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
