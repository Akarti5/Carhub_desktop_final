package com.carhub.ui.main;

import com.carhub.entity.Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NavigationPanel extends JPanel {

    private static final Color BACKGROUND_COLOR = new Color(42, 45, 53);
    private static final Color HOVER_COLOR = new Color(55, 65, 81);
    private static final Color ACTIVE_COLOR = new Color(222, 255, 41, 50);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color ACCENT_COLOR = new Color(222, 255, 41);

    private Admin currentAdmin;
    private ActionListener navigationListener;
    private JPanel selectedPanel;

    public NavigationPanel(Admin admin) {
        this.currentAdmin = admin;
        setupPanel();
        createComponents();
    }

    private void setupPanel() {
        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(280, 0));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(55, 65, 81)));
    }

    private void createComponents() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(16, 0, 16, 0));

        // Header section
        JPanel headerPanel = createHeaderPanel();
        contentPanel.add(headerPanel);
        contentPanel.add(Box.createVerticalStrut(24));

        // Navigation items
        contentPanel.add(createNavigationItem("Dashboard", "📊", "dashboard", true));
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(createNavigationItem("Cars", "🚗", "cars", false));
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(createNavigationItem("Sales", "💳", "sales", false));
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(createNavigationItem("Clients", "👥", "clients", false));
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(createNavigationItem("Reports", "📈", "reports", false));
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(createNavigationItem("Settings", "⚙️", "settings", false));

        // Add glue to push everything to the top
        contentPanel.add(Box.createVerticalGlue());

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BACKGROUND_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

        // App logo/title
        JLabel logoLabel = new JLabel("CarHub");
        logoLabel.setFont(new Font("SF Pro Display", Font.BOLD, 24));
        logoLabel.setForeground(ACCENT_COLOR);
        logoLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        // User profile section
        JPanel profilePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 8));
        profilePanel.setBackground(BACKGROUND_COLOR);
        profilePanel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);

        // Avatar (placeholder)
        JLabel avatarLabel = new JLabel("");
        avatarLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 20));
        avatarLabel.setPreferredSize(new Dimension(40, 40));
        avatarLabel.setHorizontalAlignment(SwingConstants.CENTER);
        avatarLabel.setBorder(BorderFactory.createLineBorder(new Color(55, 65, 81), 1));

        // User info
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setBackground(BACKGROUND_COLOR);

        JLabel nameLabel = new JLabel(currentAdmin.getFullName());
        nameLabel.setFont(new Font("SF Pro Text", Font.BOLD, 14));
        nameLabel.setForeground(TEXT_COLOR);

        JLabel roleLabel = new JLabel(currentAdmin.getRole().toString());
        roleLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 12));
        roleLabel.setForeground(new Color(161, 161, 170));

        userInfoPanel.add(nameLabel);
        userInfoPanel.add(roleLabel);

        profilePanel.add(avatarLabel);
        profilePanel.add(Box.createHorizontalStrut(12));
        profilePanel.add(userInfoPanel);

        headerPanel.add(logoLabel);
        headerPanel.add(Box.createVerticalStrut(16));
        headerPanel.add(profilePanel);

        return headerPanel;
    }

    private JPanel createNavigationItem(String text, String icon, String action, boolean isSelected) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBackground(isSelected ? ACTIVE_COLOR : BACKGROUND_COLOR);
        itemPanel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        itemPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("SF Pro Display", Font.PLAIN, 18));
        iconLabel.setPreferredSize(new Dimension(24, 24));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Text
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("SF Pro Text", Font.BOLD, 14));
        textLabel.setForeground(TEXT_COLOR);

        itemPanel.add(iconLabel, BorderLayout.WEST);
        itemPanel.add(Box.createHorizontalStrut(12), BorderLayout.CENTER);
        itemPanel.add(textLabel, BorderLayout.CENTER);

        // Mouse events
        itemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isSelected) {
                    itemPanel.setBackground(HOVER_COLOR);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isSelected) {
                    itemPanel.setBackground(BACKGROUND_COLOR);
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                selectNavigationItem(itemPanel);
                if (navigationListener != null) {
                    navigationListener.actionPerformed(new java.awt.event.ActionEvent(
                            itemPanel, java.awt.event.ActionEvent.ACTION_PERFORMED, action));
                }
            }
        });

        if (isSelected) {
            selectedPanel = itemPanel;
        }

        return itemPanel;
    }

    private void selectNavigationItem(JPanel selectedItem) {
        // Reset all navigation items
        Container parent = selectedItem.getParent();
        if (parent != null) {
            for (java.awt.Component component : parent.getComponents()) {
                if (component instanceof JPanel && component != selectedItem) {
                    component.setBackground(BACKGROUND_COLOR);
                }
            }
        }

        // Set selected item
        selectedItem.setBackground(ACTIVE_COLOR);
        selectedPanel = selectedItem;
        repaint();
    }

    public void setNavigationListener(ActionListener listener) {
        this.navigationListener = listener;
    }
}
