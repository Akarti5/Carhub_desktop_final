package com.carhub.repository;

import com.carhub.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    List<Car> findByStatus(Car.Status status);

    List<Car> findByBrandContainingIgnoreCase(String brand);

    List<Car> findByModelContainingIgnoreCase(String model);

    List<Car> findByYear(Integer year);

    List<Car> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    @Query("SELECT c FROM Car c WHERE c.status = :status AND " +
            "(LOWER(c.brand) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.model) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Car> findByStatusAndSearch(@Param("status") Car.Status status, @Param("search") String search);

    @Query("SELECT c FROM Car c WHERE c.status = 'AVAILABLE' ORDER BY c.createdAt DESC")
    List<Car> findAvailableCarsOrderByNewest();

    @Query("SELECT COUNT(c) FROM Car c WHERE c.status = :status")
    Long countByStatus(@Param("status") Car.Status status);

    @Query("SELECT c.brand, COUNT(c) FROM Car c GROUP BY c.brand ORDER BY COUNT(c) DESC")
    List<Object[]> findCarCountByBrand();

    @Query("SELECT c FROM Car c WHERE c.daysInStock > :days AND c.status = 'AVAILABLE'")
    List<Car> findCarsInStockLongerThan(@Param("days") Integer days);

    Optional<Car> findByVinNumber(String vinNumber);

    Optional<Car> findByLicensePlate(String licensePlate);
}
