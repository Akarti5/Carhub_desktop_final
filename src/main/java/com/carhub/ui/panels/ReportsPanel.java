package com.carhub.ui.panels;

import com.carhub.service.CarService;
import com.carhub.service.ClientService;
import com.carhub.service.SaleService;
import com.carhub.ui.components.ModernButton;
import com.carhub.ui.main.MainWindow;

import javax.swing.*;
import java.awt.*;

public class ReportsPanel extends JPanel implements MainWindow.RefreshablePanel {

    private SaleService saleService;
    private CarService carService;
    private ClientService clientService;

    public ReportsPanel(SaleService saleService, CarService carService, ClientService clientService) {
        this.saleService = saleService;
        this.carService = carService;
        this.clientService = clientService;

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

        JLabel titleLabel = new JLabel("Reports & Analytics");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Generate and view business reports");
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

        // Sales Report Card
        JPanel salesReportCard = createReportCard(
                "Sales Report",
                "Generate detailed sales reports with filters",
                "Generate Sales Report",
                e -> generateSalesReport()
        );

        // Inventory Report Card
        JPanel inventoryReportCard = createReportCard(
                "Inventory Report",
                "View current inventory status and analytics",
                "Generate Inventory Report",
                e -> generateInventoryReport()
        );

        // Client Report Card
        JPanel clientReportCard = createReportCard(
                "Client Report",
                "Analyze client data and purchase history",
                "Generate Client Report",
                e -> generateClientReport()
        );

        // Financial Report Card
        JPanel financialReportCard = createReportCard(
                "Financial Report",
                "View revenue, profit, and financial analytics",
                "Generate Financial Report",
                e -> generateFinancialReport()
        );

        contentPanel.add(salesReportCard);
        contentPanel.add(inventoryReportCard);
        contentPanel.add(clientReportCard);
        contentPanel.add(financialReportCard);

        return contentPanel;
    }

    private JPanel createReportCard(String title, String description, String buttonText,
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
        ModernButton generateButton = new ModernButton(buttonText);
        generateButton.addActionListener(action);
        generateButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(descriptionArea);
        card.add(Box.createVerticalGlue());
        card.add(generateButton);

        return card;
    }

    private void generateSalesReport() {
        JOptionPane.showMessageDialog(this,
                "Sales report generation will be implemented here.\nThis would include:\n" +
                        "- Sales by date range\n" +
                        "- Sales by car model\n" +
                        "- Sales by salesperson\n" +
                        "- Revenue analytics",
                "Sales Report",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateInventoryReport() {
        JOptionPane.showMessageDialog(this,
                "Inventory report generation will be implemented here.\nThis would include:\n" +
                        "- Current stock levels\n" +
                        "- Low inventory alerts\n" +
                        "- Car aging report\n" +
                        "- Inventory valuation",
                "Inventory Report",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateClientReport() {
        JOptionPane.showMessageDialog(this,
                "Client report generation will be implemented here.\nThis would include:\n" +
                        "- Client demographics\n" +
                        "- Purchase history\n" +
                        "- Client lifetime value\n" +
                        "- Client acquisition trends",
                "Client Report",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void generateFinancialReport() {
        JOptionPane.showMessageDialog(this,
                "Financial report generation will be implemented here.\nThis would include:\n" +
                        "- Revenue analysis\n" +
                        "- Profit margins\n" +
                        "- Monthly/quarterly summaries\n" +
                        "- Financial forecasting",
                "Financial Report",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void refresh() {
        // Reports panel doesn't need regular refresh
    }
}
