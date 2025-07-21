package com.carhub.repository;

import com.carhub.entity.CarImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarImageRepository extends JpaRepository<CarImage, Long> {
    
    List<CarImage> findByCarIdOrderByDisplayOrder(Long carId);
    
    Optional<CarImage> findByCarIdAndIsPrimaryTrue(Long carId);
    
    @Query("SELECT ci FROM CarImage ci WHERE ci.car.id = :carId ORDER BY ci.isPrimary DESC, ci.displayOrder ASC")
    List<CarImage> findByCarIdOrderByPrimaryAndDisplayOrder(@Param("carId") Long carId);
    
    void deleteByCarId(Long carId);
    
    Long countByCarId(Long carId);
}
