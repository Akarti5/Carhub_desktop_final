package com.carhub.ui.components;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ModernTextField extends JTextField {
    
    private static final Color BACKGROUND_COLOR = new Color(47, 51, 73);
    private static final Color BORDER_COLOR = new Color(55, 65, 81);
    private static final Color FOCUS_BORDER_COLOR = new Color(222, 255, 41);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color PLACEHOLDER_COLOR = new Color(161, 161, 170);
    
    private String placeholder;
    private boolean isFocused = false;
    
    public ModernTextField() {
        initializeTextField();
    }
    
    public ModernTextField(String placeholder) {
        this.placeholder = placeholder;
        initializeTextField();
    }
    
    public ModernTextField(int columns) {
        super(columns);
        initializeTextField();
    }
    
    private void initializeTextField() {
        setBackground(BACKGROUND_COLOR);
        setForeground(TEXT_COLOR);
        setCaretColor(TEXT_COLOR);
        setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        setBorder(new ModernTextFieldBorder());
        
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                isFocused = true;
                repaint();
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                isFocused = false;
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (placeholder != null && getText().isEmpty() && !isFocused) {
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
    
    private class ModernTextFieldBorder extends AbstractBorder {
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
