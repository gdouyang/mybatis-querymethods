package jpa.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import jpa.entity.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long>
{
	
	List<Customer> findByFirstName(String firstName);
	
//	List<Customer> findByLastName(String lastName);
//	
//	List<Customer> findAllCustomersWithName(@Param("custName") String custName);
//	
//	List<Customer> findAllCustomersWithNameNative(
//			@Param("custName") String custName);
//	
//	Customer findFirstByOrderByAddressAsc();
}
