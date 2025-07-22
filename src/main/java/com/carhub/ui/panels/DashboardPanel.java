package com.carhub.ui.panels;

import com.carhub.entity.Car;
import com.carhub.entity.Sale;
import com.carhub.service.CarService;
import com.carhub.service.ClientService;
import com.carhub.service.SaleService;
import com.carhub.ui.components.MetricCard;
import com.carhub.ui.components.ModernTable;
import com.carhub.ui.components.MonthlyRevenueChart;
import com.carhub.ui.main.MainWindow;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import com.carhub.util.CurrencyUtils;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardPanel extends JPanel implements MainWindow.RefreshablePanel {

    private CarService carService;
    private SaleService saleService;
    private ClientService clientService;

    private MetricCard totalCarsCard;
    private MetricCard totalSalesCard;
    private MetricCard totalClientsCard;
    private MetricCard monthlyRevenueCard;

    private ModernTable recentSalesTable;
    private ModernTable lowInventoryTable;
    private MonthlyRevenueChart monthlyRevenueChart;

    public DashboardPanel(CarService carService, SaleService saleService, ClientService clientService) {
        this.carService = carService;
        this.saleService = saleService;
        this.clientService = clientService;

        setupPanel();
        createComponents();
        loadData();
    }

    private void setupPanel() {
        setBackground(new Color(26, 28, 32));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
    }

    private void createComponents() {
        // Create a vertical box layout for the main content
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);
        
        // Add a small gap at the top
        mainContent.add(Box.createVerticalStrut(8));
        
        // Header - don't let it take too much space
        JPanel headerPanel = createHeaderPanel();
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        mainContent.add(headerPanel);
        
        // Add a small gap
        mainContent.add(Box.createVerticalStrut(8));
        
        // Metrics cards - fixed height
        JPanel metricsPanel = createMetricsPanel();
        metricsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        mainContent.add(metricsPanel);
        
        // Add a small gap
        mainContent.add(Box.createVerticalStrut(16));
        
        // Monthly Revenue Chart - fixed height
        JPanel chartPanel = createChartPanel();
        chartPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));
        mainContent.add(chartPanel);
        
        // Add a small gap
        mainContent.add(Box.createVerticalStrut(16));
        
        // Tables panel - takes remaining space
        JPanel tablesPanel = createTablesPanel();
        tablesPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        
        mainContent.add(tablesPanel);
        
        // Add everything to the main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);
        mainPanel.add(mainContent, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Welcome to CarHub - " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        subtitleLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(161, 161, 170));

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel createMetricsPanel() {
        JPanel metricsPanel = new JPanel(new GridLayout(1, 4, 8, 0)); // Reduced horizontal gap between cards
        metricsPanel.setOpaque(false);
        metricsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90)); // Set maximum height
        metricsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0)); // Add small bottom padding

        // Create metric cards with 4 parameters (title, value, subtitle, isPositive)
        totalCarsCard = new MetricCard("Total Cars", "0", "In inventory", true);
        totalSalesCard = new MetricCard("Total Sales", "0", "This month", true);
        totalClientsCard = new MetricCard("Total Clients", "0", "Active clients", true);
        monthlyRevenueCard = new MetricCard("Revenue (6M)", "$0", "Last 6 months", true);

        metricsPanel.add(totalCarsCard);
        metricsPanel.add(totalSalesCard);
        metricsPanel.add(totalClientsCard);
        metricsPanel.add(monthlyRevenueCard);

        return metricsPanel;
    }

    private JPanel createChartPanel() {
        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(new Color(42, 45, 53));
        chartContainer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel titleLabel = new JLabel("Revenue Overview");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        monthlyRevenueChart = new MonthlyRevenueChart();

        chartContainer.add(titleLabel, BorderLayout.NORTH);
        chartContainer.add(monthlyRevenueChart, BorderLayout.CENTER);

        return chartContainer;
    }

    private JPanel createTablesPanel() {
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 8, 0)); // Reduced gap between tables
        tablesPanel.setOpaque(false);
        
        // Set a fixed height for the tables panel
        int tableHeight = 200; // Reduced from 300
        tablesPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, tableHeight));

        // Recent Sales Table
        JScrollPane recentSalesScroll = createRecentSalesTable();
        recentSalesScroll.setPreferredSize(new Dimension(0, tableHeight - 20)); // Account for panel padding
        JPanel recentSalesPanel = createTablePanel("Recent Sales", recentSalesScroll);
        
        // Low Inventory Table
        JScrollPane lowInventoryScroll = createLowInventoryTable();
        lowInventoryScroll.setPreferredSize(new Dimension(0, tableHeight - 20)); // Account for panel padding
        JPanel lowInventoryPanel = createTablePanel("Low Inventory Alert", lowInventoryScroll);
        
        tablesPanel.add(recentSalesPanel);
        tablesPanel.add(lowInventoryPanel);

        return tablesPanel;
    }

    private JPanel createTablePanel(String title, JScrollPane tableScrollPane) {
        JPanel panel = new JPanel(new BorderLayout(0, 8)); // Reduced vertical gap
        panel.setBackground(new Color(42, 45, 53));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12)); // Reduced padding

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 16)); // Slightly smaller font
        titleLabel.setForeground(Color.WHITE);

        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Make sure the table takes up the remaining space
        tableScrollPane.setBorder(null);
        tableScrollPane.setOpaque(false);
        tableScrollPane.getViewport().setOpaque(false);
        
        // Style the table header
        JTable table = (JTable) tableScrollPane.getViewport().getView();
        table.setRowHeight(28); // Slightly smaller row height
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        panel.add(tableScrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JScrollPane createRecentSalesTable() {
        String[] columns = {"Date", "Car", "Client", "Amount"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        recentSalesTable = new ModernTable(model);
        JScrollPane scrollPane = new JScrollPane(recentSalesTable);
        scrollPane.setBackground(new Color(42, 45, 53));
        scrollPane.getViewport().setBackground(new Color(42, 45, 53));
        scrollPane.setBorder(null);

        return scrollPane;
    }

    private JScrollPane createLowInventoryTable() {
        String[] columns = {"Make", "Model", "Year", "Stock"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        lowInventoryTable = new ModernTable(model);
        JScrollPane scrollPane = new JScrollPane(lowInventoryTable);
        scrollPane.setBackground(new Color(42, 45, 53));
        scrollPane.getViewport().setBackground(new Color(42, 45, 53));
        scrollPane.setBorder(null);

        return scrollPane;
    }

    private void loadData() {
        try {
            // Load metrics
            long totalCars = carService.getTotalCarsCount();
            long totalSales = saleService.getTotalSalesCount();
            long totalClients = clientService.getTotalClientsCount();
            
            // Calculate total revenue for last 6 months
            List<Object[]> monthlyData = saleService.getMonthlyRevenueForLast6Months();
            BigDecimal totalRevenue6Months = monthlyData.stream()
                .map(data -> (BigDecimal) data[2])
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Update metric cards
            totalCarsCard.updateValue(String.valueOf(totalCars));
            totalSalesCard.updateValue(String.valueOf(totalSales));
            totalClientsCard.updateValue(String.valueOf(totalClients));
            monthlyRevenueCard.updateValue(CurrencyUtils.formatCurrency(totalRevenue6Months));

            // Load recent sales
            loadRecentSales();

            // Load low inventory
            loadLowInventory();

            // Load monthly revenue chart
            loadMonthlyRevenueChart();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading dashboard data: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRecentSales() {
        try {
            List<Sale> recentSales = saleService.getRecentSales(10);
            DefaultTableModel model = (DefaultTableModel) recentSalesTable.getModel();
            model.setRowCount(0);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
            for (Sale sale : recentSales) {
                Object[] row = {
                        sale.getSaleDate().format(formatter),
                        sale.getCar().getMake() + " " + sale.getCar().getModel(),
                        sale.getClient().getFullName(),
                        CurrencyUtils.formatCurrency(sale.getTotalAmount())
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLowInventory() {
        try {
            List<Car> lowInventoryCars = carService.getLowInventoryCars(5);
            DefaultTableModel model = (DefaultTableModel) lowInventoryTable.getModel();
            model.setRowCount(0);

            for (Car car : lowInventoryCars) {
                Object[] row = {
                        car.getMake(),
                        car.getModel(),
                        car.getYear(),
                        "Low Stock"
                };
                model.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadMonthlyRevenueChart() {
        try {
            List<Object[]> monthlyRevenueData = saleService.getMonthlyRevenueForLast6Months();
            monthlyRevenueChart.updateData(monthlyRevenueData);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() {
        loadData();
    }
}
