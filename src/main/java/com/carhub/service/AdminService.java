package com.carhub.service;

import com.carhub.entity.Admin;
import com.carhub.repository.AdminRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AdminService {
    
    @Autowired
    private AdminRepository adminRepository;
    
    public Optional<Admin> authenticate(String username, String password) {
        Optional<Admin> adminOpt = adminRepository.findByUsername(username);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            if (admin.getIsActive() && BCrypt.checkpw(password, admin.getPasswordHash())) {
                admin.setLastLogin(LocalDateTime.now());
                adminRepository.save(admin);
                return Optional.of(admin);
            }
        }
        return Optional.empty();
    }
    
    public Admin createAdmin(String username, String email, String password, String fullName, Admin.Role role) {
        if (adminRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (adminRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPasswordHash(BCrypt.hashpw(password, BCrypt.gensalt()));
        admin.setFullName(fullName);
        admin.setRole(role);
        
        return adminRepository.save(admin);
    }
    
    public Admin updateAdmin(Admin admin) {
        return adminRepository.save(admin);
    }
    
    public void changePassword(Long adminId, String newPassword) {
        Optional<Admin> adminOpt = adminRepository.findById(adminId);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            admin.setPasswordHash(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
            adminRepository.save(admin);
        }
    }
    
    public List<Admin> getAllActiveAdmins() {
        return adminRepository.findActiveAdminsOrderByName();
    }
    
    public Optional<Admin> findById(Long id) {
        return adminRepository.findById(id);
    }
    
    public Optional<Admin> findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }
    
    public void deactivateAdmin(Long adminId) {
        Optional<Admin> adminOpt = adminRepository.findById(adminId);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            admin.setIsActive(false);
            adminRepository.save(admin);
        }
    }
    
    public void activateAdmin(Long adminId) {
        Optional<Admin> adminOpt = adminRepository.findById(adminId);
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            admin.setIsActive(true);
            adminRepository.save(admin);
        }
    }
    
    public boolean isUsernameAvailable(String username) {
        return !adminRepository.existsByUsername(username);
    }
    
    public boolean isEmailAvailable(String email) {
        return !adminRepository.existsByEmail(email);
    }
}
