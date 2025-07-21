package com.carhub.ui.dialogs;

import com.carhub.entity.Admin;
import com.carhub.entity.Car;
import com.carhub.service.CarService;
import com.carhub.ui.components.ModernButton;
import com.carhub.ui.components.ModernTextField;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class CarDialog extends JDialog {

    private CarService carService;
    private Admin currentAdmin;
    private Car car;
    private boolean carSaved = false;

    // Form fields
    private ModernTextField brandField;
    private ModernTextField modelField;
    private ModernTextField yearField;
    private ModernTextField priceField;
    private ModernTextField costPriceField;
    private ModernTextField mileageField;
    private JComboBox<Car.FuelType> fuelTypeCombo;
    private JComboBox<Car.Transmission> transmissionCombo;
    private ModernTextField engineSizeField;
    private ModernTextField colorField;
    private ModernTextField vinField;
    private ModernTextField licensePlateField;
    private JComboBox<Car.Status> statusCombo;
    private JComboBox<Car.Condition> conditionCombo;
    private JTextArea descriptionArea;
    private ModernTextField locationField;

    public CarDialog(Window parent, Car car, CarService carService, Admin currentAdmin) {
        super(parent, car == null ? "Add New Car" : "Edit Car", ModalityType.APPLICATION_MODAL);
        this.car = car;
        this.carService = carService;
        this.currentAdmin = currentAdmin;

        setupDialog();
        createComponents();
        if (car != null) {
            populateFields();
        }
    }

    private void setupDialog() {
        setSize(600, 700);
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

        JLabel titleLabel = new JLabel(car == null ? "Add New Car" : "Edit Car");
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

        // Brand
        addFormField(formPanel, gbc, row++, "Brand:", brandField = new ModernTextField(20));

        // Model
        addFormField(formPanel, gbc, row++, "Model:", modelField = new ModernTextField(20));

        // Year
        addFormField(formPanel, gbc, row++, "Year:", yearField = new ModernTextField(20));

        // Price
        addFormField(formPanel, gbc, row++, "Price:", priceField = new ModernTextField(20));

        // Cost Price
        addFormField(formPanel, gbc, row++, "Cost Price:", costPriceField = new ModernTextField(20));

        // Mileage
        addFormField(formPanel, gbc, row++, "Mileage:", mileageField = new ModernTextField(20));

        // Fuel Type
        fuelTypeCombo = new JComboBox<>(Car.FuelType.values());
        addFormField(formPanel, gbc, row++, "Fuel Type:", fuelTypeCombo);

        // Transmission
        transmissionCombo = new JComboBox<>(Car.Transmission.values());
        addFormField(formPanel, gbc, row++, "Transmission:", transmissionCombo);

        // Engine Size
        addFormField(formPanel, gbc, row++, "Engine Size:", engineSizeField = new ModernTextField(20));

        // Color
        addFormField(formPanel, gbc, row++, "Color:", colorField = new ModernTextField(20));

        // VIN Number
        addFormField(formPanel, gbc, row++, "VIN Number:", vinField = new ModernTextField(20));

        // License Plate
        addFormField(formPanel, gbc, row++, "License Plate:", licensePlateField = new ModernTextField(20));

        // Status
        statusCombo = new JComboBox<>(Car.Status.values());
        addFormField(formPanel, gbc, row++, "Status:", statusCombo);

        // Condition
        conditionCombo = new JComboBox<>(Car.Condition.values());
        addFormField(formPanel, gbc, row++, "Condition:", conditionCombo);

        // Location
        addFormField(formPanel, gbc, row++, "Location:", locationField = new ModernTextField(20));

        // Description
        gbc.gridx = 0; gbc.gridy = row;
        JLabel descLabel = new JLabel("Description:");
        descLabel.setForeground(Color.WHITE);
        descLabel.setFont(new Font("SF Pro Text", Font.BOLD, 14));
        formPanel.add(descLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 1.0;
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setBackground(new Color(47, 51, 73));
        descriptionArea.setForeground(Color.WHITE);
        descriptionArea.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        descriptionArea.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setBorder(BorderFactory.createLineBorder(new Color(55, 65, 81)));
        formPanel.add(descScrollPane, gbc);

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
        ModernButton saveButton = new ModernButton("Save Car");

        cancelButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> saveCar());

        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(8));
        buttonPanel.add(saveButton);

        return buttonPanel;
    }

    private void populateFields() {
        brandField.setText(car.getBrand());
        modelField.setText(car.getModel());
        yearField.setText(String.valueOf(car.getYear()));
        priceField.setText(car.getPrice().toString());
        if (car.getCostPrice() != null) {
            costPriceField.setText(car.getCostPrice().toString());
        }
        if (car.getMileage() != null) {
            mileageField.setText(String.valueOf(car.getMileage()));
        }
        fuelTypeCombo.setSelectedItem(car.getFuelType());
        transmissionCombo.setSelectedItem(car.getTransmission());
        if (car.getEngineSize() != null) {
            engineSizeField.setText(car.getEngineSize());
        }
        if (car.getColor() != null) {
            colorField.setText(car.getColor());
        }
        if (car.getVinNumber() != null) {
            vinField.setText(car.getVinNumber());
        }
        if (car.getLicensePlate() != null) {
            licensePlateField.setText(car.getLicensePlate());
        }
        statusCombo.setSelectedItem(car.getStatus());
        conditionCombo.setSelectedItem(car.getCondition());
        if (car.getLocation() != null) {
            locationField.setText(car.getLocation());
        }
        if (car.getDescription() != null) {
            descriptionArea.setText(car.getDescription());
        }
    }

    private void saveCar() {
        try {
            // Validate required fields
            if (brandField.getText().trim().isEmpty()) {
                showError("Brand is required");
                return;
            }
            if (modelField.getText().trim().isEmpty()) {
                showError("Model is required");
                return;
            }
            if (yearField.getText().trim().isEmpty()) {
                showError("Year is required");
                return;
            }
            if (priceField.getText().trim().isEmpty()) {
                showError("Price is required");
                return;
            }

            // Create or update car
            if (car == null) {
                car = new Car();
                car.setCreatedBy(currentAdmin);
            }

            car.setBrand(brandField.getText().trim());
            car.setModel(modelField.getText().trim());
            car.setYear(Integer.parseInt(yearField.getText().trim()));
            car.setPrice(new BigDecimal(priceField.getText().trim()));

            if (!costPriceField.getText().trim().isEmpty()) {
                car.setCostPrice(new BigDecimal(costPriceField.getText().trim()));
            }
            if (!mileageField.getText().trim().isEmpty()) {
                car.setMileage(Integer.parseInt(mileageField.getText().trim()));
            }

            car.setFuelType((Car.FuelType) fuelTypeCombo.getSelectedItem());
            car.setTransmission((Car.Transmission) transmissionCombo.getSelectedItem());
            car.setEngineSize(engineSizeField.getText().trim());
            car.setColor(colorField.getText().trim());
            car.setVinNumber(vinField.getText().trim());
            car.setLicensePlate(licensePlateField.getText().trim());
            car.setStatus((Car.Status) statusCombo.getSelectedItem());
            car.setCondition((Car.Condition) conditionCombo.getSelectedItem());
            car.setLocation(locationField.getText().trim());
            car.setDescription(descriptionArea.getText().trim());

            carService.saveCar(car);
            carSaved = true;
            dispose();

        } catch (NumberFormatException e) {
            showError("Please enter valid numbers for year, price, cost price, and mileage");
        } catch (Exception e) {
            showError("Error saving car: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isCarSaved() {
        return carSaved;
    }
}
