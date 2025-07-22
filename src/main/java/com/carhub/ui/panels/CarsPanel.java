package com.carhub.ui.panels;

import com.carhub.entity.Admin;
import com.carhub.entity.Car;
import com.carhub.service.CarService;
import com.carhub.service.PdfService;
import com.carhub.ui.components.ModernButton;
import com.carhub.ui.components.ModernTable;
import com.carhub.ui.components.ModernTextField;
import com.carhub.ui.dialogs.CarDialog;
import com.carhub.ui.main.MainWindow;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import com.carhub.util.CurrencyUtils;
import java.util.List;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import com.toedter.calendar.JDateChooser;

public class CarsPanel extends JPanel implements MainWindow.RefreshablePanel {

    private CarService carService;
    private Admin currentAdmin;
    private ModernTable carsTable;
    private DefaultTableModel tableModel;
    private ModernTextField searchField;
    private JComboBox<String> statusFilter;
    private JDateChooser fromDateChooser;
    private JDateChooser toDateChooser;

    @Autowired
    private PdfService pdfService;

    public CarsPanel(CarService carService, Admin currentAdmin) {
        this.carService = carService;
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

        JLabel titleLabel = new JLabel("Cars Inventory");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Manage your car inventory");
        subtitleLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(161, 161, 170));

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(subtitleLabel);

        // Search and filter section
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        searchPanel.setOpaque(false);

        searchField = new ModernTextField("Search cars...");
        searchField.setPreferredSize(new Dimension(200, 36));
        searchField.addActionListener(e -> filterData());

        statusFilter = new JComboBox<>(new String[]{"All Status", "AVAILABLE", "SOLD", "RESERVED", "MAINTENANCE"});
        statusFilter.setBackground(new Color(47, 51, 73));
        statusFilter.setForeground(Color.WHITE);
        statusFilter.addActionListener(e -> filterData());

        // Date range filter
        fromDateChooser = new JDateChooser();
        fromDateChooser.setDateFormatString("dd/MM/yyyy");
        fromDateChooser.getCalendarButton().setText("From");
        fromDateChooser.addPropertyChangeListener("date", e -> filterData());

        toDateChooser = new JDateChooser();
        toDateChooser.setDateFormatString("dd/MM/yyyy");
        toDateChooser.getCalendarButton().setText("To");
        toDateChooser.addPropertyChangeListener("date", e -> filterData());

        // Add clear date filter button
        ModernButton clearDateFilterBtn = new ModernButton("Clear Dates");
        clearDateFilterBtn.addActionListener(e -> {
            fromDateChooser.setDate(null);
            toDateChooser.setDate(null);
            filterData();
        });

        ModernButton refreshBtn = new ModernButton("Refresh");
        refreshBtn.addActionListener(e -> loadData());

        // First row of filters
        searchPanel.add(new JLabel("Search:") {{ setForeground(Color.WHITE); }});
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Status:") {{ setForeground(Color.WHITE); }});
        searchPanel.add(statusFilter);
        searchPanel.add(refreshBtn);

        // Second row for date range filters
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        datePanel.setOpaque(false);
        
        datePanel.add(new JLabel("Added From:") {{ setForeground(Color.WHITE); }});
        datePanel.add(fromDateChooser);
        datePanel.add(new JLabel("To:") {{ setForeground(Color.WHITE); }});
        datePanel.add(toDateChooser);
        datePanel.add(clearDateFilterBtn);
        
