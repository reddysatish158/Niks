package com.bvrit.pecunia.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;

import com.bvrit.pecunia.exception.AccountAlreadyExistException;
import com.bvrit.pecunia.exception.AccountNotFoundException;
import com.bvrit.pecunia.exception.AmountException;
import com.bvrit.pecunia.exception.InsufficientBalanceException;
import com.bvrit.pecunia.model.Customer;
import com.bvrit.pecunia.model.Transaction;
import com.bvrit.pecunia.repository.CustomerRepo;
import com.bvrit.pecunia.repository.TransactionRepo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.transaction.Transactional;


@Service
public class ChequeServiceImp implements IChequeService {
	Map<Long,List<Transaction>> data=new HashMap<>();

	@Autowired
	RestTemplate rt;

	
  	@Autowired
	CustomerRepo customerRepo;
  	
	@Autowired
	TransactionRepo transactionRepo;
	
	Random random=new Random();
	
//	@Override
//	public List<Transaction> getAllTransactionById(long transAccountId){
//		return transactionRepo.findByTransAccountId(transAccountId);
//	}
	
	
	

	@Override
	public Transaction getLastTransactionById(long transAccountId){
		return transactionRepo.getOne(transAccountId);
	}
	
	@Override
	public List<Transaction> getAllById(long accountId){
		if(!data.containsKey(accountId)) {
			throw new AccountNotFoundException("account not found");
		}
			return new ArrayList<> (data.get(accountId));
	} 
	
	@Override	
 	public List<Transaction> getAllTransactions(){
		return transactionRepo.findAll();
	}
	
	public static boolean isValidChequeIssueDate(LocalDate chequeIssueDate){
		LocalDate transactionDate=LocalDate.now();
		 Long months=ChronoUnit.MONTHS.between(chequeIssueDate, transactionDate) ;
		if(months<=3){
		return  true;
		}
		 
		return false;
	} 
	 

	
	/////////////////////////////////addAccount//////////////////////////////////////////////////////////
	
	@Override
	@Transactional
	public Customer addAccount(Customer customer) {
		if(customerRepo.existsById(customer.getAccountId())) {
			throw new AccountAlreadyExistException("Account Already Exists");
		}
		return customerRepo.save(customer);
	}

	  	@Override
	public List<Customer> getAllAccounts(){
		
		return customerRepo.findAll();
	}
	
	 
	
	 
	/*************************************************************************
	 -FunctionName                     : getBalanceById
	 -Input Parameters                 : accountId
	 -Return Type                      : accountBalance
	 -Throws				           : AccountNotFoundException
	 -Author				           : Prameela
	 -Creation Date			           : 28/04/2020
	 -Description			           : viewing accountBalance based on accountId
	***************************************************************************/

	@Override	
	@Transactional
	public double getBalanceById(long accountId) {
		Customer customer=customerRepo.getOne(accountId);
         	if(!customerRepo.existsById(accountId) ) {
				throw new AccountNotFoundException("Account does not exists");
			}
		return customer.getAccountBalance();
		 
	}
	
	/*************************************************************************
	 -FunctionName                     : CreditUsingCheque
	 -Input Parameters                 : accountId,amount,transaction
	 -Return Type                      : transaction
	 -Throws				           : AccountNotFoundException
	 -Author				           : Prameela
	 -Creation Date			           : 28/04/2020
	 -Description			           : saving CreditUsingCheque to database
	***************************************************************************/

	@Override
	@Transactional
	public Transaction creditUsingCheque(long accountId,double amount,Transaction transaction) {
	 	 
//		if(!customerRepo.existsById(accountId) ) {
//			
//			throw new AccountNotFoundException("Account Does not exists");
 if(amount<100 || amount>200000) {
			throw new AmountException("please Enter the amount with in 100 - 200000");
		}
 		  		Customer customer=customerRepo.getOne(accountId);
		double currentBalance=getBalanceById(accountId)+amount;
		 customer.setAccountBalance(currentBalance);
		 customerRepo.save(customer);
			 transaction.setTransClosingBalance(getBalanceById(accountId));
	 		//  String transaction_type="Credit Using Cheque";
	 		   transaction.setTransaction_id(Long.parseLong(String.valueOf(Math.abs(new Random().nextLong())).substring(0, 12)));
			  transaction.setTransAccountId(accountId);
			  //transaction.setTransOption(transaction_type);
			  transaction.setTransAmount(amount);
			  transaction.setTransDate(LocalDate.now());
			//  transactionRepo.save(transaction);
			  
			  Transaction list=new Transaction( accountId,transaction.getTransaction_id(),transaction.getTransAmount(),transaction.getTransOption(),transaction.getTransDate(),transaction.getTransChequeId(),transaction.getTransClosingBalance(),transaction.getChequeList());
			  if(!data.containsKey(accountId)){
				  data.put(accountId,new ArrayList<>());
				  data.get(accountId).add(list);
			  }
			  else{
				  data.get(accountId).add(list);
			  }
		return transactionRepo.save(transaction);

	}

	/*************************************************************************
	 -FunctionName                     : DebitUsingCheque
	 -Input Parameters                 : accountId,amount,transaction
	 -Return Type                      : transaction
	 -Throws				           :  AccountNotFoundException,AmountException,InsufficientBalanceException
	 -Author				           : Prameela
	 -Creation Date			           : 28/04/2020
	 -Description			           : saving DebitUsingCheque to database
	***************************************************************************/

	@Override
	@Transactional
	public Transaction debitUsingCheque(long accountId,double amount,Transaction transaction) {
		if(!customerRepo.existsById(accountId) ) {
			
			throw new AccountNotFoundException("Account Does not exists");
		}
		else if(amount<100 || amount>200000) {
			throw new AmountException("please Enter the amount with in 100 - 200000");
		}
		else if(amount>getBalanceById(accountId)) {
			throw new InsufficientBalanceException("Insufficient balance");
		}
 		Customer customer=customerRepo.getOne(accountId);
		double currentBalance=getBalanceById(accountId)-amount;
		 customer.setAccountBalance(currentBalance);
		 customerRepo.save(customer);
		 transaction.setTransClosingBalance(getBalanceById(accountId));
		  String transaction_type="Debit Using Cheque";
		   transaction.setTransaction_id(Long.parseLong(String.valueOf(Math.abs(new Random().nextLong())).substring(0, 12)));
		  transaction.setTransAccountId(accountId);
		  transaction.setTransOption(transaction_type);
		  transaction.setTransAmount(amount);
		  transaction.setTransDate(LocalDate.now());	 
		 // transactionRepo.save(transaction);
		  Transaction list=new Transaction(accountId,transaction.getTransaction_id(),transaction.getTransAmount(),transaction.getTransOption(),transaction.getTransDate(),transaction.getTransChequeId(),transaction.getTransClosingBalance(),transaction.getChequeList());
		  if(!data.containsKey(accountId)){
			  data.put(accountId,new ArrayList<>());
			  data.get(accountId).add(list);
		  }
		  else{
			  data.get(accountId).add(list);
		  }
	  
	 
	return transactionRepo.save(transaction);
 
	}

	public boolean isValidAccountId(long accountId) {
		String str=String.valueOf(accountId);
		if(str.matches("[0-9]{12}")) {
		return true;	
		}
		return false;
	}
 
}
