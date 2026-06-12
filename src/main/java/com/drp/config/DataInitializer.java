package com.drp.config;

import com.drp.entity.Category;
import com.drp.entity.Role;
import com.drp.entity.User;
import com.drp.repository.CategoryRepository;
import com.drp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${drp.seed.admin.username}")
    private String adminUsername;

    @Value("${drp.seed.admin.password}")
    private String adminPassword;

    @Value("${drp.seed.admin.email}")
    private String adminEmail;

    public DataInitializer(
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdminUser();
        seedDefaultCategories();
    }

    private void seedAdminUser() {
        if (!userRepository.existsByUsername(adminUsername)) {
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setActive(true);
            userRepository.save(admin);
        }
    }

    private void seedDefaultCategories() {
        createCategoryIfMissing("Research Papers", "Academic and research documents");
        createCategoryIfMissing("Reports", "Organizational and project reports");
        createCategoryIfMissing("Presentations", "Slides and presentation files");
        createCategoryIfMissing("General Documents", "Miscellaneous digital files");
    }

    private void createCategoryIfMissing(String name, String description) {
        if (!categoryRepository.existsByNameIgnoreCase(name)) {
            Category category = new Category();
            category.setName(name);
            category.setDescription(description);
            categoryRepository.save(category);
        }
    }
}
