package com.carhub.ui.panels;

import com.carhub.entity.Admin;
import com.carhub.entity.Client;
import com.carhub.service.ClientService;
import com.carhub.service.PdfService;
import com.carhub.ui.components.ModernButton;
import com.carhub.ui.components.ModernTable;
import com.carhub.ui.components.ModernTextField;
import com.carhub.ui.dialogs.ClientDialog;
import com.carhub.ui.main.MainWindow;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClientsPanel extends JPanel implements MainWindow.RefreshablePanel {

    private ClientService clientService;
    private Admin currentAdmin;
    private ModernTable clientsTable;
    private DefaultTableModel tableModel;
    private ModernTextField searchField;
    private JComboBox<String> typeFilter;

    @Autowired
    private PdfService pdfService;

    public ClientsPanel(ClientService clientService, Admin currentAdmin) {
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

        JLabel titleLabel = new JLabel("Client Management");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Manage your client database");
        subtitleLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(161, 161, 170));

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(subtitleLabel);

        // Search and filter section
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        searchPanel.setOpaque(false);

        searchField = new ModernTextField("Search clients...");
        searchField.setPreferredSize(new Dimension(200, 36));
        searchField.addActionListener(e -> filterData());

        typeFilter = new JComboBox<>(new String[]{"All Types", "INDIVIDUAL", "BUSINESS", "GOVERNMENT", "DEALER"});
        typeFilter.setBackground(new Color(47, 51, 73));
        typeFilter.setForeground(Color.WHITE);
        typeFilter.addActionListener(e -> filterData());

        ModernButton refreshBtn = new ModernButton("Refresh");
        refreshBtn.addActionListener(e -> loadData());

        searchPanel.add(new JLabel("Search:") {{ setForeground(Color.WHITE); }});
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Type:") {{ setForeground(Color.WHITE); }});
        searchPanel.add(typeFilter);
        searchPanel.add(refreshBtn);

        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);

        ModernButton addClientBtn = new ModernButton("Add Client");
        ModernButton editClientBtn = new ModernButton("Edit Client");
        ModernButton deleteClientBtn = new ModernButton("Delete Client");
        ModernButton exportPdfBtn = new ModernButton("Export PDF");

        addClientBtn.addActionListener(e -> showAddClientDialog());
        editClientBtn.addActionListener(e -> showEditClientDialog());
        deleteClientBtn.addActionListener(e -> deleteSelectedClient());
        exportPdfBtn.addActionListener(e -> exportToPdf());

        buttonPanel.add(addClientBtn);
        buttonPanel.add(editClientBtn);
        buttonPanel.add(deleteClientBtn);
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
        String[] columns = {"ID", "Name", "Email", "Phone", "City", "Type", "Registration Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        clientsTable = new ModernTable(tableModel);
        clientsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Double-click to edit
        clientsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    showEditClientDialog();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(clientsTable);
        scrollPane.setBackground(new Color(42, 45, 53));
        scrollPane.getViewport().setBackground(new Color(42, 45, 53));
        scrollPane.setBorder(null);

        return scrollPane;
    }

    private void loadData() {
        try {
            List<Client> clients = clientService.getAllClients();
            updateTable(clients);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading clients: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterData() {
        try {
            String searchText = searchField.getText().trim();
            String typeText = (String) typeFilter.getSelectedItem();

            List<Client> clients;
            if (searchText.isEmpty() && "All Types".equals(typeText)) {
                clients = clientService.getAllClients();
            } else {
                clients = clientService.getAllClients();
                clients = clients.stream()
                        .filter(client -> {
                            boolean matchesSearch = searchText.isEmpty() ||
                                    client.getFullName().toLowerCase().contains(searchText.toLowerCase()) ||
                                    (client.getEmail() != null && client.getEmail().toLowerCase().contains(searchText.toLowerCase())) ||
                                    client.getPhoneNumber().contains(searchText);

                            boolean matchesType = "All Types".equals(typeText) ||
                                    client.getCustomerType().toString().equals(typeText);

                            return matchesSearch && matchesType;
                        })
                        .toList();
            }
            updateTable(clients);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTable(List<Client> clients) {
        tableModel.setRowCount(0);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        for (Client client : clients) {
            Object[] row = {
                    client.getId(),
                    client.getFullName(),
                    client.getEmail() != null ? client.getEmail() : "N/A",
                    client.getPhoneNumber(),
                    client.getCity() != null ? client.getCity() : "N/A",
                    client.getCustomerType().toString(),
                    client.getRegistrationDate().format(dateFormatter)
            };
            tableModel.addRow(row);
        }
    }

    private void showAddClientDialog() {
        ClientDialog dialog = new ClientDialog(SwingUtilities.getWindowAncestor(this), null, clientService, currentAdmin);
        dialog.setVisible(true);

        if (dialog.isClientSaved()) {
            loadData();
            JOptionPane.showMessageDialog(this, "Client added successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showEditClientDialog() {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a client to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Long clientId = (Long) tableModel.getValueAt(selectedRow, 0);
            Client client = clientService.findById(clientId).orElse(null);

            if (client != null) {
                ClientDialog dialog = new ClientDialog(SwingUtilities.getWindowAncestor(this), client, clientService, currentAdmin);
                dialog.setVisible(true);

                if (dialog.isClientSaved()) {
                    loadData();
                    JOptionPane.showMessageDialog(this, "Client updated successfully!",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error editing client: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteSelectedClient() {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a client to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int option = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this client?\nThis action cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (option == JOptionPane.YES_OPTION) {
            try {
                Long clientId = (Long) tableModel.getValueAt(selectedRow, 0);
                clientService.deleteClient(clientId);
                loadData();
                JOptionPane.showMessageDialog(this, "Client deleted successfully.",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting client: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void exportToPdf() {
        try {
            List<Client> clients = clientService.getAllClients();

            if (pdfService == null) {
                JOptionPane.showMessageDialog(this,
                        "PDF export functionality will generate:\n" +
                                "- Complete clients list\n" +
                                "- Client contact information\n" +
                                "- Client statistics and demographics\n" +
                                "- Professional formatted report",
                        "PDF Export",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String outputPath = pdfService.getDefaultOutputPath();
            String fileName = pdfService.generateClientsReport(clients, outputPath);

            int option = JOptionPane.showConfirmDialog(this,
                    "Clients report generated successfully!\n" +
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
