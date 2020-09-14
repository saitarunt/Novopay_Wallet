package com.wallet.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.entities.transactions.Transactions;
import com.wallet.entities.transactions.UserTransactions;
import com.wallet.entities.user.UserAccount;
import com.wallet.forms.transactions.TopUpForm;
import com.wallet.forms.transactions.TransferMoneyForm;
import com.wallet.forms.user.LoginForm;
import com.wallet.services.AccountServices;
import com.wallet.util.WalletUtil;

/**
 * This class provides Rest mappings for Account Services
 * @author Sai Tarun
 *
 */
@RestController
public class AccountController {

	@Autowired
	AccountServices accountServices;
	
	/**
	 * Mapping to initiate Transfer of funds.
	 * @param transferForm
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value="/Novopay/Account/TransferFunds")
	public String transferFunds(@RequestBody TransferMoneyForm transferForm) {
		return accountServices.transferFunds(transferForm);
	}
	
	/**
	 * 
	 * Mapping to get details of any Transaction.
	 * @param loginForm
	 * @param transactionId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value="/Novopay/Account/GetTransactionDetails/{transactionId}")
	public String getTransactionDetails(@RequestBody LoginForm loginForm, @PathVariable long transactionId) {
		Transactions transaction =  accountServices.getTransactionDetails(loginForm, transactionId);
		if(!WalletUtil.isVoid(transaction))
			return transaction.toString();
		else
			return "User Authentication Failed / Restriceted to Non Novopay Users";
	}
	
	/**
	 * Mapping to get Transactions made by the User.
	 * @param loginForm
	 * @param userId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value="/Novopay/Account/GetUserTransactions/{userId}")
	public String getUserTransactions(@RequestBody LoginForm loginForm, @PathVariable String userId) {
		List<UserTransactions> listOfUserTran =  accountServices.getUserTransactionDetails(loginForm, userId);
		if(!WalletUtil.isVoid(listOfUserTran))
			return listOfUserTran.toString();
		else
			return "User Authentication Failed";
	}
	
	/**
	 * Mapping to get all the Transactions made so far.
	 * @param loginForm
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value="/Novopay/Account/GetAllTransactions")
	public String getAllTransactions(@RequestBody LoginForm loginForm) {
		List<Transactions> allTransactions =  accountServices.getAllTransactions(loginForm);
		if(!WalletUtil.isVoid(allTransactions))
			return allTransactions.toString();
		else
			return "User Authentication Failed / Restriceted to Non Novopay Users";
	}
	
	/**
	 * Mapping to get a UserAccount.
	 * @param loginForm
	 * @param userId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value="/Novopay/Account/GetUserAccount/{userId}")
	public String getUserAccount(@RequestBody LoginForm loginForm, @PathVariable String userId) {
		UserAccount userAccount = accountServices.getUserAccount(loginForm, userId);
		if(!WalletUtil.isVoid(userAccount))
			return userAccount.toString();
		else
			return "User Authentication Failed";
	}
	
	/**
	 * Mapping to topUp an Account.
	 * @param topUpForm
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value="/Novopay/Account/TopUp")
	public String topUpAccount(@RequestBody TopUpForm topUpForm) {
		return accountServices.topUpAccount(topUpForm);
	}
	
	/**
	 * Mapping to get Status of a Transaction.
	 * @param loginForm
	 * @param transactionId
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value="/Novopay/Account/GetTransacctionStatus/{transactionId}")
	public String getTransactionStatus(@RequestBody LoginForm loginForm, @PathVariable long transactionId) {
		return accountServices.getTransactionStatus(loginForm, transactionId);
	}
}

