package com.carhub.repository;

import com.carhub.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    Optional<Client> findByEmail(String email);
    
    List<Client> findByPhoneNumber(String phoneNumber);
    
    @Query("SELECT c FROM Client c WHERE " +
           "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "c.phoneNumber LIKE CONCAT('%', :search, '%')")
    List<Client> findBySearchTerm(@Param("search") String search);
    
    List<Client> findByCity(String city);
    
    List<Client> findByCustomerType(Client.CustomerType customerType);
    
    @Query("SELECT c FROM Client c ORDER BY c.firstName, c.lastName")
    List<Client> findAllOrderByName();
    
    @Query("SELECT c FROM Client c WHERE SIZE(c.sales) > 0 ORDER BY c.createdAt DESC")
    List<Client> findClientsWithSales();
    
    boolean existsByEmail(String email);
}
