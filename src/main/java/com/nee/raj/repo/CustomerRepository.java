package com.nee.raj.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.nee.raj.entity.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

    List<Customer> findByLastName(String lastName);
    List<Customer> findByFirstName(String firstName);
}