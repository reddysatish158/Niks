package com.bvrit.pecunia.controller;



import java.time.LocalDate;
import java.util.List;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bvrit.pecunia.model.Customer;
import com.bvrit.pecunia.model.Transaction;
import com.bvrit.pecunia.service.IChequeService;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@CrossOrigin(origins = "http://localhost:4200") 
@RestController
@RequestMapping("/transactions")
public class ChequeRestController {
	
	
	@Autowired
	IChequeService chequeService;
 
//	@GetMapping("/listTransactionsById/id/{id}")
//	public List<Transaction> ListAllTransactionsById(@PathVariable("id") long transAccountId) {
//		return chequeService.getAllTransactionById(transAccountId);
//	}


	@GetMapping("/allTransactionsById/id/{id}")
	public List<Transaction> getAllTransactionsById(@PathVariable("id") long transAccountId) {
		return chequeService.getAllById(transAccountId);
	}

	
	@GetMapping("/lastTransactionsById/id/{id}")
	public Transaction getTransactionById(@PathVariable("id") long transAccountId) {
		return chequeService.getLastTransactionById(transAccountId);
	}


	@GetMapping("/allTransactions")
	public ResponseEntity<List<Transaction>> getAllTransactions(){
		List<Transaction> alltransactions=chequeService.getAllTransactions();
		return new ResponseEntity<List<Transaction>>(alltransactions,HttpStatus.OK);
	}


	@PostMapping("/addAccount")
	public ResponseEntity<Customer> addAccount(@RequestBody Customer customer){
		 chequeService.addAccount(customer);
		return new ResponseEntity<Customer>(customer,HttpStatus.CREATED); 
	}
	
	
	@GetMapping("/allAccounts")
	public ResponseEntity<List<Customer>> getAllAccounts(){
		List<Customer> allaccounts=chequeService.getAllAccounts();
		return new ResponseEntity<List<Customer>>(allaccounts,HttpStatus.OK);
	}
	
	
	@GetMapping("/balanceById/id/{id}")
	public double getBalanceById(@PathVariable("id") long accountId) {
		return chequeService.getBalanceById(accountId); 
		 
	}
	
	
	@PostMapping("/creditcheque/id/{id}/amount/{amount}")
	@HystrixCommand(fallbackMethod="creditUsingChequeFallback")
	public Transaction creditUsingCheque(@PathVariable("id") long accountId ,@PathVariable("amount") double amount,@RequestBody Transaction transaction) {
	  	  return chequeService.creditUsingCheque(accountId, amount, transaction);
	}
	 
	@PostMapping("/debitcheque/id/{id}/amount/{amount}")
	 public Transaction debitUsingCheque(@PathVariable("id") long accountId,@PathVariable("amount") double amount,@RequestBody Transaction transaction ) {
		  return chequeService.debitUsingCheque(accountId, amount, transaction);
	}
 
	public Transaction creditUsingChequeFallback(@PathVariable("id") long accountId ,@PathVariable("amount") double amount,@RequestBody Transaction transaction) {
		
			Transaction t=new Transaction(accountId,123456789012L,amount, "cheque",LocalDate.now(), 123456, 20000.00, null);
			return t;
		}
}
