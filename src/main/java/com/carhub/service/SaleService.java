package com.carhub.service;

import com.carhub.entity.Sale;
import com.carhub.entity.Car;
import com.carhub.repository.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SaleService {

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private CarService carService;

    @Autowired
    private EmailService emailService;

    public Sale createSale(Sale sale) {
        // Generate invoice number if not provided
        if (sale.getInvoiceNumber() == null || sale.getInvoiceNumber().isEmpty()) {
            sale.setInvoiceNumber(generateInvoiceNumber());
        }

        // Mark car as sold
        if (sale.getCar() != null) {
            carService.markCarAsSold(sale.getCar().getId());
        }

        Sale savedSale = saleRepository.save(sale);
        
        // Send confirmation email to client
        try {
            emailService.sendSaleConfirmationEmail(savedSale);
        } catch (Exception e) {
            System.err.println("Failed to send sale confirmation email: " + e.getMessage());
        }

        return savedSale;
    }

    public Sale updateSale(Sale sale) {
        return saleRepository.save(sale);
    }

    public Optional<Sale> findById(Long id) {
        return saleRepository.findById(id);
    }

    public Optional<Sale> findByInvoiceNumber(String invoiceNumber) {
        return saleRepository.findByInvoiceNumber(invoiceNumber);
    }

    public List<Sale> findAll() {
        return saleRepository.findAllOrderByDateDesc();
    }

    public List<Sale> getAllSales() {
        return saleRepository.findAllOrderByDateDesc();
    }

    public List<Sale> findSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return saleRepository.findSalesBetweenDates(startDate, endDate);
    }

    public List<Sale> findSalesByClient(Long clientId) {
        return saleRepository.findByClientId(clientId);
    }

    public List<Sale> findSalesByAdmin(Long adminId) {
        return saleRepository.findByAdminId(adminId);
    }

    public BigDecimal getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal revenue = saleRepository.getTotalRevenueBetweenDates(startDate, endDate);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    public BigDecimal getTotalProfit(LocalDateTime startDate, LocalDateTime endDate) {
        BigDecimal profit = saleRepository.getTotalProfitBetweenDates(startDate, endDate);
        return profit != null ? profit : BigDecimal.ZERO;
    }

    public Long getSalesCount(LocalDateTime startDate, LocalDateTime endDate) {
        return saleRepository.getSalesCountBetweenDates(startDate, endDate);
    }

    public Long getTotalSalesCount() {
        return saleRepository.count();
    }

    public BigDecimal getMonthlyRevenue() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfMonth = LocalDateTime.now();
        return getTotalRevenue(startOfMonth, endOfMonth);
    }

    public List<Sale> getRecentSales(int limit) {
        List<Sale> allSales = saleRepository.findAllOrderByDateDesc();
        return allSales.size() > limit ? allSales.subList(0, limit) : allSales;
    }

    public List<Object[]> getMonthlySalesData(int year) {
        return saleRepository.getMonthlySalesCount(year);
    }

    public List<Object[]> getSalesByPaymentMethod() {
        return saleRepository.getSalesCountByPaymentMethod();
    }

    public void deleteSale(Long saleId) {
        Optional<Sale> saleOpt = saleRepository.findById(saleId);
        if (saleOpt.isPresent()) {
            Sale sale = saleOpt.get();
            // Mark car as available again
            if (sale.getCar() != null) {
                Car car = sale.getCar();
                car.setStatus(Car.Status.AVAILABLE);
                car.setSoldAt(null);
                carService.saveCar(car);
            }
            saleRepository.deleteById(saleId);
        }
    }

    private String generateInvoiceNumber() {
        String prefix = "INV";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // Find the next sequence number for today
        long count = saleRepository.count() + 1;

        return String.format("%s-%s-%03d", prefix, timestamp, count);
    }

    public boolean isInvoiceNumberAvailable(String invoiceNumber) {
        return !saleRepository.existsByInvoiceNumber(invoiceNumber);
    }

    public List<Object[]> getMonthlyRevenueForLast6Months() {
        LocalDateTime sixMonthsAgo = LocalDateTime.now().minusMonths(6).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        return saleRepository.getMonthlyRevenueData(sixMonthsAgo);
    }

    public List<Object[]> getMonthlyAnalytics(LocalDateTime startDate, LocalDateTime endDate) {
        return saleRepository.getMonthlyAnalytics(startDate, endDate);
    }
}
