package com.dilaraalk.common.config;

import com.dilaraalk.category.entity.Category;
import com.dilaraalk.category.repository.CategoryRepository;
import com.dilaraalk.product.entity.Product;
import com.dilaraalk.product.entity.ProductImage;
import com.dilaraalk.product.repository.ProductRepository;
import com.dilaraalk.user.entity.User;
import com.dilaraalk.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.count() > 0) {
            log.info("Veritabanı zaten dolu, örnek veri eklenmiyor.");
            return;
        }

        log.info("Veritabanı boş, örnek veriler ekleniyor...");

        // 1. Kullanıcılar
        User admin = User.builder()
                .userName("admin")
                .email("admin@example.com")
                .password(passwordEncoder.encode("password123"))
                .roles(Arrays.asList("ADMIN", "USER"))
                .build();

        User user = User.builder()
                .userName("user")
                .email("user@example.com")
                .password(passwordEncoder.encode("password123"))
                .roles(Collections.singletonList("USER"))
                .build();

        userRepository.saveAll(Arrays.asList(admin, user));
        log.info("Örnek kullanıcılar eklendi: admin, user");

        // 2. Kategoriler
        Category elektronik = Category.builder()
                .name("Elektronik")
                .slug("elektronik")
                .build();

        Category giyim = Category.builder()
                .name("Giyim")
                .slug("giyim")
                .build();

        categoryRepository.saveAll(Arrays.asList(elektronik, giyim));
        log.info("Örnek kategoriler eklendi: Elektronik, Giyim");

        // 3. Ürünler
        createProduct("iPhone 14", "En yeni iPhone modeli.", new BigDecimal("35000"), 50, "iphone-14", elektronik,
                "https://images.unsplash.com/photo-1678685888221-cda773a3dcdb?auto=format&fit=crop&w=500&q=80");
        createProduct("MacBook Pro M2", "Güçlü performans.", new BigDecimal("45000"), 30, "macbook-pro-m2", elektronik,
                "https://images.unsplash.com/photo-1517336714731-489689fd1ca4?auto=format&fit=crop&w=500&q=80");
        createProduct("Basic T-Shirt", "Pamuklu rahat tişört.", new BigDecimal("250"), 100, "basic-t-shirt", giyim,
                "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?auto=format&fit=crop&w=500&q=80");

        log.info("Örnek ürünler eklendi.");
        log.info("Veritabanı başlangıç verileriyle dolduruldu. ✅");
    }

    private void createProduct(String name, String description, BigDecimal price, int stock, String slug,
            Category category, String imageUrl) {
        Product product = Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .stock(stock)
                .slug(slug)
                .categories(new HashSet<>(Collections.singletonList(category)))
                .build();

        if (imageUrl != null) {
            ProductImage image = ProductImage.builder()
                    .imageUrl(imageUrl)
                    .product(product)
                    .build();
            product.setImages(new HashSet<>(Collections.singletonList(image)));
        }

        productRepository.save(product);
    }
}
