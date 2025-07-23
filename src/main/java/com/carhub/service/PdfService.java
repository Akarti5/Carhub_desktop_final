package com.carhub.service;

import com.carhub.entity.Car;
import com.carhub.entity.Client;
import com.carhub.entity.Sale;
import com.carhub.util.CurrencyUtils;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {

    private static final String COMPANY_NAME = "CarHub";
    private static final String COMPANY_ADDRESS = "carhub Business Street, Fianarantsoa, Madagascar";
    private static final String COMPANY_PHONE = "+261 34 46 481 31";
    private static final String COMPANY_EMAIL = "info@carhub.com";

    public String generateCarsInventoryReport(List<Car> cars, String outputPath) throws Exception {
        String fileName = outputPath + "/cars_inventory_" + System.currentTimeMillis() + ".pdf";

        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Add header
        addReportHeader(document, "Cars Inventory Report");

        // Add summary
        addInventorySummary(document, cars);

        // Add cars table
        addCarsTable(document, cars);

        // Add footer
        addReportFooter(document);

        document.close();
        return fileName;
    }

    public String generateSalesReport(List<Sale> sales, String outputPath) throws Exception {
        String fileName = outputPath + "/sales_report_" + System.currentTimeMillis() + ".pdf";

        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Add header
        addReportHeader(document, "Sales Report");

        // Add summary
        addSalesSummary(document, sales);

        // Add sales table
        addSalesTable(document, sales);

        // Add footer
        addReportFooter(document);

        document.close();
        return fileName;
    }

    public String generateClientsReport(List<Client> clients, String outputPath) throws Exception {
        String fileName = outputPath + "/clients_report_" + System.currentTimeMillis() + ".pdf";

        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Add header
        addReportHeader(document, "Clients Report");

        // Add summary
        addClientsSummary(document, clients);

        // Add clients table
        addClientsTable(document, clients);

        // Add footer
        addReportFooter(document);

        document.close();
        return fileName;
    }

    public String generateSaleInvoice(Sale sale, String outputPath) throws Exception {
        String fileName = outputPath + "/invoice_" + sale.getInvoiceNumber() + ".pdf";

        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Add invoice header
        addInvoiceHeader(document, sale);

        // Add invoice details
        addInvoiceDetails(document, sale);

        // Add invoice footer
        addInvoiceFooter(document, sale);

        document.close();
        return fileName;
    }

    private void addReportHeader(Document document, String reportTitle) throws Exception {
        PdfFont boldFont = PdfFontFactory.createFont();

        // Company name
        Paragraph companyName = new Paragraph(COMPANY_NAME)
                .setFont(boldFont)
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);
        document.add(companyName);

        // Company details
        Paragraph companyDetails = new Paragraph(COMPANY_ADDRESS + " | " + COMPANY_PHONE + " | " + COMPANY_EMAIL)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(companyDetails);

        // Report title
        Paragraph title = new Paragraph(reportTitle)
                .setFont(boldFont)
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(title);

        // Report date
        Paragraph reportDate = new Paragraph("Generated on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(reportDate);
    }

    private void addInventorySummary(Document document, List<Car> cars) throws Exception {
        PdfFont boldFont = PdfFontFactory.createFont();

        document.add(new Paragraph("Inventory Summary").setFont(boldFont).setFontSize(14).setMarginBottom(10));

        long totalCars = cars.size();
        long availableCars = cars.stream().filter(car -> car.getStatus() == Car.Status.AVAILABLE).count();
        long soldCars = cars.stream().filter(car -> car.getStatus() == Car.Status.SOLD).count();

        BigDecimal totalValue = cars.stream()
                .filter(car -> car.getStatus() == Car.Status.AVAILABLE)
                .map(Car::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Table summaryTable = new Table(2);
        summaryTable.setWidth(UnitValue.createPercentValue(50));

        addSummaryRow(summaryTable, "Total Cars:", String.valueOf(totalCars));
        addSummaryRow(summaryTable, "Available Cars:", String.valueOf(availableCars));
        addSummaryRow(summaryTable, "Sold Cars:", String.valueOf(soldCars));
        addSummaryRow(summaryTable, "Total Inventory Value:", CurrencyUtils.formatCurrency(totalValue));

        document.add(summaryTable);
        document.add(new Paragraph("\n"));
    }

    private void addSalesSummary(Document document, List<Sale> sales) throws Exception {
        PdfFont boldFont = PdfFontFactory.createFont();

        document.add(new Paragraph("Sales Summary").setFont(boldFont).setFontSize(14).setMarginBottom(10));

        long totalSales = sales.size();
        BigDecimal totalRevenue = sales.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalProfit = sales.stream()
                .map(sale -> sale.getProfit() != null ? sale.getProfit() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Table summaryTable = new Table(2);
        summaryTable.setWidth(UnitValue.createPercentValue(50));

        addSummaryRow(summaryTable, "Total Sales:", String.valueOf(totalSales));
        addSummaryRow(summaryTable, "Total Revenue:", CurrencyUtils.formatCurrency(totalRevenue));
        addSummaryRow(summaryTable, "Total Profit:", CurrencyUtils.formatCurrency(totalProfit));

        document.add(summaryTable);
        document.add(new Paragraph("\n"));
    }

    private void addClientsSummary(Document document, List<Client> clients) throws Exception {
        PdfFont boldFont = PdfFontFactory.createFont();

        document.add(new Paragraph("Clients Summary").setFont(boldFont).setFontSize(14).setMarginBottom(10));

        long totalClients = clients.size();
        long individualClients = clients.stream().filter(client -> client.getCustomerType() == Client.CustomerType.INDIVIDUAL).count();
        long businessClients = clients.stream().filter(client -> client.getCustomerType() == Client.CustomerType.BUSINESS).count();

        Table summaryTable = new Table(2);
        summaryTable.setWidth(UnitValue.createPercentValue(50));

        addSummaryRow(summaryTable, "Total Clients:", String.valueOf(totalClients));
        addSummaryRow(summaryTable, "Individual Clients:", String.valueOf(individualClients));
        addSummaryRow(summaryTable, "Business Clients:", String.valueOf(businessClients));

        document.add(summaryTable);
        document.add(new Paragraph("\n"));
    }

    private void addSummaryRow(Table table, String label, String value) throws Exception {
        PdfFont boldFont = PdfFontFactory.createFont();

        table.addCell(new Cell().add(new Paragraph(label).setFont(boldFont)));
        table.addCell(new Cell().add(new Paragraph(value)));
    }

    private void addCarsTable(Document document, List<Car> cars) throws Exception {
        PdfFont boldFont = PdfFontFactory.createFont();

        document.add(new Paragraph("Cars Details").setFont(boldFont).setFontSize(14).setMarginBottom(10));

        Table table = new Table(new float[]{1, 2, 2, 1, 2, 2, 2});
        table.setWidth(UnitValue.createPercentValue(100));

        // Header
        table.addHeaderCell(new Cell().add(new Paragraph("ID").setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Make").setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Model").setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Year").setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Color").setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Price").setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Status").setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));

        // Data rows
        for (Car car : cars) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(car.getId()))));
            table.addCell(new Cell().add(new Paragraph(car.getBrand())));
            table.addCell(new Cell().add(new Paragraph(car.getModel())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(car.getYear()))));
            table.addCell(new Cell().add(new Paragraph(car.getColor() != null ? car.getColor() : "N/A")));
            table.addCell(new Cell().add(new Paragraph(CurrencyUtils.formatCurrency(car.getPrice()))));
            table.addCell(new Cell().add(new Paragraph(car.getStatus().toString())));
        }

        document.add(table);
    }

    private void addSalesTable(Document document, List<Sale> sales) throws Exception {
        PdfFont boldFont = PdfFontFactory.createFont();

        document.add(new Paragraph("Sales Details").setFont(boldFont).setFontSize(14).setMarginBottom(10));

        Table table = new Table(new float[]{1, 2, 2, 3, 3, 2, 2});
        table.setWidth(UnitValue.createPercentValue(100));

        // Header
        table.addHeaderCell(new Cell().add(new Paragraph("ID").setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Invoice").setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Date").setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Car").setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Client").setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Amount").setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Status").setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));

        // Data rows
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        for (Sale sale : sales) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(sale.getId()))));
            table.addCell(new Cell().add(new Paragraph(sale.getInvoiceNumber())));
            table.addCell(new Cell().add(new Paragraph(sale.getSaleDate().format(dateFormatter))));
            table.addCell(new Cell().add(new Paragraph(sale.getCar().getDisplayName())));
            table.addCell(new Cell().add(new Paragraph(sale.getClient().getFullName())));
            table.addCell(new Cell().add(new Paragraph(CurrencyUtils.formatCurrency(sale.getTotalAmount()))));
            table.addCell(new Cell().add(new Paragraph(sale.getPaymentStatus().toString())));
        }

        document.add(table);
    }

    private void addClientsTable(Document document, List<Client> clients) throws Exception {
        PdfFont boldFont = PdfFontFactory.createFont();

        document.add(new Paragraph("Clients Details").setFont(boldFont).setFontSize(14).setMarginBottom(10));

        Table table = new Table(new float[]{1, 3, 3, 2, 2, 2, 2});
        table.setWidth(UnitValue.createPercentValue(100));

        // Header
        table.addHeaderCell(new Cell().add(new Paragraph("ID").setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Name").setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Email").setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Phone").setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("City").setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Type").setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        table.addHeaderCell(new Cell().add(new Paragraph("Registered").setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));

        // Data rows
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        for (Client client : clients) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(client.getId()))));
            table.addCell(new Cell().add(new Paragraph(client.getFullName())));
            table.addCell(new Cell().add(new Paragraph(client.getEmail() != null ? client.getEmail() : "N/A")));
            table.addCell(new Cell().add(new Paragraph(client.getPhoneNumber())));
            table.addCell(new Cell().add(new Paragraph(client.getCity() != null ? client.getCity() : "N/A")));
            table.addCell(new Cell().add(new Paragraph(client.getCustomerType().toString())));
            table.addCell(new Cell().add(new Paragraph(client.getRegistrationDate().format(dateFormatter))));
        }

        document.add(table);
    }

    private void addInvoiceHeader(Document document, Sale sale) throws Exception {
        PdfFont boldFont = PdfFontFactory.createFont();

        // Company info and invoice title
        Table headerTable = new Table(2);
        headerTable.setWidth(UnitValue.createPercentValue(100));

        // Left side - Company info
        Cell companyCell = new Cell();
        companyCell.add(new Paragraph(COMPANY_NAME).setFont(boldFont).setFontSize(18));
        companyCell.add(new Paragraph(COMPANY_ADDRESS).setFontSize(10));
        companyCell.add(new Paragraph(COMPANY_PHONE).setFontSize(10));
        companyCell.add(new Paragraph(COMPANY_EMAIL).setFontSize(10));
        companyCell.setBorder(null);

        // Right side - Invoice info
        Cell invoiceCell = new Cell();
        invoiceCell.add(new Paragraph("INVOICE").setFont(boldFont).setFontSize(20).setTextAlignment(TextAlignment.RIGHT));
        invoiceCell.add(new Paragraph("Invoice #: " + sale.getInvoiceNumber()).setFontSize(12).setTextAlignment(TextAlignment.RIGHT));
        invoiceCell.add(new Paragraph("Date: " + sale.getSaleDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))).setFontSize(12).setTextAlignment(TextAlignment.RIGHT));
        invoiceCell.setBorder(null);

        headerTable.addCell(companyCell);
        headerTable.addCell(invoiceCell);

        document.add(headerTable);
        document.add(new Paragraph("\n"));
    }

    private void addInvoiceDetails(Document document, Sale sale) throws Exception {
        PdfFont boldFont = PdfFontFactory.createFont();

        // Client information
        document.add(new Paragraph("Bill To:").setFont(boldFont).setFontSize(12));
        document.add(new Paragraph(sale.getClient().getFullName()).setFontSize(11));
        if (sale.getClient().getAddress() != null) {
            document.add(new Paragraph(sale.getClient().getAddress()).setFontSize(10));
        }
        document.add(new Paragraph(sale.getClient().getPhoneNumber()).setFontSize(10));
        if (sale.getClient().getEmail() != null) {
            document.add(new Paragraph(sale.getClient().getEmail()).setFontSize(10));
        }
        document.add(new Paragraph("\n"));

        // Invoice items table
        Table itemsTable = new Table(new float[]{4, 1, 2, 2});
        itemsTable.setWidth(UnitValue.createPercentValue(100));

        // Header
        itemsTable.addHeaderCell(new Cell().add(new Paragraph("Description").setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        itemsTable.addHeaderCell(new Cell().add(new Paragraph("Qty").setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        itemsTable.addHeaderCell(new Cell().add(new Paragraph("Unit Price").setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));
        itemsTable.addHeaderCell(new Cell().add(new Paragraph("Total").setFont(boldFont)).setBackgroundColor(ColorConstants.LIGHT_GRAY));

        // Car item
        itemsTable.addCell(new Cell().add(new Paragraph(sale.getCar().getDisplayName())));
        itemsTable.addCell(new Cell().add(new Paragraph("1")));
        itemsTable.addCell(new Cell().add(new Paragraph(CurrencyUtils.formatCurrency(sale.getSalePrice()))));
        itemsTable.addCell(new Cell().add(new Paragraph(CurrencyUtils.formatCurrency(sale.getSalePrice()))));

        document.add(itemsTable);
        document.add(new Paragraph("\n"));

        // Totals section
        Table totalsTable = new Table(new float[]{3, 1});
        totalsTable.setWidth(UnitValue.createPercentValue(50));
        totalsTable.setHorizontalAlignment(com.itextpdf.layout.properties.HorizontalAlignment.RIGHT);

        totalsTable.addCell(new Cell().add(new Paragraph("Subtotal:").setFont(boldFont)).setBorder(null));
        totalsTable.addCell(new Cell().add(new Paragraph(CurrencyUtils.formatCurrency(sale.getSalePrice()))).setBorder(null));

        if (sale.getDiscountAmount() != null && sale.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            totalsTable.addCell(new Cell().add(new Paragraph("Discount:").setFont(boldFont)).setBorder(null));
            totalsTable.addCell(new Cell().add(new Paragraph("-" + CurrencyUtils.formatCurrency(sale.getDiscountAmount()))).setBorder(null));
        }

        if (sale.getTaxAmount() != null && sale.getTaxAmount().compareTo(BigDecimal.ZERO) > 0) {
            totalsTable.addCell(new Cell().add(new Paragraph("Tax:").setFont(boldFont)).setBorder(null));
            totalsTable.addCell(new Cell().add(new Paragraph(CurrencyUtils.formatCurrency(sale.getTaxAmount()))).setBorder(null));
        }

        totalsTable.addCell(new Cell().add(new Paragraph("Total:").setFont(boldFont).setFontSize(14)).setBorder(null));
        totalsTable.addCell(new Cell().add(new Paragraph(CurrencyUtils.formatCurrency(sale.getTotalAmount())).setFont(boldFont).setFontSize(14)).setBorder(null));

        document.add(totalsTable);
    }

    private void addInvoiceFooter(Document document, Sale sale) throws Exception {
        document.add(new Paragraph("\n\n"));

        // Payment information
        if (sale.getPaymentMethod() != null) {
            document.add(new Paragraph("Payment Method: " + sale.getPaymentMethod().toString()).setFontSize(10));
        }
        document.add(new Paragraph("Payment Status: " + sale.getPaymentStatus().toString()).setFontSize(10));

        if (sale.getNotes() != null && !sale.getNotes().isEmpty()) {
            document.add(new Paragraph("\nNotes:").setFontSize(10));
            document.add(new Paragraph(sale.getNotes()).setFontSize(9));
        }

        // Footer
        document.add(new Paragraph("\n\nThank you for your business!").setTextAlignment(TextAlignment.CENTER).setFontSize(12));
        document.add(new Paragraph("For any questions, please contact us at " + COMPANY_PHONE).setTextAlignment(TextAlignment.CENTER).setFontSize(10));
    }

    private void addReportFooter(Document document) throws Exception {
        document.add(new Paragraph("\n\n"));
        document.add(new Paragraph("Report generated by " + COMPANY_NAME + " - Car Dealership Management System")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(8)
                .setFontColor(ColorConstants.GRAY));
    }

    public String generateInvoice(Sale sale, String outputPath) throws Exception {
        String fileName = outputPath + "/facture_" + sale.getInvoiceNumber() + "_" + System.currentTimeMillis() + ".pdf";

        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        try {
            // Invoice Header
            Paragraph header = new Paragraph("FACTURE N°" + sale.getInvoiceNumber())
                    .setFontSize(16)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(header);
            document.add(new Paragraph("\n"));

            // Company Info (right aligned)
            Paragraph companyInfo = new Paragraph()
                    .add(COMPANY_NAME + "\n")
                    .add(COMPANY_ADDRESS + "\n")
                    .add("Tél: " + COMPANY_PHONE + "\n")
                    .add("Email: " + COMPANY_EMAIL)
                    .setTextAlignment(TextAlignment.RIGHT);
            document.add(companyInfo);
            document.add(new Paragraph("\n\n"));

            // Client Info (left aligned)
            Paragraph clientInfo = new Paragraph()
                    .add("Date de facturation: " + 
                         sale.getSaleDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + "\n")
                    .add("Nom du client: " + sale.getClient().getFullName() + "\n")
                    .add("Contact: " + sale.getClient().getPhoneNumber())
                    .setTextAlignment(TextAlignment.LEFT);
            document.add(clientInfo);
            document.add(new Paragraph("\n\n"));

            // Items Table
            float[] columnWidths = {10, 3, 4};
            Table table = new Table(columnWidths);
            
            // Table Header
            table.addHeaderCell(new Cell().add(new Paragraph("Désignation").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Quantité").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Prix").setBold()));
            
            // Table Rows
            table.addCell(new Cell().add(new Paragraph(sale.getCar().getDisplayName())));
            table.addCell(new Cell().add(new Paragraph("1")).setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph(CurrencyUtils.formatCurrency(sale.getSalePrice())))
                    .setTextAlignment(TextAlignment.RIGHT));
            
            document.add(table);
            document.add(new Paragraph("\n\n"));
            
            // Total
            Paragraph total = new Paragraph()
                    .add("Total: " + CurrencyUtils.formatCurrency(sale.getTotalAmount()))
                    .setTextAlignment(TextAlignment.RIGHT);
            document.add(total);
            
            // Amount in words
            String amountInWords = convertToWords(sale.getTotalAmount().intValue()) + " ariary";
            Paragraph amountWords = new Paragraph("\n" + 
                    "Arrêté par la présente facture à la somme de: " + amountInWords)
                    .setItalic();
            document.add(amountWords);
            
            // Footer
            document.add(new Paragraph("\n\n\n"));
            Paragraph footer = new Paragraph("Merci pour votre confiance!")
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(footer);
            
            return fileName;
        } finally {
            document.close();
        }
    }
    
    // Helper method to convert number to words in French
    private String convertToWords(int number) {
        if (number == 0) {
            return "zéro";
        }
        
        String[] units = {"", "un", "deux", "trois", "quatre", "cinq", "six", "sept", "huit", "neuf", "dix",
                "onze", "douze", "treize", "quatorze", "quinze", "seize", "dix-sept", "dix-huit", "dix-neuf"};
        String[] tens = {"", "dix", "vingt", "trente", "quarante", "cinquante", "soixante", "soixante-dix", "quatre-vingt", "quatre-vingt-dix"};
        
        if (number < 20) {
            return units[number];
        }
        
        if (number < 100) {
            if (number % 10 == 0) {
                return tens[number / 10];
            } else if (number < 70 || (number > 79 && number < 90)) {
                return tens[number / 10] + "-" + units[number % 10];
            } else if (number < 80) {
                return "soixante-et-onze";
            } else {
                return tens[number / 10] + "-" + units[number % 10];
            }
        }
        
        if (number < 1000) {
            if (number % 100 == 0) {
                return units[number / 100] + " cent";
            } else {
                return units[number / 100] + " cent " + convertToWords(number % 100);
            }
        }
        
        if (number < 1000000) {
            if (number % 1000 == 0) {
                return convertToWords(number / 1000) + " mille";
            } else {
                return convertToWords(number / 1000) + " mille " + convertToWords(number % 1000);
            }
        }
        
        if (number % 1000000 == 0) {
            return convertToWords(number / 1000000) + " million";
        } else {
            return convertToWords(number / 1000000) + " million " + convertToWords(number % 1000000);
        }
    }

    public String getDefaultOutputPath() {
        String userHome = System.getProperty("user.home");
        String outputDir = userHome + File.separator + "CarHub_Factures";

        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return outputDir;
    }
}
