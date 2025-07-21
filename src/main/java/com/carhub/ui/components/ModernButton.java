package com.carhub.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernButton extends JButton {
    
    private static final Color ACCENT_COLOR = new Color(222, 255, 41);
    private static final Color HOVER_COLOR = new Color(222, 255, 41, 180);
    private static final Color TEXT_COLOR = new Color(26, 28, 32);
    private boolean isHovered = false;
    
    public ModernButton(String text) {
        super(text);
        initializeButton();
    }
    
    public ModernButton(String text, Icon icon) {
        super(text, icon);
        initializeButton();
    }
    
    private void initializeButton() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(new Font("SF Pro Display", Font.PLAIN, 14));
        setForeground(TEXT_COLOR);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Color bgColor = isHovered ? HOVER_COLOR : ACCENT_COLOR;
        g2d.setColor(bgColor);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
        
        super.paintComponent(g2d);
        g2d.dispose();
    }
    
    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        return new Dimension(size.width + 24, Math.max(size.height + 12, 36));
    }
}
