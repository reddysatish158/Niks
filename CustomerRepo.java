package com.bvrit.pecunia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

//import org.springframework.data.jpa.repository.JpaRepository;

import com.bvrit.pecunia.model.Customer;

public interface CustomerRepo extends JpaRepository<Customer,Long>{

}