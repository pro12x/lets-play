package com.cgl.lets_play.config;

import com.cgl.lets_play.model.Product;
import com.cgl.lets_play.model.User;
import com.cgl.lets_play.repository.ProductRepository;
import com.cgl.lets_play.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    @Profile("!test")
    public CommandLineRunner initializeData() {
        return args -> {
            if (userRepository.count() == 0) {
                log.info("Initializing database with sample data");

                User admin = User.builder()
                        .name("Admin User")
                        .email("admin@example.com")
                        .password(passwordEncoder.encode("admin@123"))
                        .role("ROLE_ADMIN")
                        .build();
                userRepository.save(admin);

                User user1 = User.builder()
                        .name("Regular User 1")
                        .email("user1@example.com")
                        .password(passwordEncoder.encode("user@123"))
                        .role("ROLE_USER")
                        .build();
                userRepository.save(user1);

                User user2 = User.builder()
                        .name("Regular User 2")
                        .email("user2@example.com")
                        .password(passwordEncoder.encode("user@123"))
                        .role("ROLE_USER")
                        .build();
                userRepository.save(user2);

                Product product1 = Product.builder()
                        .name("Dell Alienware X17 R2 14th Gen")
                        .description("High-performance gaming laptop with Intel Core i9 processor and NVIDIA GeForce RTX 4070 Ti graphics card.")
                        .price(2999d)
                        .userId(admin.getId())
                        .build();
                productRepository.save(product1);

                Product product2 = Product.builder()
                        .name("Apple MacBook Pro 16-inch")
                        .description("Powerful laptop with M1 Max chip, 64GB RAM, and 8TB SSD.")
                        .price(6499d)
                        .userId(user1.getId())
                        .build();
                productRepository.save(product2);

                log.info("Initialized database with sample data");
            } else {
                log.info("Database already contains data, skipping initialization");
            }
        };
    }
}
