package com.culcon.backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.culcon.backend.models.Staff;

public interface StaffRepo extends JpaRepository<Staff, String> {

}
