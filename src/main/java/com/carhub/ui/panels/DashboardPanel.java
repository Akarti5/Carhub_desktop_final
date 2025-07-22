package com.carhub.ui.panels;

import com.carhub.entity.Car;
import com.carhub.entity.Sale;
import com.carhub.service.CarService;
import com.carhub.service.ClientService;
import com.carhub.service.SaleService;
import com.carhub.ui.components.MetricCard;
import com.carhub.ui.components.ModernTable;
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
        JPanel mainPanel = new JPanel(new BorderLayout(24, 24));
        mainPanel.setOpaque(false);

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Metrics cards
        JPanel metricsPanel = createMetricsPanel();
        mainPanel.add(metricsPanel, BorderLayout.CENTER);

        // Tables panel
        JPanel tablesPanel = createTablesPanel();
        mainPanel.add(tablesPanel, BorderLayout.SOUTH);

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
        JPanel metricsPanel = new JPanel(new GridLayout(1, 4, 16, 0));
        metricsPanel.setOpaque(false);
        metricsPanel.setPreferredSize(new Dimension(0, 120));

        // Create metric cards with 4 parameters (title, value, subtitle, isPositive)
        totalCarsCard = new MetricCard("Total Cars", "0", "In inventory", true);
        totalSalesCard = new MetricCard("Total Sales", "0", "This month", true);
        totalClientsCard = new MetricCard("Total Clients", "0", "Active clients", true);
        monthlyRevenueCard = new MetricCard("Monthly Revenue", "$0", "Last 6 months", true);

        metricsPanel.add(totalCarsCard);
        metricsPanel.add(totalSalesCard);
        metricsPanel.add(totalClientsCard);
        metricsPanel.add(monthlyRevenueCard);

        return metricsPanel;
    }

    private JPanel createTablesPanel() {
        JPanel tablesPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        tablesPanel.setOpaque(false);
        tablesPanel.setPreferredSize(new Dimension(0, 300));

        // Recent Sales Table
        JPanel recentSalesPanel = createTablePanel("Recent Sales", createRecentSalesTable());
        tablesPanel.add(recentSalesPanel);

        // Low Inventory Table
        JPanel lowInventoryPanel = createTablePanel("Low Inventory Alert", createLowInventoryTable());
        tablesPanel.add(lowInventoryPanel);

        return tablesPanel;
    }

    private JPanel createTablePanel(String title, JScrollPane tableScrollPane) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(42, 45, 53));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        panel.add(titleLabel, BorderLayout.NORTH);
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
            BigDecimal monthlyRevenue = saleService.getMonthlyRevenue();

            // Update metric cards
            totalCarsCard.updateValue(String.valueOf(totalCars));
            totalSalesCard.updateValue(String.valueOf(totalSales));
            totalClientsCard.updateValue(String.valueOf(totalClients));
            monthlyRevenueCard.updateValue(CurrencyUtils.formatCurrency(monthlyRevenue));

            // Load recent sales
            loadRecentSales();

            // Load low inventory
            loadLowInventory();

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

    @Override
    public void refresh() {
        loadData();
    }
}
