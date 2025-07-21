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
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    private void createComponents(String title, String value, String subtitle) {
        // Title
        titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(161, 161, 170));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Value
        valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SF Pro Display", Font.BOLD, 28));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Subtitle
        subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 12));
        subtitleLabel.setForeground(isPositive ? new Color(34, 197, 94) : new Color(239, 68, 68));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        add(titleLabel);
        add(Box.createVerticalStrut(8));
        add(valueLabel);
        add(Box.createVerticalStrut(4));
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
