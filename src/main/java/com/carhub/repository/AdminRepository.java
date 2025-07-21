package com.carhub.repository;

import com.carhub.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    
    Optional<Admin> findByUsername(String username);
    
    Optional<Admin> findByEmail(String email);
    
    List<Admin> findByIsActiveTrue();
    
    List<Admin> findByRole(Admin.Role role);
    
    @Query("SELECT a FROM Admin a WHERE a.isActive = true ORDER BY a.fullName")
    List<Admin> findActiveAdminsOrderByName();
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}
