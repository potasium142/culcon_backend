package com.culcon.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.culcon.backend.models.Customer;

public interface CustomerRepo extends JpaRepository<Customer, String> {

}
