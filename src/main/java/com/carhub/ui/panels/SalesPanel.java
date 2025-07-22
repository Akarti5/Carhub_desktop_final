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
import com.carhub.util.CurrencyUtils;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.awt.Dialog;

import com.toedter.calendar.JDateChooser;
import java.util.Date;
import java.time.ZoneId;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.File;


public class SalesPanel extends JPanel implements MainWindow.RefreshablePanel {

    private SaleService saleService;
    private CarService carService;
    private ClientService clientService;
    private Admin currentAdmin;
    private ModernTable salesTable;
    private DefaultTableModel tableModel;
    private ModernTextField searchField;
    private JComboBox<String> statusFilter;

    private JDateChooser fromDateChooser;
    private JDateChooser toDateChooser;
    private ModernButton filterByDateBtn;
    private ModernButton clearDateFilterBtn;

    private final PdfService pdfService;

    @Autowired
    public SalesPanel(SaleService saleService, CarService carService, ClientService clientService, PdfService pdfService, Admin currentAdmin) {
        this.saleService = saleService;
        this.carService = carService;
        this.clientService = clientService;
        this.pdfService = pdfService;
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

        // Date range filters
        fromDateChooser = new JDateChooser();
        fromDateChooser.setPreferredSize(new Dimension(120, 36));
        fromDateChooser.setBackground(new Color(47, 51, 73));
        fromDateChooser.setForeground(Color.WHITE);

        toDateChooser = new JDateChooser();
        toDateChooser.setPreferredSize(new Dimension(120, 36));
        toDateChooser.setBackground(new Color(47, 51, 73));
        toDateChooser.setForeground(Color.WHITE);

        filterByDateBtn = new ModernButton("Filter by Date");
        filterByDateBtn.addActionListener(e -> filterByDateRange());

        clearDateFilterBtn = new ModernButton("Clear Date Filter");
        clearDateFilterBtn.addActionListener(e -> clearDateFilter());

        ModernButton refreshBtn = new ModernButton("Refresh");
        refreshBtn.addActionListener(e -> loadData());

        searchPanel.add(new JLabel("Search:") {{ setForeground(Color.WHITE); }});
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Status:") {{ setForeground(Color.WHITE); }});
        searchPanel.add(statusFilter);
        searchPanel.add(new JLabel("From:") {{ setForeground(Color.WHITE); }});
        searchPanel.add(fromDateChooser);
        searchPanel.add(new JLabel("To:") {{ setForeground(Color.WHITE); }});
        searchPanel.add(toDateChooser);
        searchPanel.add(filterByDateBtn);
        searchPanel.add(clearDateFilterBtn);
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

    private void filterByDateRange() {
        try {
            Date fromDate = fromDateChooser.getDate();
            Date toDate = toDateChooser.getDate();

            if (fromDate == null || toDate == null) {
                JOptionPane.showMessageDialog(this,
                        "Please select both From and To dates.",
                        "Date Range Required",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (fromDate.after(toDate)) {
                JOptionPane.showMessageDialog(this,
                        "From date cannot be after To date.",
                        "Invalid Date Range",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Convert Date to LocalDateTime
            LocalDateTime startDateTime = fromDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                    .withHour(0).withMinute(0).withSecond(0);

            LocalDateTime endDateTime = toDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()
                    .withHour(23).withMinute(59).withSecond(59);

            // Get sales within date range
            List<Sale> sales = saleService.findSalesByDateRange(startDateTime, endDateTime);

            // Apply additional filters if any
            String searchText = searchField.getText().trim();
            String statusText = (String) statusFilter.getSelectedItem();

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

            // Show results count
            JOptionPane.showMessageDialog(this,
                    "Found " + sales.size() + " sales between " +
                            fromDate.toString().substring(0, 10) + " and " +
                            toDate.toString().substring(0, 10),
                    "Filter Results",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error filtering by date range: " + e.getMessage(),
                    "Filter Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearDateFilter() {
        fromDateChooser.setDate(null);
        toDateChooser.setDate(null);
        loadData(); // Reload all data
    }

    private void updateTable(List<Sale> sales) {
        tableModel.setRowCount(0);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        for (Sale sale : sales) {
            Object[] row = {
                    sale.getId(),
                    sale.getInvoiceNumber(),
                    sale.getSaleDate().format(dateFormatter),
                    sale.getCar().getDisplayName(),
                    sale.getClient().getFullName(),
                    CurrencyUtils.formatCurrency(sale.getTotalAmount()),
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
        JDialog detailsDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Détails de la vente", Dialog.ModalityType.APPLICATION_MODAL);
        detailsDialog.setSize(500, 650);
        detailsDialog.setLocationRelativeTo(this);
        detailsDialog.getContentPane().setBackground(new Color(26, 28, 32));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(26, 28, 32));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(26, 28, 32));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add sale details
        addDetailRow(contentPanel, "Numéro de facture:", sale.getInvoiceNumber());
        addDetailRow(contentPanel, "Date de vente:", sale.getSaleDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        addDetailRow(contentPanel, "Véhicule:", sale.getCar().getDisplayName());
        addDetailRow(contentPanel, "Client:", sale.getClient().getFullName());
        addDetailRow(contentPanel, "Prix de vente:", CurrencyUtils.formatCurrency(sale.getSalePrice()));
        addDetailRow(contentPanel, "Acompte:", CurrencyUtils.formatCurrency(sale.getDownPayment()));
        addDetailRow(contentPanel, "Montant total:", CurrencyUtils.formatCurrency(sale.getTotalAmount()));
        addDetailRow(contentPanel, "Vendu par:", sale.getAdmin().getFullName());

        if (sale.getNotes() != null && !sale.getNotes().isEmpty()) {
            addDetailRow(contentPanel, "Remarques:", sale.getNotes());
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(26, 28, 32));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add button panel at the bottom
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(26, 28, 32));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        ModernButton printButton = new ModernButton("Générer Facture");
        printButton.setBackground(new Color(59, 130, 246));
        printButton.setForeground(Color.WHITE);
        printButton.addActionListener(e -> generateInvoicePdf(sale));

        ModernButton closeButton = new ModernButton("Fermer");
        closeButton.setBackground(new Color(100, 116, 139));
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(e -> detailsDialog.dispose());

        buttonPanel.add(printButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        detailsDialog.add(mainPanel);

        // Center the dialog on the screen
        detailsDialog.setLocationRelativeTo(null);
        detailsDialog.setVisible(true);
    }

    private void addDetailRow(JPanel parent, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("SF Pro Text", Font.BOLD, 14));
        labelComp.setForeground(new Color(161, 161, 170));
        labelComp.setPreferredSize(new Dimension(150, 20));

        JLabel valueComp = new JLabel(value);
        valueComp.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        valueComp.setForeground(Color.WHITE);

        // Add some spacing between label and value
        row.add(Box.createHorizontalStrut(10), BorderLayout.WEST);
        row.add(labelComp, BorderLayout.WEST);
        row.add(Box.createHorizontalStrut(20), BorderLayout.CENTER);
        row.add(valueComp, BorderLayout.EAST);
        row.add(Box.createHorizontalStrut(10), BorderLayout.EAST);

        parent.add(row);
    }

    private void generateInvoicePdf(Sale sale) {
        try {
            // Show confirmation dialog
            int option = JOptionPane.showConfirmDialog(
                this,
                "Voulez-vous générer une facture PDF pour cette vente ?",
                "Générer une facture",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (option == JOptionPane.YES_OPTION) {
                // Show progress dialog
                JDialog progressDialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Génération de la facture", Dialog.ModalityType.APPLICATION_MODAL);
                progressDialog.setSize(300, 100);
                progressDialog.setLocationRelativeTo(this);
                progressDialog.setLayout(new BorderLayout());
                
                JLabel progressLabel = new JLabel("Génération de la facture en cours...");
                progressLabel.setHorizontalAlignment(JLabel.CENTER);
                progressDialog.add(progressLabel, BorderLayout.CENTER);
                
                // Show the progress dialog in a separate thread to keep the UI responsive
                new Thread(() -> {
                    try {
                        String outputPath = pdfService.getDefaultOutputPath();
                        String filePath = pdfService.generateInvoice(sale, outputPath);
                        
                        // Close the progress dialog in the Event Dispatch Thread
                        SwingUtilities.invokeLater(() -> {
                            progressDialog.dispose();
                            
                            // Show success message with option to open the file
                            Object[] options = {"Ouvrir le dossier", "OK"};
                            int choice = JOptionPane.showOptionDialog(
                                this,
                                "La facture a été générée avec succès.\nEmplacement: " + filePath,
                                "Facture générée",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.INFORMATION_MESSAGE,
                                null,
                                options,
                                options[0]
                            );
                            
                            if (choice == 0) { // User clicked "Open Folder"
                                try {
                                    File file = new File(filePath);
                                    Desktop.getDesktop().open(file.getParentFile());
                                } catch (Exception ex) {
                                    JOptionPane.showMessageDialog(
                                        this,
                                        "Impossible d'ouvrir le dossier: " + ex.getMessage(),
                                        "Erreur",
                                        JOptionPane.ERROR_MESSAGE
                                    );
                                }
                            }
                        });
                    } catch (Exception ex) {
                        // Show error message in the Event Dispatch Thread
                        SwingUtilities.invokeLater(() -> {
                            progressDialog.dispose();
                            JOptionPane.showMessageDialog(
                                this,
                                "Erreur lors de la génération de la facture: " + ex.getMessage(),
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE
                            );
                        });
                    }
                }).start();
                
                progressDialog.setVisible(true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Erreur inattendue: " + ex.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void exportToPdf() {
        try {
            // Show information about what will be included in the PDF
            JOptionPane.showMessageDialog(this,
                    "This will generate a PDF report containing:\n" +
                    "- Complete sales report\n" +
                    "- Sales summary and statistics\n" +
                    "- Individual sale details\n" +
                    "- Professional formatted report",
                    "PDF Export",
                    JOptionPane.INFORMATION_MESSAGE);

            // Get all sales data
            List<Sale> sales = saleService.getAllSales();
            
            if (sales == null || sales.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No sales data available to export.",
                        "No Data",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Get the output directory and generate the report
            String outputPath = pdfService.getDefaultOutputPath();
            String fileName = pdfService.generateSalesReport(sales, outputPath);

            // Show success message with option to open the file location
            int option = JOptionPane.showConfirmDialog(this,
                    "Sales report generated successfully!\n" +
                    "File: " + fileName + "\n\n" +
                    "Would you like to open the file location?",
                    "Export Successful",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);

            if (option == JOptionPane.YES_OPTION) {
                try {
                    Desktop.getDesktop().open(new java.io.File(outputPath));
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                            "Could not open file location. The file was saved to:\n" + fileName,
                            "Open Location Failed",
                            JOptionPane.WARNING_MESSAGE);
                }
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
