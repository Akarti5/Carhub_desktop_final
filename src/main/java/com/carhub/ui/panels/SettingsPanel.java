package com.carhub.ui.panels;

import com.carhub.entity.Admin;
import com.carhub.service.AdminService;
import com.carhub.service.SystemSettingService;
import com.carhub.ui.components.ModernButton;
import com.carhub.ui.main.MainWindow;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel implements MainWindow.RefreshablePanel {

    private SystemSettingService systemSettingService;
    private AdminService adminService;
    private Admin currentAdmin;

    public SettingsPanel(SystemSettingService systemSettingService, AdminService adminService, Admin currentAdmin) {
        this.systemSettingService = systemSettingService;
        this.adminService = adminService;
        this.currentAdmin = currentAdmin;

        setupPanel();
        createComponents();
    }

    private void setupPanel() {
        setBackground(new Color(26, 28, 32));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
    }

    private void createComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(24, 24));
        mainPanel.setOpaque(false);

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Application settings and preferences");
        subtitleLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(161, 161, 170));

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 16, 16));
        contentPanel.setOpaque(false);

        // User Settings Card
        JPanel userSettingsCard = createSettingsCard(
                "User Settings",
                "Manage user account and profile settings",
                "Manage Users",
                e -> manageUsers()
        );

        // System Settings Card
        JPanel systemSettingsCard = createSettingsCard(
                "System Settings",
                "Configure application settings and preferences",
                "System Config",
                e -> configureSystem()
        );

        // Database Settings Card
        JPanel databaseSettingsCard = createSettingsCard(
                "Database Settings",
                "Manage database connections and backup",
                "Database Config",
                e -> configureDatabase()
        );

        // About Card
        JPanel aboutCard = createSettingsCard(
                "About CarHub",
                "Application information and version details",
                "About",
                e -> showAbout()
        );

        contentPanel.add(userSettingsCard);
        contentPanel.add(systemSettingsCard);
        contentPanel.add(databaseSettingsCard);
        contentPanel.add(aboutCard);

        return contentPanel;
    }

    private JPanel createSettingsCard(String title, String description, String buttonText,
                                      java.awt.event.ActionListener action) {
        JPanel card = new JPanel();
        card.setBackground(new Color(42, 45, 53));
        card.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Description
        JTextArea descriptionArea = new JTextArea(description);
        descriptionArea.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        descriptionArea.setForeground(new Color(161, 161, 170));
        descriptionArea.setOpaque(false);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Button
        ModernButton actionButton = new ModernButton(buttonText);
        actionButton.addActionListener(action);
        actionButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(descriptionArea);
        card.add(Box.createVerticalGlue());
        card.add(actionButton);

        return card;
    }

    private void manageUsers() {
        JOptionPane.showMessageDialog(this,
                "User management will be implemented here.\nThis would include:\n" +
                        "- Add/Edit/Delete users\n" +
                        "- Manage user roles and permissions\n" +
                        "- Password management\n" +
                        "- User activity logs",
                "User Management",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void configureSystem() {
        JOptionPane.showMessageDialog(this,
                "System configuration will be implemented here.\nThis would include:\n" +
                        "- Company information\n" +
                        "- Tax rates and currency settings\n" +
                        "- Email configuration\n" +
                        "- Backup settings",
                "System Configuration",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void configureDatabase() {
        JOptionPane.showMessageDialog(this,
                "Database configuration will be implemented here.\nThis would include:\n" +
                        "- Database connection settings\n" +
                        "- Backup and restore\n" +
                        "- Data maintenance\n" +
                        "- Performance monitoring",
                "Database Configuration",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
                "CarHub - Car Dealership Management System\n\n" +
                        "Version: 1.0.0\n" +
                        "Built with Java Spring Framework\n" +
                        "Database: PostgreSQL\n" +
                        "UI Framework: Java Swing with FlatLaf\n\n" +
                        "© 2024 CarHub. All rights reserved.",
                "About CarHub",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void refresh() {
        // Settings panel doesn't need regular refresh
    }
}
