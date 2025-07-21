package com.carhub.service;

import com.carhub.entity.Client;
import com.carhub.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public Client saveClient(Client client) {
        if (client.getId() == null) {
            client.setCreatedAt(LocalDateTime.now());
        }
        client.setUpdatedAt(LocalDateTime.now());
        return clientRepository.save(client);
    }

    public Optional<Client> findById(Long id) {
        return clientRepository.findById(id);
    }

    public List<Client> findAll() {
        return clientRepository.findAllOrderByName();
    }

    public List<Client> getAllClients() {
        return clientRepository.findAllOrderByName();
    }

    public List<Client> searchClients(String searchTerm) {
        return clientRepository.findBySearchTerm(searchTerm);
    }

    public Optional<Client> findByEmail(String email) {
        return clientRepository.findByEmail(email);
    }

    public List<Client> findByPhoneNumber(String phoneNumber) {
        return clientRepository.findByPhoneNumber(phoneNumber);
    }

    public List<Client> findClientsWithSales() {
        return clientRepository.findClientsWithSales();
    }

    public void deleteClient(Long clientId) {
        clientRepository.deleteById(clientId);
    }

    public boolean isEmailAvailable(String email) {
        return !clientRepository.existsByEmail(email);
    }

    public long getTotalClientCount() {
        return clientRepository.count();
    }

    public long getTotalClientsCount() {
        return clientRepository.count();
    }

    public List<Client> findByCustomerType(Client.CustomerType customerType) {
        return clientRepository.findByCustomerType(customerType);
    }
}
