package com.carhub;

import com.carhub.config.ApplicationConfig;
import com.carhub.entity.Admin;
import com.carhub.service.AdminService;
import com.carhub.service.SystemSettingService;
import com.carhub.ui.dialogs.LoginDialog;
import com.carhub.ui.main.MainWindow;
import com.formdev.flatlaf.FlatDarkLaf;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.swing.*;

public class CarHubApplication {
    
    public static void main(String[] args) {
        // Set system properties for better rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        System.setProperty("sun.java2d.xrender", "true");
        
        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize Spring context
                ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
                
                // Get services
                AdminService adminService = context.getBean(AdminService.class);
                SystemSettingService systemSettingService = context.getBean(SystemSettingService.class);
                
                // Initialize default settings
                systemSettingService.initializeDefaultSettings();
                
                // Create default admin if none exists
                createDefaultAdminIfNeeded(adminService);
                
                // Show login dialog
                LoginDialog loginDialog = new LoginDialog(null, adminService);
                loginDialog.setVisible(true);
                
                if (loginDialog.isLoginSuccessful()) {
                    Admin authenticatedAdmin = loginDialog.getAuthenticatedAdmin();
                    
                    // Create and show main window
                    MainWindow mainWindow = context.getBean(MainWindow.class);
                    mainWindow.initialize(authenticatedAdmin);
                } else {
                    System.exit(0);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Failed to start CarHub application:\n" + e.getMessage(),
                    "Startup Error", 
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
    
    private static void createDefaultAdminIfNeeded(AdminService adminService) {
        try {
            // Check if any admin exists
            if (adminService.getAllActiveAdmins().isEmpty()) {
                // Create default admin
                adminService.createAdmin(
                    "admin", 
                    "admin@carhub.com", 
                    "password", 
                    "System Administrator", 
                    Admin.Role.SUPER_ADMIN
                );
                System.out.println("Default admin created: username=admin, password=password");
            }
        } catch (Exception e) {
            System.err.println("Error creating default admin: " + e.getMessage());
        }
    }
}