        searchPanel.add(Box.createHorizontalStrut(20)); // Add some spacing
        searchPanel.add(datePanel);

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);

        ModernButton addCarBtn = new ModernButton("Add Car");
        ModernButton editCarBtn = new ModernButton("Edit Car");
        ModernButton deleteCarBtn = new ModernButton("Delete Car");
        ModernButton exportPdfBtn = new ModernButton("Export PDF");

        addCarBtn.addActionListener(e -> showAddCarDialog());
        editCarBtn.addActionListener(e -> showEditCarDialog());
        deleteCarBtn.addActionListener(e -> deleteSelectedCar());
        exportPdfBtn.addActionListener(e -> exportToPdf());

        buttonPanel.add(addCarBtn);
        buttonPanel.add(editCarBtn);
        buttonPanel.add(deleteCarBtn);
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
        String[] columns = {"ID", "Make", "Model", "Year", "Color", "Price", "Status", "Days in Stock"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        carsTable = new ModernTable(tableModel);
        carsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Double-click to edit
        carsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showEditCarDialog();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(carsTable);
        scrollPane.setBackground(new Color(42, 45, 53));
        scrollPane.getViewport().setBackground(new Color(42, 45, 53));
        scrollPane.setBorder(null);

        return scrollPane;
    }

    private void loadData() {
        try {
            List<Car> cars = carService.getAllCars();
            updateTable(cars);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading cars: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterData() {
        try {
            String searchText = searchField.getText().trim();
            String statusText = (String) statusFilter.getSelectedItem();
            Date fromDate = fromDateChooser.getDate();
            Date toDate = toDateChooser.getDate();

            List<Car> cars = carService.getAllCars();
            
            // Apply filters
            cars = cars.stream()
                    .filter(car -> {
                        // Search text filter
                        boolean matchesSearch = searchText.isEmpty() ||
                                car.getBrand().toLowerCase().contains(searchText.toLowerCase()) ||
                                car.getModel().toLowerCase().contains(searchText.toLowerCase()) ||
                                car.getColor().toLowerCase().contains(searchText.toLowerCase());

                        // Status filter
                        boolean matchesStatus = "All Status".equals(statusText) ||
                                car.getStatus().toString().equals(statusText);
                                
                        // Date range filter
                        boolean matchesDateRange = true;
                        if (fromDate != null || toDate != null) {
                            LocalDate carDate = car.getCreatedAt() != null ? 
                                car.getCreatedAt().toLocalDate() : 
                                LocalDate.now();
                                
                            if (fromDate != null) {
                                LocalDate fromLocalDate = fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                matchesDateRange = !carDate.isBefore(fromLocalDate);
                            }
                            
                            if (matchesDateRange && toDate != null) {
                                LocalDate toLocalDate = toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                matchesDateRange = !carDate.isAfter(toLocalDate);
                            }
                        }

                        return matchesSearch && matchesStatus && matchesDateRange;
                    })
                    .collect(java.util.stream.Collectors.toList());
            
            updateTable(cars);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error filtering cars: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTable(List<Car> cars) {
        tableModel.setRowCount(0);

        for (Car car : cars) {
            Object[] row = {
                    car.getId(),
                    car.getBrand(),
                    car.getModel(),
                    car.getYear(),
                    car.getColor() != null ? car.getColor() : "N/A",
                    CurrencyUtils.formatCurrency(car.getPrice()),
                    car.getStatus().toString(),
                    car.getDaysInStock() + " days"
            };
            tableModel.addRow(row);
        }
    }

    public void showAddCarDialog() {
        CarDialog dialog = new CarDialog(SwingUtilities.getWindowAncestor(this), null, carService, currentAdmin);
        dialog.setVisible(true);

        if (dialog.isCarSaved()) {
            loadData();
            JOptionPane.showMessageDialog(this, "Car added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showEditCarDialog() {
        int selectedRow = carsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a car to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Long carId = (Long) tableModel.getValueAt(selectedRow, 0);
            Car car = carService.findById(carId).orElse(null);

            if (car != null) {
                CarDialog dialog = new CarDialog(SwingUtilities.getWindowAncestor(this), car, carService, currentAdmin);
                dialog.setVisible(true);

                if (dialog.isCarSaved()) {
                    loadData();
                    JOptionPane.showMessageDialog(this, "Car updated successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error editing car: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedCar() {
        int selectedRow = carsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a car to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int option = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this car?\nThis action cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (option == JOptionPane.YES_OPTION) {
            try {
                Long carId = (Long) tableModel.getValueAt(selectedRow, 0);
                carService.deleteCar(carId);
                loadData();
                JOptionPane.showMessageDialog(this, "Car deleted successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting car: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportToPdf() {
        try {
            List<Car> cars = carService.getAllCars();

            if (pdfService == null) {
                // Fallback if PDF service is not available
                JOptionPane.showMessageDialog(this,
                        "PDF export functionality will generate:\n" +
                                "- Complete cars inventory list\n" +
                                "- Car details with specifications\n" +
                                "- Inventory statistics and summary\n" +
                                "- Professional formatted report",
                        "PDF Export",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String outputPath = pdfService.getDefaultOutputPath();
            String fileName = pdfService.generateCarsInventoryReport(cars, outputPath);

            int option = JOptionPane.showConfirmDialog(this,
                    "Cars inventory report generated successfully!\n" +
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
