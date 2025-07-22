package com.carhub.ui.components;

import javax.swing.*;
import java.awt.*;

public class MetricCard extends JPanel {

    private JLabel titleLabel;
    private JLabel valueLabel;
    private JLabel subtitleLabel;
    private boolean isPositive;

    public MetricCard(String title, String value, String subtitle, boolean isPositive) {
        this.isPositive = isPositive;

        setupCard();
        createComponents(title, value, subtitle);
    }

    private void setupCard() {
        setBackground(new Color(42, 45, 53));
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12)); // Further reduced padding
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 80)); // Reduced maximum height
        setPreferredSize(new Dimension(200, 80)); // Set a fixed preferred size
    }

    private void createComponents(String title, String value, String subtitle) {
        // Title
        // Title - even more compact
        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 11)); // Smaller font
        titleLabel.setForeground(new Color(161, 161, 170));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Value - more compact
        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SF Pro Display", Font.BOLD, 20)); // Smaller font
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Subtitle - more compact
        subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 10)); // Smaller font
        subtitleLabel.setForeground(isPositive ? new Color(34, 197, 94) : new Color(239, 68, 68));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Add components with minimal spacing
        add(titleLabel);
        add(Box.createVerticalStrut(2)); // Minimal spacing
        add(valueLabel);
        add(Box.createVerticalStrut(1)); // Minimal spacing
        add(subtitleLabel);
    }

    public void updateValue(String newValue) {
        valueLabel.setText(newValue);
        repaint();
    }

    public void updateTitle(String newTitle) {
        titleLabel.setText(newTitle);
        repaint();
    }

    public void updateSubtitle(String newSubtitle) {
        subtitleLabel.setText(newSubtitle);
        repaint();
    }

    public void setPositive(boolean positive) {
        this.isPositive = positive;
        subtitleLabel.setForeground(positive ? new Color(34, 197, 94) : new Color(239, 68, 68));
        repaint();
    }
}
