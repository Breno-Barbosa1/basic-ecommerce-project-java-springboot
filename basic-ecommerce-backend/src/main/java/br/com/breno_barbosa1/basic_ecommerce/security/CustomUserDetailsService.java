package br.com.breno_barbosa1.basic_ecommerce.security;

import br.com.breno_barbosa1.basic_ecommerce.model.User;
import br.com.breno_barbosa1.basic_ecommerce.model.auth.SecurityUser;
import br.com.breno_barbosa1.basic_ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = repository.findByEmail(email);

        if (user == null) throw new UsernameNotFoundException("Email not found: " + email);

        return new SecurityUser((User) user);
    }
}