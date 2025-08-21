package com.dilaraalk.address.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dilaraalk.address.entity.Address;
import com.dilaraalk.user.entity.User;

public interface AddressRepository extends JpaRepository<Address, Long>{

	List<Address> findByUser(User user);
	
}
