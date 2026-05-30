package com.houseplan.config;

import com.houseplan.model.User;
import com.houseplan.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@houseplan.local");
                admin.setPassword(encoder.encode("admin123"));
                admin.setRole("ADMIN");
                userRepository.save(admin);
            }
        };
    }
}
