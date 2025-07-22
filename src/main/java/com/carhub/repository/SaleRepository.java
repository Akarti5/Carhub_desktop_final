package com.carhub.repository;

import com.carhub.entity.Sale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    
    Optional<Sale> findByInvoiceNumber(String invoiceNumber);
    
    List<Sale> findByPaymentStatus(Sale.PaymentStatus paymentStatus);
    
    List<Sale> findByCarId(Long carId);
    
    List<Sale> findByClientId(Long clientId);
    
    List<Sale> findByAdminId(Long adminId);
    
    @Query("SELECT s FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate ORDER BY s.saleDate DESC")
    List<Sale> findSalesBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(s.salePrice) FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalRevenueBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                          @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(s.profit) FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate")
    BigDecimal getTotalProfitBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(s) FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate")
    Long getSalesCountBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                  @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT s FROM Sale s ORDER BY s.saleDate DESC")
    List<Sale> findAllOrderByDateDesc();
    
    @Query("SELECT EXTRACT(MONTH FROM s.saleDate) as month, COUNT(s) as count " +
           "FROM Sale s WHERE EXTRACT(YEAR FROM s.saleDate) = :year " +
           "GROUP BY EXTRACT(MONTH FROM s.saleDate) ORDER BY month")
    List<Object[]> getMonthlySalesCount(@Param("year") int year);
    
    @Query("SELECT s.paymentMethod, COUNT(s) FROM Sale s GROUP BY s.paymentMethod")
    List<Object[]> getSalesCountByPaymentMethod();
    
    @Query("SELECT EXTRACT(YEAR FROM s.saleDate) as year, EXTRACT(MONTH FROM s.saleDate) as month, SUM(s.salePrice) as revenue " +
           "FROM Sale s WHERE s.saleDate >= :startDate " +
           "GROUP BY EXTRACT(YEAR FROM s.saleDate), EXTRACT(MONTH FROM s.saleDate) " +
           "ORDER BY year DESC, month DESC")
    List<Object[]> getMonthlyRevenueData(@Param("startDate") LocalDateTime startDate);
    
    @Query("SELECT EXTRACT(YEAR FROM s.saleDate) as year, EXTRACT(MONTH FROM s.saleDate) as month, " +
           "SUM(s.salePrice) as revenue, COUNT(s) as salesCount " +
           "FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate " +
           "GROUP BY EXTRACT(YEAR FROM s.saleDate), EXTRACT(MONTH FROM s.saleDate) " +
           "ORDER BY year DESC, month DESC")
    List<Object[]> getMonthlyAnalytics(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);
    
    boolean existsByInvoiceNumber(String invoiceNumber);
}
