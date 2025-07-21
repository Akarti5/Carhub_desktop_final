package com.carhub.ui.dialogs;

import com.carhub.entity.Admin;
import com.carhub.entity.Car;
import com.carhub.entity.Client;
import com.carhub.entity.Sale;
import com.carhub.service.CarService;
import com.carhub.service.ClientService;
import com.carhub.service.SaleService;
import com.carhub.ui.components.ModernButton;
import com.carhub.ui.components.ModernTextField;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class SaleDialog extends JDialog {

    private SaleService saleService;
    private CarService carService;
    private ClientService clientService;
    private Admin currentAdmin;
    private Sale sale;
    private boolean saleSaved = false;

    // Form fields
    private JComboBox<Car> carCombo;
    private JComboBox<Client> clientCombo;
    private ModernTextField salePriceField;
    private JComboBox<Sale.PaymentMethod> paymentMethodCombo;
    private JComboBox<Sale.PaymentStatus> paymentStatusCombo;
    private ModernTextField downPaymentField;
    private ModernTextField invoiceNumberField;
    private JTextArea notesArea;

    public SaleDialog(Window parent, Sale sale, SaleService saleService, CarService carService,
                      ClientService clientService, Admin currentAdmin) {
        super(parent, sale == null ? "New Sale" : "View/Edit Sale", ModalityType.APPLICATION_MODAL);
        this.sale = sale;
        this.saleService = saleService;
        this.carService = carService;
        this.clientService = clientService;
        this.currentAdmin = currentAdmin;

        setupDialog();
        createComponents();
        if (sale != null) {
            populateFields();
        }
    }

    private void setupDialog() {
        setSize(500, 600);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(new Color(26, 28, 32));
    }

    private void createComponents() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(42, 45, 53));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        JLabel titleLabel = new JLabel(sale == null ? "New Sale" : "Sale Details");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Form content
        JPanel contentPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBackground(new Color(26, 28, 32));
        scrollPane.getViewport().setBackground(new Color(26, 28, 32));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(26, 28, 32));
        formPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // Car selection
        List<Car> availableCars = carService.findAvailableCars();
        carCombo = new JComboBox<>(availableCars.toArray(new Car[0]));
        carCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Car) {
                    setText(((Car) value).getDisplayName());
                }
                return this;
            }
        });
        addFormField(formPanel, gbc, row++, "Car:", carCombo);

        // Client selection
        List<Client> clients = clientService.findAll();
        clientCombo = new JComboBox<>(clients.toArray(new Client[0]));
        clientCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Client) {
                    setText(((Client) value).getFullName());
                }
                return this;
            }
        });
        addFormField(formPanel, gbc, row++, "Client:", clientCombo);

        // Sale Price
        addFormField(formPanel, gbc, row++, "Sale Price:", salePriceField = new ModernTextField(20));

        // Payment Method
        paymentMethodCombo = new JComboBox<>(Sale.PaymentMethod.values());
        addFormField(formPanel, gbc, row++, "Payment Method:", paymentMethodCombo);

        // Payment Status
        paymentStatusCombo = new JComboBox<>(Sale.PaymentStatus.values());
        addFormField(formPanel, gbc, row++, "Payment Status:", paymentStatusCombo);

        // Down Payment
        addFormField(formPanel, gbc, row++, "Down Payment:", downPaymentField = new ModernTextField(20));

        // Invoice Number
        addFormField(formPanel, gbc, row++, "Invoice Number:", invoiceNumberField = new ModernTextField(20));

        // Notes
        gbc.gridx = 0; gbc.gridy = row;
        JLabel notesLabel = new JLabel("Notes:");
        notesLabel.setForeground(Color.WHITE);
        notesLabel.setFont(new Font("SF Pro Text", Font.BOLD, 14));
        formPanel.add(notesLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        notesArea = new JTextArea(4, 20);
        notesArea.setBackground(new Color(47, 51, 73));
        notesArea.setForeground(Color.WHITE);
        notesArea.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        notesArea.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        notesScrollPane.setBorder(BorderFactory.createLineBorder(new Color(55, 65, 81)));
        formPanel.add(notesScrollPane, gbc);

        // Auto-fill price when car is selected
        carCombo.addActionListener(e -> {
            Car selectedCar = (Car) carCombo.getSelectedItem();
            if (selectedCar != null && salePriceField.getText().isEmpty()) {
                salePriceField.setText(selectedCar.getPrice().toString());
            }
        });

        return formPanel;
    }

    private void addFormField(JPanel parent, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0; gbc.weighty = 0;

        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("SF Pro Text", Font.BOLD, 14));
        parent.add(label, gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        if (field instanceof JComboBox) {
            JComboBox<?> combo = (JComboBox<?>) field;
            combo.setBackground(new Color(47, 51, 73));
            combo.setForeground(Color.WHITE);
            combo.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        }

        parent.add(field, gbc);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(42, 45, 53));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(16, 24, 16, 24));

        ModernButton cancelButton = new ModernButton("Cancel");
        ModernButton saveButton = new ModernButton("Save Sale");

        cancelButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> saveSale());

        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(8));
        buttonPanel.add(saveButton);

        return buttonPanel;
    }

    private void populateFields() {
        if (sale.getCar() != null) {
            carCombo.setSelectedItem(sale.getCar());
        }
        if (sale.getClient() != null) {
            clientCombo.setSelectedItem(sale.getClient());
        }
        salePriceField.setText(sale.getSalePrice().toString());
        paymentMethodCombo.setSelectedItem(sale.getPaymentMethod());
        paymentStatusCombo.setSelectedItem(sale.getPaymentStatus());
        if (sale.getDownPayment() != null) {
            downPaymentField.setText(sale.getDownPayment().toString());
        }
        invoiceNumberField.setText(sale.getInvoiceNumber());
        if (sale.getNotes() != null) {
            notesArea.setText(sale.getNotes());
        }
    }

    private void saveSale() {
        try {
            // Validate required fields
            if (carCombo.getSelectedItem() == null) {
                showError("Please select a car");
                return;
            }
            if (clientCombo.getSelectedItem() == null) {
                showError("Please select a client");
                return;
            }
            if (salePriceField.getText().trim().isEmpty()) {
                showError("Sale price is required");
                return;
            }

            // Create or update sale
            if (sale == null) {
                sale = new Sale();
                sale.setAdmin(currentAdmin);
            }

            sale.setCar((Car) carCombo.getSelectedItem());
            sale.setClient((Client) clientCombo.getSelectedItem());
            sale.setSalePrice(new BigDecimal(salePriceField.getText().trim()));
            sale.setPaymentMethod((Sale.PaymentMethod) paymentMethodCombo.getSelectedItem());
            sale.setPaymentStatus((Sale.PaymentStatus) paymentStatusCombo.getSelectedItem());

            if (!downPaymentField.getText().trim().isEmpty()) {
                sale.setDownPayment(new BigDecimal(downPaymentField.getText().trim()));
            }

            if (!invoiceNumberField.getText().trim().isEmpty()) {
                sale.setInvoiceNumber(invoiceNumberField.getText().trim());
            }

            sale.setNotes(notesArea.getText().trim());
            sale.setTotalAmount(sale.getSalePrice());

            if (sale.getId() == null) {
                saleService.createSale(sale);
            } else {
                saleService.updateSale(sale);
            }

            saleSaved = true;
            dispose();

        } catch (NumberFormatException e) {
            showError("Please enter valid numbers for prices");
        } catch (Exception e) {
            showError("Error saving sale: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isSaleSaved() {
        return saleSaved;
    }
}
