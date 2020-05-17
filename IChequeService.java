package com.bvrit.pecunia.service;

import java.util.List;

import com.bvrit.pecunia.model.Customer;
import com.bvrit.pecunia.model.Transaction;

public interface IChequeService {
	 Transaction getLastTransactionById(long transAccountId);
	 
	 List<Customer> getAllAccounts();
	
 	 List<Transaction> getAllTransactions();
	
	 Customer addAccount(Customer account) ;
	 
	 double getBalanceById(long accountId) ;
 	 
	 Transaction creditUsingCheque(long accountId,double amount,Transaction transaction) ;
	
	 Transaction debitUsingCheque(long accountId,double amount,Transaction transaction) ;

	public List<Transaction> getAllById(long accountId);
	
	//public List<Transaction> getAllTransactionById(long transAccountId);
}

