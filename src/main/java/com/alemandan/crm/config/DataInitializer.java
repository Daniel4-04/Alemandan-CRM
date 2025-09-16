package com.alemandan.crm.config;

import com.alemandan.crm.model.AppRole;
import com.alemandan.crm.model.AppUser;
import com.alemandan.crm.repository.AppRoleRepository;
import com.alemandan.crm.repository.AppUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    @Transactional
    CommandLineRunner initData(AppRoleRepository roleRepo,
                               AppUserRepository userRepo,
                               PasswordEncoder encoder) {
        return args -> {
            // Roles
            AppRole adminRole = roleRepo.findByName("ADMIN").orElseGet(() -> roleRepo.save(new AppRole("ADMIN")));
            AppRole employeeRole = roleRepo.findByName("EMPLOYEE").orElseGet(() -> roleRepo.save(new AppRole("EMPLOYEE")));

            // Usuarios (solo si no existen)
            if (!userRepo.existsByUsername("admin")) {
                AppUser admin = new AppUser("admin", encoder.encode("admin123"), true);
                admin.setRoles(Set.of(adminRole, employeeRole));
                userRepo.save(admin);
            }

            if (!userRepo.existsByUsername("empleado")) {
                AppUser empleado = new AppUser("empleado", encoder.encode("empleado123"), true);
                empleado.setRoles(Set.of(employeeRole));
                userRepo.save(empleado);
            }
        };
    }
}