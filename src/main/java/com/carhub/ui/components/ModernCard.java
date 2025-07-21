package com.carhub.ui.components;

import javax.swing.*;
import java.awt.*;

public class ModernCard extends JPanel {
    
    private static final Color CARD_BACKGROUND = new Color(47, 51, 73, 200);
    private static final Color BORDER_COLOR = new Color(55, 65, 81, 100);
    private static final int CORNER_RADIUS = 12;
    
    public ModernCard() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
    }
    
    public ModernCard(LayoutManager layout) {
        super(layout);
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw card background with rounded corners
        g2d.setColor(CARD_BACKGROUND);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
        
        // Draw subtle border
        g2d.setColor(BORDER_COLOR);
        g2d.setStroke(new BasicStroke(1f));
        g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, CORNER_RADIUS, CORNER_RADIUS);
        
        g2d.dispose();
        super.paintComponent(g);
    }
}
