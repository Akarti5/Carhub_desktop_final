package com.carhub.ui.dialogs;

import com.carhub.entity.Admin;
import com.carhub.entity.Client;
import com.carhub.service.ClientService;
import com.carhub.ui.components.ModernButton;
import com.carhub.ui.components.ModernTextField;

import javax.swing.*;
import java.awt.*;

public class ClientDialog extends JDialog {

    private ClientService clientService;
    private Admin currentAdmin;
    private Client client;
    private boolean clientSaved = false;

    // Form fields
    private ModernTextField firstNameField;
    private ModernTextField lastNameField;
    private ModernTextField emailField;
    private ModernTextField phoneField;
    private JTextArea addressArea;
    private ModernTextField cityField;
    private ModernTextField postalCodeField;
    private ModernTextField countryField;
    private JComboBox<Client.Gender> genderCombo;
    private JComboBox<Client.ContactMethod> contactMethodCombo;
    private JComboBox<Client.CustomerType> customerTypeCombo;
    private JTextArea notesArea;

    public ClientDialog(Window parent, Client client, ClientService clientService, Admin currentAdmin) {
        super(parent, client == null ? "Add New Client" : "Edit Client", ModalityType.APPLICATION_MODAL);
        this.client = client;
        this.clientService = clientService;
        this.currentAdmin = currentAdmin;

        setupDialog();
        createComponents();
        if (client != null) {
            populateFields();
        }
    }

    private void setupDialog() {
        setSize(500, 700);
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

        JLabel titleLabel = new JLabel(client == null ? "Add New Client" : "Edit Client");
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

        // First Name
        addFormField(formPanel, gbc, row++, "First Name:", firstNameField = new ModernTextField(20));

        // Last Name
        addFormField(formPanel, gbc, row++, "Last Name:", lastNameField = new ModernTextField(20));

        // Email
        addFormField(formPanel, gbc, row++, "Email:", emailField = new ModernTextField(20));

        // Phone
        addFormField(formPanel, gbc, row++, "Phone:", phoneField = new ModernTextField(20));

        // City
        addFormField(formPanel, gbc, row++, "City:", cityField = new ModernTextField(20));

        // Postal Code
        addFormField(formPanel, gbc, row++, "Postal Code:", postalCodeField = new ModernTextField(20));

        // Country
        addFormField(formPanel, gbc, row++, "Country:", countryField = new ModernTextField(20));

        // Gender
        genderCombo = new JComboBox<>(Client.Gender.values());
        addFormField(formPanel, gbc, row++, "Gender:", genderCombo);

        // Contact Method
        contactMethodCombo = new JComboBox<>(Client.ContactMethod.values());
        addFormField(formPanel, gbc, row++, "Preferred Contact:", contactMethodCombo);

        // Customer Type
        customerTypeCombo = new JComboBox<>(Client.CustomerType.values());
        addFormField(formPanel, gbc, row++, "Customer Type:", customerTypeCombo);

        // Address
        gbc.gridx = 0; gbc.gridy = row++;
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setForeground(Color.WHITE);
        addressLabel.setFont(new Font("SF Pro Text", Font.BOLD, 14));
        formPanel.add(addressLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 0.3;
        addressArea = new JTextArea(3, 20);
        addressArea.setBackground(new Color(47, 51, 73));
        addressArea.setForeground(Color.WHITE);
        addressArea.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        addressArea.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JScrollPane addressScrollPane = new JScrollPane(addressArea);
        addressScrollPane.setBorder(BorderFactory.createLineBorder(new Color(55, 65, 81)));
        formPanel.add(addressScrollPane, gbc);

        // Notes
        gbc.gridx = 0; gbc.gridy = row;
        JLabel notesLabel = new JLabel("Notes:");
        notesLabel.setForeground(Color.WHITE);
        notesLabel.setFont(new Font("SF Pro Text", Font.BOLD, 14));
        formPanel.add(notesLabel, gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 0.7;
        notesArea = new JTextArea(4, 20);
        notesArea.setBackground(new Color(47, 51, 73));
        notesArea.setForeground(Color.WHITE);
        notesArea.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        notesArea.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        notesScrollPane.setBorder(BorderFactory.createLineBorder(new Color(55, 65, 81)));
        formPanel.add(notesScrollPane, gbc);

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
        ModernButton saveButton = new ModernButton("Save Client");

        cancelButton.addActionListener(e -> dispose());
        saveButton.addActionListener(e -> saveClient());

        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(8));
        buttonPanel.add(saveButton);

        return buttonPanel;
    }

    private void populateFields() {
        firstNameField.setText(client.getFirstName());
        lastNameField.setText(client.getLastName());
        if (client.getEmail() != null) {
            emailField.setText(client.getEmail());
        }
        phoneField.setText(client.getPhoneNumber());
        if (client.getAddress() != null) {
            addressArea.setText(client.getAddress());
        }
        if (client.getCity() != null) {
            cityField.setText(client.getCity());
        }
        if (client.getPostalCode() != null) {
            postalCodeField.setText(client.getPostalCode());
        }
        if (client.getCountry() != null) {
            countryField.setText(client.getCountry());
        }
        if (client.getGender() != null) {
            genderCombo.setSelectedItem(client.getGender());
        }
        contactMethodCombo.setSelectedItem(client.getPreferredContact());
        customerTypeCombo.setSelectedItem(client.getCustomerType());
        if (client.getNotes() != null) {
            notesArea.setText(client.getNotes());
        }
    }

    private void saveClient() {
        try {
            // Validate required fields
            if (firstNameField.getText().trim().isEmpty()) {
                showError("First name is required");
                return;
            }
            if (lastNameField.getText().trim().isEmpty()) {
                showError("Last name is required");
                return;
            }
            if (phoneField.getText().trim().isEmpty()) {
                showError("Phone number is required");
                return;
            }

            // Create or update client
            if (client == null) {
                client = new Client();
                client.setCreatedBy(currentAdmin);
            }

            client.setFirstName(firstNameField.getText().trim());
            client.setLastName(lastNameField.getText().trim());
            client.setEmail(emailField.getText().trim().isEmpty() ? null : emailField.getText().trim());
            client.setPhoneNumber(phoneField.getText().trim());
            client.setAddress(addressArea.getText().trim().isEmpty() ? null : addressArea.getText().trim());
            client.setCity(cityField.getText().trim().isEmpty() ? null : cityField.getText().trim());
            client.setPostalCode(postalCodeField.getText().trim().isEmpty() ? null : postalCodeField.getText().trim());
            client.setCountry(countryField.getText().trim().isEmpty() ? "Madagascar" : countryField.getText().trim());
            client.setGender((Client.Gender) genderCombo.getSelectedItem());
            client.setPreferredContact((Client.ContactMethod) contactMethodCombo.getSelectedItem());
            client.setCustomerType((Client.CustomerType) customerTypeCombo.getSelectedItem());
            client.setNotes(notesArea.getText().trim().isEmpty() ? null : notesArea.getText().trim());

            clientService.saveClient(client);
            clientSaved = true;
            dispose();

        } catch (Exception e) {
            showError("Error saving client: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public boolean isClientSaved() {
        return clientSaved;
    }
}
