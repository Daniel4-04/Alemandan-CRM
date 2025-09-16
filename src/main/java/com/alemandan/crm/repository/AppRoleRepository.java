package com.alemandan.crm.repository;

import com.alemandan.crm.model.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppRoleRepository extends JpaRepository<AppRole, Long> {
    Optional<AppRole> findByName(String name);
    boolean existsByName(String name);
}