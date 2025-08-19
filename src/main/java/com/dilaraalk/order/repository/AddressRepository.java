package com.dilaraalk.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dilaraalk.order.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long>{

}
