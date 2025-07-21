package com.carhub.service;

import com.carhub.entity.Car;
import com.carhub.entity.CarImage;
import com.carhub.repository.CarRepository;
import com.carhub.repository.CarImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarImageRepository carImageRepository;

    public Car saveCar(Car car) {
        if (car.getId() == null) {
            car.setCreatedAt(LocalDateTime.now());
        }
        car.setUpdatedAt(LocalDateTime.now());
        updateDaysInStock(car);
        return carRepository.save(car);
    }

    public Optional<Car> findById(Long id) {
        return carRepository.findById(id);
    }

    public List<Car> findAll() {
        return carRepository.findAll();
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public List<Car> findAvailableCars() {
        return carRepository.findByStatus(Car.Status.AVAILABLE);
    }

    public List<Car> findSoldCars() {
        return carRepository.findByStatus(Car.Status.SOLD);
    }

    public List<Car> searchCars(String searchTerm) {
        return carRepository.findByStatusAndSearch(Car.Status.AVAILABLE, searchTerm);
    }

    public List<Car> findCarsByBrand(String brand) {
        return carRepository.findByBrandContainingIgnoreCase(brand);
    }

    public List<Car> findCarsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return carRepository.findByPriceBetween(minPrice, maxPrice);
    }

    public void markCarAsSold(Long carId) {
        Optional<Car> carOpt = carRepository.findById(carId);
        if (carOpt.isPresent()) {
            Car car = carOpt.get();
            car.setStatus(Car.Status.SOLD);
            car.setSoldAt(LocalDateTime.now());
            carRepository.save(car);
        }
    }

    public void deleteCar(Long carId) {
        carRepository.deleteById(carId);
    }

    public Long getAvailableCarCount() {
        return carRepository.countByStatus(Car.Status.AVAILABLE);
    }

    public Long getSoldCarCount() {
        return carRepository.countByStatus(Car.Status.SOLD);
    }

    public Long getTotalCarsCount() {
        return carRepository.count();
    }

    public List<Object[]> getCarCountByBrand() {
        return carRepository.findCarCountByBrand();
    }

    public List<Car> findOldInventory(int days) {
        return carRepository.findCarsInStockLongerThan(days);
    }

    public List<Car> getLowInventoryCars(int threshold) {
        return carRepository.findCarsInStockLongerThan(30); // Cars older than 30 days
    }

    private void updateDaysInStock(Car car) {
        if (car.getCreatedAt() != null && car.getStatus() == Car.Status.AVAILABLE) {
            long days = ChronoUnit.DAYS.between(car.getCreatedAt(), LocalDateTime.now());
            car.setDaysInStock((int) days);
        }
    }

    public void addCarImage(Long carId, String imagePath, String imageName, boolean isPrimary) {
        Optional<Car> carOpt = carRepository.findById(carId);
        if (carOpt.isPresent()) {
            Car car = carOpt.get();

            // If this is set as primary, unset other primary images
            if (isPrimary) {
                List<CarImage> existingImages = carImageRepository.findByCarIdOrderByDisplayOrder(carId);
                existingImages.forEach(img -> {
                    img.setIsPrimary(false);
                    carImageRepository.save(img);
                });
            }

            CarImage carImage = new CarImage(car, imagePath, imageName);
            carImage.setIsPrimary(isPrimary);
            carImageRepository.save(carImage);
        }
    }

    public List<CarImage> getCarImages(Long carId) {
        return carImageRepository.findByCarIdOrderByPrimaryAndDisplayOrder(carId);
    }

    public Optional<CarImage> getPrimaryCarImage(Long carId) {
        return carImageRepository.findByCarIdAndIsPrimaryTrue(carId);
    }
}
