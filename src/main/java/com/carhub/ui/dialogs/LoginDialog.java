package com.carhub.ui.dialogs;

import com.carhub.entity.Admin;
import com.carhub.service.AdminService;
import com.carhub.ui.components.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Optional;

public class LoginDialog extends JDialog {

    private AdminService adminService;
    private Admin authenticatedAdmin;
    private boolean loginSuccessful = false;

    private ModernTextField usernameField;
    private ModernPasswordField passwordField;
    private ModernButton loginButton;
    private ModernButton cancelButton;

    public LoginDialog(Window parent, AdminService adminService) {
        super(parent, "CarHub Login", ModalityType.APPLICATION_MODAL);
        this.adminService = adminService;

        setupDialog();
        createComponents();
    }

    private void setupDialog() {
        setSize(450, 450); // Increased height to accommodate all elements
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(new Color(26, 28, 32));
    }

    private void createComponents() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(42, 45, 53));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 16, 24));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("CarHub");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 32));
        titleLabel.setForeground(new Color(222, 255, 41));
        titleLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Car Dealership Management System");
        subtitleLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(161, 161, 170));
        subtitleLabel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(subtitleLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(26, 28, 32));
        formPanel.setBorder(BorderFactory.createEmptyBorder(16, 40, 16, 40)); // Adjusted padding

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 16, 4); // Reduced vertical insets between fields
        gbc.anchor = GridBagConstraints.WEST;

        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("SF Pro Text", Font.BOLD, 14));
        formPanel.add(usernameLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        usernameField = new ModernTextField("Enter username");
        usernameField.setPreferredSize(new Dimension(250, 36));
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        passwordLabel.setFont(new Font("SF Pro Text", Font.BOLD, 14));
        formPanel.add(passwordLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        passwordField = new ModernPasswordField("Enter password");
        passwordField.setPreferredSize(new Dimension(250, 36));
        formPanel.add(passwordField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        buttonPanel.setBackground(new Color(42, 45, 53));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(16, 24, 32, 24)); // Increased bottom padding

        cancelButton = new ModernButton("Cancel");
        loginButton = new ModernButton("Login");

        buttonPanel.add(cancelButton);
        buttonPanel.add(loginButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Event listeners
        setupEventListeners();

        // Set default values for testing
        usernameField.setText("admin");
        passwordField.setText("password");
    }

    private void setupEventListeners() {
        loginButton.addActionListener(e -> performLogin());
        cancelButton.addActionListener(e -> {
            loginSuccessful = false;
            dispose();
        });

        // Enter key to login
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };

        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty()) {
            showError("Please enter username");
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showError("Please enter password");
            passwordField.requestFocus();
            return;
        }

        // Disable login button during authentication
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");

        // Perform authentication in background thread
        SwingWorker<Optional<Admin>, Void> worker = new SwingWorker<Optional<Admin>, Void>() {
            @Override
            protected Optional<Admin> doInBackground() throws Exception {
                return adminService.authenticate(username, password);
            }

            @Override
            protected void done() {
                try {
                    Optional<Admin> adminOpt = get();
                    if (adminOpt.isPresent()) {
                        authenticatedAdmin = adminOpt.get();
                        loginSuccessful = true;
                        dispose();
                    } else {
                        showError("Invalid username or password");
                        passwordField.setText("");
                        passwordField.requestFocus();
                    }
                } catch (Exception e) {
                    showError("Login failed: " + e.getMessage());
                } finally {
                    loginButton.setEnabled(true);
                    loginButton.setText("Login");
                }
            }
        };

        worker.execute();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Login Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isLoginSuccessful() {
        return loginSuccessful;
    }

    public Admin getAuthenticatedAdmin() {
        return authenticatedAdmin;
    }
}
