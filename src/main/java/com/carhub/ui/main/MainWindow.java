package com.carhub.ui.main;

import com.carhub.entity.Admin;
import com.carhub.service.*;
import com.carhub.ui.components.ModernButton;
import com.carhub.ui.dialogs.LoginDialog;
import com.carhub.ui.panels.*;
import com.formdev.flatlaf.FlatDarkLaf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

@Component
public class MainWindow extends JFrame {

    @Autowired
    private AdminService adminService;

    @Autowired
    private CarService carService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private SaleService saleService;

    @Autowired
    private SystemSettingService systemSettingService;
    
    @Autowired
    private PdfService pdfService;

    private Admin currentAdmin;
    private NavigationPanel navigationPanel;
    private JPanel contentPanel;
    private CardLayout contentLayout;

    // Panels
    private DashboardPanel dashboardPanel;
    private CarsPanel carsPanel;
    private SalesPanel salesPanel;
    private ClientsPanel clientsPanel;
    private ReportsPanel reportsPanel;
    private SettingsPanel settingsPanel;

    public MainWindow() {
        // This will be called after Spring injection
    }

    public void initialize(Admin admin) {
        this.currentAdmin = admin;

        // Set Look and Feel
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setupWindow();
        createComponents();
        layoutComponents();
        setupEventHandlers();

        setVisible(true);
    }

    private void setupWindow() {
        setTitle("CarHub - Car Dealership Management System");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1200, 800));

        // Set application icon
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage(
                    getClass().getResource("/images/carhub-icon.png")));
        } catch (Exception e) {
            // Icon not found, continue without it
        }

        // Set background color
        getContentPane().setBackground(new Color(26, 28, 32));
    }

    private void createComponents() {
        // Create navigation panel
        navigationPanel = new NavigationPanel(currentAdmin);

        // Create content panel with CardLayout
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(new Color(26, 28, 32));

        // Create panels
        dashboardPanel = new DashboardPanel(carService, saleService, clientService);
        carsPanel = new CarsPanel(carService, currentAdmin);
        salesPanel = new SalesPanel(saleService, carService, clientService, pdfService, currentAdmin);
        clientsPanel = new ClientsPanel(clientService, currentAdmin);
        reportsPanel = new ReportsPanel(saleService, carService, clientService);
        settingsPanel = new SettingsPanel(systemSettingService, adminService, currentAdmin);

        // Add panels to content panel
        contentPanel.add(dashboardPanel, "dashboard");
        contentPanel.add(carsPanel, "cars");
        contentPanel.add(salesPanel, "sales");
        contentPanel.add(clientsPanel, "clients");
        contentPanel.add(reportsPanel, "reports");
        contentPanel.add(settingsPanel, "settings");
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        // Add navigation panel to the left
        add(navigationPanel, BorderLayout.WEST);

        // Add content panel to the center
        add(contentPanel, BorderLayout.CENTER);

        // Create top toolbar
        JPanel toolbar = createToolbar();
        add(toolbar, BorderLayout.NORTH);
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(new Color(42, 45, 53));
        toolbar.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        // Left side - App title
        JLabel titleLabel = new JLabel("CarHub");
        titleLabel.setFont(new Font("SF Pro Display", Font.BOLD, 20));
        titleLabel.setForeground(new Color(222, 255, 41));

        // Right side - User info and actions
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightPanel.setOpaque(false);

        // User info
        JLabel userLabel = new JLabel("Welcome, " + currentAdmin.getFullName());
        userLabel.setFont(new Font("SF Pro Text", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);

        // Quick action buttons
        ModernButton addCarBtn = new ModernButton("Add Car");
        ModernButton newSaleBtn = new ModernButton("New Sale");
        ModernButton refreshBtn = new ModernButton("Refresh");

        rightPanel.add(userLabel);
        rightPanel.add(Box.createHorizontalStrut(16));
        rightPanel.add(addCarBtn);
        rightPanel.add(newSaleBtn);
        rightPanel.add(refreshBtn);

        toolbar.add(titleLabel, BorderLayout.WEST);
        toolbar.add(rightPanel, BorderLayout.EAST);

        // Add action listeners
        addCarBtn.addActionListener(e -> {
            showPanel("cars");
            carsPanel.showAddCarDialog();
        });

        newSaleBtn.addActionListener(e -> {
            showPanel("sales");
            salesPanel.showNewSaleDialog();
        });

        refreshBtn.addActionListener(e -> refreshCurrentPanel());

        return toolbar;
    }

    private void setupEventHandlers() {
        // Window closing event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(
                        MainWindow.this,
                        "Are you sure you want to exit CarHub?",
                        "Confirm Exit",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        // Navigation panel events - Fixed: Use lambda instead of method reference
        navigationPanel.setNavigationListener(e -> {
            String panelName = e.getActionCommand();
            showPanel(panelName);
        });
    }

    public void showPanel(String panelName) {
        SwingUtilities.invokeLater(() -> {
            contentLayout.show(contentPanel, panelName);
            refreshCurrentPanel();
        });
    }

    private void refreshCurrentPanel() {
        SwingUtilities.invokeLater(() -> {
            // Refresh the currently visible panel
            java.awt.Component[] components = contentPanel.getComponents();
            for (java.awt.Component component : components) {
                if (component.isVisible() && component instanceof RefreshablePanel) {
                    ((RefreshablePanel) component).refresh();
                    break;
                }
            }
        });
    }

    public Admin getCurrentAdmin() {
        return currentAdmin;
    }

    // Interface for panels that can be refreshed
    public interface RefreshablePanel {
        void refresh();
    }
}
