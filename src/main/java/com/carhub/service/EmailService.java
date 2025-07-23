package com.carhub.service;

import com.carhub.entity.Sale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

@Service
public class EmailService {

    @Value("${mail.smtp.host}")
    private String smtpHost;

    @Value("${mail.smtp.port}")
    private String smtpPort;

    @Value("${mail.smtp.auth}")
    private String smtpAuth;

    @Value("${mail.smtp.starttls.enable}")
    private String smtpStartTls;

    @Value("${mail.username}")
    private String emailUsername;

    @Value("${mail.password}")
    private String emailPassword;

    @Value("${app.company.name}")
    private String companyName;

    @Value("${app.company.address}")
    private String companyAddress;

    @Value("${app.company.phone}")
    private String companyPhone;

    @Value("${app.company.email}")
    private String companyEmail;

    public void sendSaleConfirmationEmail(Sale sale) {
        try {
            if (sale.getClient().getEmail() == null || sale.getClient().getEmail().trim().isEmpty()) {
                System.out.println("Client email is not available for sale: " + sale.getInvoiceNumber());
                return;
            }

            Properties props = new Properties();
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.port", smtpPort);
            props.put("mail.smtp.auth", smtpAuth);
            props.put("mail.smtp.starttls.enable", smtpStartTls);

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailUsername, emailPassword);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailUsername, companyName));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(sale.getClient().getEmail()));
            message.setSubject("Sale Confirmation - Invoice #" + sale.getInvoiceNumber());

            String htmlContent = generateSaleEmailContent(sale);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            System.out.println("Sale confirmation email sent successfully to: " + sale.getClient().getEmail());

        } catch (Exception e) {
            System.err.println("Failed to send sale confirmation email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String generateSaleEmailContent(Sale sale) {
        StringBuilder content = new StringBuilder();
        
        content.append("<!DOCTYPE html>");
        content.append("<html>");
        content.append("<head>");
        content.append("<meta charset='UTF-8'>");
        content.append("<style>");
        content.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        content.append(".container { max-width: 600px; margin: 0 auto; padding: 20px; }");
        content.append(".header { background-color: #2c3e50; color: white; padding: 20px; text-align: center; }");
        content.append(".content { padding: 20px; background-color: #f9f9f9; }");
        content.append(".details { background-color: white; padding: 15px; margin: 10px 0; border-radius: 5px; }");
        content.append(".footer { background-color: #34495e; color: white; padding: 15px; text-align: center; }");
        content.append(".highlight { color: #e74c3c; font-weight: bold; }");
        content.append("table { width: 100%; border-collapse: collapse; margin: 10px 0; }");
        content.append("th, td { padding: 8px; text-align: left; border-bottom: 1px solid #ddd; }");
        content.append("th { background-color: #f2f2f2; }");
        content.append("</style>");
        content.append("</head>");
        content.append("<body>");
        
        content.append("<div class='container'>");
        
        // Header
        content.append("<div class='header'>");
        content.append("<h1>").append(companyName).append("</h1>");
        content.append("<h2>Sale Confirmation</h2>");
        content.append("</div>");
        
        // Content
        content.append("<div class='content'>");
        content.append("<h3>Dear ").append(sale.getClient().getFullName()).append(",</h3>");
        content.append("<p>Thank you for your purchase! We are pleased to confirm your vehicle purchase with the following details:</p>");
        
        // Sale Details
        content.append("<div class='details'>");
        content.append("<h4>Purchase Information</h4>");
        content.append("<table>");
        content.append("<tr><th>Invoice Number:</th><td class='highlight'>").append(sale.getInvoiceNumber()).append("</td></tr>");
        content.append("<tr><th>Sale Date:</th><td>").append(sale.getSaleDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm"))).append("</td></tr>");
        content.append("<tr><th>Payment Status:</th><td>").append(sale.getPaymentStatus().toString()).append("</td></tr>");
        content.append("<tr><th>Payment Method:</th><td>").append(sale.getPaymentMethod().toString()).append("</td></tr>");
        content.append("</table>");
        content.append("</div>");
        
        // Vehicle Details
        content.append("<div class='details'>");
        content.append("<h4>Vehicle Information</h4>");
        content.append("<table>");
        content.append("<tr><th>Vehicle:</th><td>").append(sale.getCar().getDisplayName()).append("</td></tr>");
        content.append("<tr><th>Year:</th><td>").append(sale.getCar().getYear()).append("</td></tr>");
        content.append("<tr><th>VIN:</th><td>").append(sale.getCar().getVinNumber() != null ? sale.getCar().getVinNumber() : "N/A").append("</td></tr>");
        content.append("<tr><th>Mileage:</th><td>").append(sale.getCar().getMileage()).append(" km</td></tr>");
        content.append("</table>");
        content.append("</div>");
        
        // Financial Details
        content.append("<div class='details'>");
        content.append("<h4>Financial Summary</h4>");
        content.append("<table>");
        content.append("<tr><th>Sale Price:</th><td>Ar").append(formatAmount(sale.getSalePrice())).append("</td></tr>");
        
        if (sale.getDiscountAmount() != null && sale.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            content.append("<tr><th>Discount:</th><td>-Ar").append(formatAmount(sale.getDiscountAmount())).append("</td></tr>");
        }
        
        if (sale.getTaxAmount() != null && sale.getTaxAmount().compareTo(BigDecimal.ZERO) > 0) {
            content.append("<tr><th>Tax:</th><td>Ar").append(formatAmount(sale.getTaxAmount())).append("</td></tr>");
        }
        
        content.append("<tr><th><strong>Total Amount:</strong></th><td class='highlight'><strong>Ar").append(formatAmount(sale.getTotalAmount())).append("</strong></td></tr>");
        
        if (sale.getDownPayment() != null && sale.getDownPayment().compareTo(BigDecimal.ZERO) > 0) {
            content.append("<tr><th>Down Payment:</th><td>Ar").append(formatAmount(sale.getDownPayment())).append("</td></tr>");
        }
        
        if (sale.getFinancingAmount() != null && sale.getFinancingAmount().compareTo(BigDecimal.ZERO) > 0) {
            content.append("<tr><th>Financing Amount:</th><td>Ar").append(formatAmount(sale.getFinancingAmount())).append("</td></tr>");
            content.append("<tr><th>Monthly Payment:</th><td>Ar").append(formatAmount(sale.getMonthlyPayment())).append("</td></tr>");
            content.append("<tr><th>Loan Term:</th><td>").append(sale.getLoanTermMonths()).append(" months</td></tr>");
        }
        
        content.append("</table>");
        content.append("</div>");
        
        // Warranty Information
        if (sale.getWarrantyMonths() != null && sale.getWarrantyMonths() > 0) {
            content.append("<div class='details'>");
            content.append("<h4>Warranty Information</h4>");
            content.append("<p>Your vehicle comes with a <strong>").append(sale.getWarrantyMonths()).append("-month warranty</strong> starting from the delivery date.</p>");
            content.append("</div>");
        }
        
        // Delivery Information
        if (sale.getDeliveryDate() != null) {
            content.append("<div class='details'>");
            content.append("<h4>Delivery Information</h4>");
            content.append("<p>Scheduled delivery date: <strong>").append(sale.getDeliveryDate().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))).append("</strong></p>");
            content.append("</div>");
        }
        
        // Additional Notes
        if (sale.getNotes() != null && !sale.getNotes().trim().isEmpty()) {
            content.append("<div class='details'>");
            content.append("<h4>Additional Notes</h4>");
            content.append("<p>").append(sale.getNotes()).append("</p>");
            content.append("</div>");
        }
        
        content.append("<p>If you have any questions about your purchase, please don't hesitate to contact us.</p>");
        content.append("<p>Thank you for choosing ").append(companyName).append("!</p>");
        content.append("</div>");
        
        // Footer
        content.append("<div class='footer'>");
        content.append("<h4>").append(companyName).append("</h4>");
        content.append("<p>").append(companyAddress).append("</p>");
        content.append("<p>Phone: ").append(companyPhone).append(" | Email: ").append(companyEmail).append("</p>");
        content.append("</div>");
        
        content.append("</div>");
        content.append("</body>");
        content.append("</html>");
        
        return content.toString();
    }
    
    private String formatAmount(BigDecimal amount) {
        if (amount == null) return "0.00";
        return String.format("%,.2f", amount);
    }
}
