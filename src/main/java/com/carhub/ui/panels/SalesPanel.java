package com.carhub.ui.panels;

import com.carhub.entity.Admin;
import com.carhub.entity.Sale;
import com.carhub.service.CarService;
import com.carhub.service.ClientService;
import com.carhub.service.SaleService;
import com.carhub.service.PdfService;
import com.carhub.ui.components.ModernButton;
import com.carhub.ui.components.ModernTable;
import com.carhub.ui.components.ModernTextField;
import com.carhub.ui.dialogs.SaleDialog;
import com.carhub.ui.main.MainWindow;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.awt.Dialog;

public class SalesPanel extends JPanel implements MainWindow.RefreshablePanel {

    private SaleService saleService;
    private CarService carService;
    private ClientService clientService;
    private Admin currentAdmin;
    private ModernTable salesTable;
    private DefaultTableModel tableModel;
    private ModernTextField searchField;
    private JComboBox<String> statusFilter;

    @Autowired
    private PdfService pdfService;

    public SalesPanel(SaleService saleService, CarService carService, ClientService clientService, Admin currentAdmin) {
        this.saleService = saleService;
        this.carService = carService;
        this.clientService = clientService;
        this.currentAdmin = currentAdmin;

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

        // Header with search and filters
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Table
        JScrollPane tableScrollPane = createTablePanel();
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        // Title section
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Sales Management");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Track and manage all sales transactions");
        subtitleLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(161, 161, 170));

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(subtitleLabel);

        // Search and filter section
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        searchPanel.setOpaque(false);

        searchField = new ModernTextField("Search sales...");
        searchField.setPreferredSize(new Dimension(200, 36));
        searchField.addActionListener(e -> filterData());

        statusFilter = new JComboBox<>(new String[]{"All Status", "PENDING", "COMPLETED", "CANCELLED"});
        statusFilter.setBackground(new Color(47, 51, 73));
        statusFilter.setForeground(Color.WHITE);
        statusFilter.addActionListener(e -> filterData());

        ModernButton refreshBtn = new ModernButton("Refresh");
        refreshBtn.addActionListener(e -> loadData());

        searchPanel.add(new JLabel("Search:") {{ setForeground(Color.WHITE); }});
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Status:") {{ setForeground(Color.WHITE); }});
        searchPanel.add(statusFilter);
        searchPanel.add(refreshBtn);

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);

        ModernButton newSaleBtn = new ModernButton("New Sale");
        ModernButton viewSaleBtn = new ModernButton("View Details");
        ModernButton editSaleBtn = new ModernButton("Edit Sale");
        ModernButton deleteSaleBtn = new ModernButton("Delete Sale");
        ModernButton exportPdfBtn = new ModernButton("Export PDF");

        newSaleBtn.addActionListener(e -> showNewSaleDialog());
        viewSaleBtn.addActionListener(e -> viewSaleDetails());
        editSaleBtn.addActionListener(e -> editSale());
        deleteSaleBtn.addActionListener(e -> deleteSale());
        exportPdfBtn.addActionListener(e -> exportToPdf());

        buttonPanel.add(newSaleBtn);
        buttonPanel.add(viewSaleBtn);
        buttonPanel.add(editSaleBtn);
        buttonPanel.add(deleteSaleBtn);
        buttonPanel.add(exportPdfBtn);

        // Combine header components
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(titlePanel, BorderLayout.WEST);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        headerPanel.add(topPanel, BorderLayout.NORTH);
        headerPanel.add(searchPanel, BorderLayout.SOUTH);

        return headerPanel;
    }

    private JScrollPane createTablePanel() {
        String[] columns = {"ID", "Invoice", "Date", "Car", "Client", "Amount", "Payment", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        salesTable = new ModernTable(tableModel);
        salesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Double-click to view details
        salesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewSaleDetails();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBackground(new Color(42, 45, 53));
        scrollPane.getViewport().setBackground(new Color(42, 45, 53));
        scrollPane.setBorder(null);

        return scrollPane;
    }

    private void loadData() {
        try {
            List<Sale> sales = saleService.getAllSales();
            updateTable(sales);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading sales: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterData() {
        try {
            String searchText = searchField.getText().trim();
            String statusText = (String) statusFilter.getSelectedItem();

            List<Sale> sales = saleService.getAllSales();

            if (!searchText.isEmpty() || !"All Status".equals(statusText)) {
                sales = sales.stream()
                        .filter(sale -> {
                            boolean matchesSearch = searchText.isEmpty() ||
                                    sale.getInvoiceNumber().toLowerCase().contains(searchText.toLowerCase()) ||
                                    sale.getCar().getDisplayName().toLowerCase().contains(searchText.toLowerCase()) ||
                                    sale.getClient().getFullName().toLowerCase().contains(searchText.toLowerCase());

                            boolean matchesStatus = "All Status".equals(statusText) ||
                                    sale.getPaymentStatus().toString().equals(statusText);

                            return matchesSearch && matchesStatus;
                        })
                        .toList();
            }
            updateTable(sales);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTable(List<Sale> sales) {
        tableModel.setRowCount(0);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        for (Sale sale : sales) {
            Object[] row = {
                    sale.getId(),
                    sale.getInvoiceNumber(),
                    sale.getSaleDate().format(dateFormatter),
                    sale.getCar().getDisplayName(),
                    sale.getClient().getFullName(),
                    currencyFormat.format(sale.getTotalAmount()),
                    sale.getPaymentMethod().toString(),
                    sale.getPaymentStatus().toString()
            };
            tableModel.addRow(row);
        }
    }

    public void showNewSaleDialog() {
        SaleDialog dialog = new SaleDialog(SwingUtilities.getWindowAncestor(this), null,
                saleService, carService, clientService, currentAdmin);
        dialog.setVisible(true);

        if (dialog.isSaleSaved()) {
            loadData();
            JOptionPane.showMessageDialog(this, "Sale created successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void viewSaleDetails() {
        int selectedRow = salesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a sale to view details.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Long saleId = (Long) tableModel.getValueAt(selectedRow, 0);
            Sale sale = saleService.findById(saleId).orElse(null);

            if (sale != null) {
                showSaleDetailsDialog(sale);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error viewing sale details: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editSale() {
        int selectedRow = salesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a sale to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Long saleId = (Long) tableModel.getValueAt(selectedRow, 0);
            Sale sale = saleService.findById(saleId).orElse(null);

            if (sale != null) {
                SaleDialog dialog = new SaleDialog(SwingUtilities.getWindowAncestor(this), sale,
                        saleService, carService, clientService, currentAdmin);
                dialog.setVisible(true);

                if (dialog.isSaleSaved()) {
                    loadData();
                    JOptionPane.showMessageDialog(this, "Sale updated successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error editing sale: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSale() {
        int selectedRow = salesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a sale to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int option = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this sale?\nThis will also mark the car as available again.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (option == JOptionPane.YES_OPTION) {
            try {
                Long saleId = (Long) tableModel.getValueAt(selectedRow, 0);
                saleService.deleteSale(saleId);
                loadData();
                JOptionPane.showMessageDialog(this, "Sale deleted successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting sale: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showSaleDetailsDialog(Sale sale) {
        JDialog detailsDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Sale Details", Dialog.ModalityType.APPLICATION_MODAL);
        detailsDialog.setSize(500, 600);
        detailsDialog.setLocationRelativeTo(this);
        detailsDialog.getContentPane().setBackground(new Color(26, 28, 32));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(26, 28, 32));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add sale details
        addDetailRow(contentPanel, "Invoice Number:", sale.getInvoiceNumber());
        addDetailRow(contentPanel, "Sale Date:", sale.getSaleDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
        addDetailRow(contentPanel, "Car:", sale.getCar().getDisplayName());
        addDetailRow(contentPanel, "Client:", sale.getClient().getFullName());
        addDetailRow(contentPanel, "Sale Price:", NumberFormat.getCurrencyInstance().format(sale.getSalePrice()));
        addDetailRow(contentPanel, "Payment Method:", sale.getPaymentMethod().toString());
        addDetailRow(contentPanel, "Payment Status:", sale.getPaymentStatus().toString());
        addDetailRow(contentPanel, "Total Amount:", NumberFormat.getCurrencyInstance().format(sale.getTotalAmount()));
        addDetailRow(contentPanel, "Sold By:", sale.getAdmin().getFullName());

        if (sale.getNotes() != null && !sale.getNotes().isEmpty()) {
            addDetailRow(contentPanel, "Notes:", sale.getNotes());
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        detailsDialog.add(scrollPane);

        detailsDialog.setVisible(true);
    }

    private void addDetailRow(JPanel parent, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("SF Pro Text", Font.BOLD, 14));
        labelComp.setForeground(new Color(161, 161, 170));
        labelComp.setPreferredSize(new Dimension(120, 20));

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        valueComp.setForeground(Color.WHITE);

        row.add(labelComp, BorderLayout.WEST);
        row.add(valueComp, BorderLayout.CENTER);

        parent.add(row);
    }

    private void exportToPdf() {
        try {
            List<Sale> sales = saleService.getAllSales();

            if (pdfService == null) {
                JOptionPane.showMessageDialog(this,
                        "PDF export functionality will generate:\n" +
                                "- Complete sales report\n" +
                                "- Sales summary and statistics\n" +
                                "- Individual sale details\n" +
                                "- Professional formatted report",
                        "PDF Export",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String outputPath = pdfService.getDefaultOutputPath();
            String fileName = pdfService.generateSalesReport(sales, outputPath);

            int option = JOptionPane.showConfirmDialog(this,
                    "Sales report generated successfully!\n" +
                            "File: " + fileName + "\n\n" +
                            "Would you like to open the file location?",
                    "Export Successful",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);

            if (option == JOptionPane.YES_OPTION) {
                Desktop.getDesktop().open(new java.io.File(outputPath));
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error exporting PDF: " + e.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void refresh() {
        loadData();
    }
}
