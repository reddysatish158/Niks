package com.bvrit.pecunia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.bvrit.pecunia.model.Transaction;


public interface TransactionRepo extends JpaRepository<Transaction,Long> {
	
	@Query
	public List<Transaction> findByTransAccountId(long transAccountId);
	
 
	 	 
}
