package com.carhub.ui.components;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;

public class ModernPasswordField extends JPasswordField {
    
    private static final Color BACKGROUND_COLOR = new Color(47, 51, 73);
    private static final Color BORDER_COLOR = new Color(55, 65, 81);
    private static final Color FOCUS_BORDER_COLOR = new Color(222, 255, 41);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color PLACEHOLDER_COLOR = new Color(161, 161, 170);
    
    private String placeholder;
    private boolean isFocused = false;
    
    public ModernPasswordField() {
        initializePasswordField();
    }
    
    public ModernPasswordField(String placeholder) {
        this.placeholder = placeholder;
        initializePasswordField();
    }
    
    private void initializePasswordField() {
        setBackground(BACKGROUND_COLOR);
        setForeground(TEXT_COLOR);
        setCaretColor(TEXT_COLOR);
        setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        setBorder(new ModernPasswordFieldBorder());
        
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                isFocused = true;
                repaint();
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                isFocused = false;
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (placeholder != null && getPassword().length == 0 && !isFocused) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(PLACEHOLDER_COLOR);
            g2d.setFont(getFont());
            
            FontMetrics fm = g2d.getFontMetrics();
            int x = getInsets().left;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            
            g2d.drawString(placeholder, x, y);
            g2d.dispose();
        }
    }
    
    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        return new Dimension(size.width, Math.max(size.height, 36));
    }
    
    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }
    
    private class ModernPasswordFieldBorder extends AbstractBorder {
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Color borderColor = isFocused ? FOCUS_BORDER_COLOR : BORDER_COLOR;
            g2d.setColor(borderColor);
            g2d.setStroke(new BasicStroke(isFocused ? 2f : 1f));
            g2d.drawRoundRect(x, y, width - 1, height - 1, 8, 8);
            
            g2d.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 12, 8, 12);
        }
    }
}
