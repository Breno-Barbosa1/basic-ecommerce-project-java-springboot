package br.com.breno_barbosa1.basic_ecommerce.repository;

import br.com.breno_barbosa1.basic_ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
