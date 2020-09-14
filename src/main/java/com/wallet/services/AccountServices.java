package com.wallet.services;

import java.sql.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.wallet.entities.transactions.Transactions;
import com.wallet.entities.transactions.UserTransactions;
import com.wallet.entities.user.UserAccount;
import com.wallet.entities.user.Users;
import com.wallet.forms.transactions.TopUpForm;
import com.wallet.forms.transactions.TransferMoneyForm;
import com.wallet.forms.user.LoginForm;
import com.wallet.pojos.TransactionDetails;
import com.wallet.pojos.UserTransactionDetails;
import com.wallet.pojos.UserTransactionsId;
import com.wallet.repositories.transactions.TransactionRepositoryImpl;
import com.wallet.repositories.user.UserAccountRepositoryImpl;
import com.wallet.repositories.user.UserTransactionsRepositoryImpl;
import com.wallet.util.WalletUtil;

/**
 * This class provides various Account related Operations.
 * @author Sai Tarun 
 *
 */
@Service
public class AccountServices {

	public static final double COMISSION_RATE = 0.0020;
	public static final double CHARGES_RATE = 0.0005;
	public static final String DEBIT_OR_CREDIT = "DEBIT/CREDIT";
	public static final String DEBIT = "DEBIT";
	public static final String CREDIT = "CREDIT";
	public static final String TOP_UP = "TOP_UP";
	public static final String SUCCESS = "SUCCESS";
	
	@Autowired
	UserServices userServices;
	@Autowired
	UserAccountRepositoryImpl userAccountRepo;
	@Autowired
	TransactionRepositoryImpl transactionsRepo;
	@Autowired
	UserTransactionsRepositoryImpl userTransactionsRepo;
	
	public String transferFunds(TransferMoneyForm transferForm) {
		String userId = transferForm.getSenderUserId();
		String password = transferForm.getSenderPassword();
		Users sender = userServices.validateAndFetchUserIfExists(userId, password);
		String receiverUserId  = transferForm.getReceiverUserId();
		if(!WalletUtil.isVoid(sender)) {
			if(userServices.checkIfUserIdExists(receiverUserId)) {
				double amountToBeTransferred= transferForm.getAmountToBeTransferred();
				UserAccount senderAccount = userAccountRepo.findById(userId).get();
				double senderBalance = senderAccount.getCurrentBalance();				
				if(isSendersBalanceSufficientForTransfer(senderBalance, amountToBeTransferred)) {
					UserAccount receiverAccount = userAccountRepo.findById(receiverUserId).get();
					long transactionId = initiateTransfer(senderAccount, receiverAccount, amountToBeTransferred);
					return "Transafer Funds Successful -- TransactionID = " +  String.valueOf(transactionId);
				}else 
					return "Transfer funds Failed -- Insuffiecient Balance, please top up";
			}else 
				return "Transfer funds Failed -- Invalid Receiver";			
		}else
			return "Transfer funds Failed -- Sender with given credentials dont exist";
		
	}
	
	/**
	 * Initiated Fund Transafer.
	 * @param senderAccount
	 * @param receiverAccount
	 * @param amountToBeExchanged
	 * @return
	 */
	private long initiateTransfer(UserAccount senderAccount, UserAccount receiverAccount, double amountToBeExchanged) {
		String initiatedByUser = senderAccount.getUserId();
		TransactionDetails transactionDetails = createTransaction(senderAccount.getAccountNo(), 
												receiverAccount.getAccountNo(), amountToBeExchanged, 
												initiatedByUser, DEBIT_OR_CREDIT);
		 createUserTransactions(senderAccount, receiverAccount, transactionDetails, amountToBeExchanged);
		 String tranType = transactionDetails.getTransactionType();
		 modifyBalanceInUserAccounts(senderAccount, receiverAccount, amountToBeExchanged, tranType);
		 return transactionDetails.getTransactionId();
	}

	private long createUserTransactions(UserAccount senderAccount, UserAccount receiverAccount,
			TransactionDetails transactionDetails, double amountToBeExchanged) {	
		
		String transactionType = transactionDetails.getTransactionType();
		double commissionsAndCharges = transactionDetails.getCommissions() + transactionDetails.getCharges();
		
		if(transactionType.equals(DEBIT_OR_CREDIT)) {
			
			UserTransactionDetails senderTransactionDetails = getUserTransactionDetails(senderAccount, amountToBeExchanged, 
															  commissionsAndCharges, DEBIT);
			generateUserTransaction(senderAccount, transactionDetails, senderTransactionDetails,
									amountToBeExchanged, receiverAccount.getUserId());
			
			UserTransactionDetails receiverTransactionDetails = getUserTransactionDetails(receiverAccount, amountToBeExchanged, 
																commissionsAndCharges, CREDIT);
			generateUserTransaction(receiverAccount, transactionDetails, receiverTransactionDetails, 
									amountToBeExchanged, senderAccount.getUserId());		
		}else if(transactionType.equals(TOP_UP)) {
			
			UserTransactionDetails userTransactionDetails = getUserTransactionDetails(senderAccount, amountToBeExchanged, 
					  commissionsAndCharges, TOP_UP);
			generateUserTransaction(senderAccount, transactionDetails, userTransactionDetails, 
									amountToBeExchanged, null);
			
		}
		return transactionDetails.getTransactionId();
	}

	private UserTransactionDetails getUserTransactionDetails(UserAccount userAccount, double amountToBeExchanged, 
															double commissionsAndCharges, String tranType) {
		double userOldBalance = userAccount.getCurrentBalance();
		String userId = userAccount.getUserId();
		String userAccountNumber = userAccount.getAccountNo();
		if(tranType.equals(DEBIT)) {
			double senderAccountDebit = amountToBeExchanged + commissionsAndCharges;			
			double senderNewBalance = userOldBalance - senderAccountDebit;			
			return new UserTransactionDetails(senderAccountDebit, userOldBalance, senderNewBalance, 
											  userId,userAccountNumber);
		}else if(tranType.equals(CREDIT)) {
			double receiverAccountCredit = amountToBeExchanged - commissionsAndCharges;
			double receiverNewBalance = userOldBalance + receiverAccountCredit;
			return new UserTransactionDetails(receiverAccountCredit, userOldBalance, receiverNewBalance, 
					  userId,userAccountNumber);
		} else if(tranType.equals(TOP_UP)) {
			double accountCredit = amountToBeExchanged - commissionsAndCharges;
			double newBalance = userOldBalance + accountCredit;
			return new UserTransactionDetails(accountCredit, userOldBalance, newBalance, 
					  userId,userAccountNumber);
		}
		return null;
	}

	/**
	 * Create a UserTransaction entry.
	 * @param userAccount
	 * @param transactionDetails
	 * @param userTransactionDetails
	 * @param amountToBeExchanged
	 * @param otherParty
	 */
	private void generateUserTransaction(UserAccount userAccount, TransactionDetails transactionDetails, UserTransactionDetails userTransactionDetails,
			double amountToBeExchanged, String otherParty) {
		
		UserTransactions userTransaction = new UserTransactions();
		userTransaction.setTransactionId(transactionDetails.getTransactionId());
		userTransaction.setTransactionTime(transactionDetails.getTransactionTime());
		userTransaction.setTansactionAmount(amountToBeExchanged);
		userTransaction.setTransactionType(transactionDetails.getTransactionType());
		userTransaction.setTransactionStatus(transactionDetails.getTransactionStatus());
		userTransaction.setTransactionComission(transactionDetails.getCommissions());
		userTransaction.setTransactionCharges(transactionDetails.getCharges());
		userTransaction.setTotalTransactionAmount(userTransactionDetails.getAccountDebitOrCredit());
		userTransaction.setAccountNo(userTransactionDetails.getAccountNumber());
		//userTransaction.setUserId(userTransactionDetails.getUserId());
		userTransaction.setOldBalance(userTransactionDetails.getOldBalance());
		userTransaction.setNewBalance(userTransactionDetails.getNewBalance());
		userTransaction.setUserTransactionId(String.valueOf(System.currentTimeMillis()));
		userTransaction.setUserId(userTransactionDetails.getUserId());
		userTransaction.setOtherParty(otherParty);
		userTransactionsRepo.save(userTransaction);		
	}
	
	/**
	 * Create a Transaction entry.
	 * @param senderAccountNo
	 * @param receiverAccountNo
	 * @param amountToBeTransferred
	 * @param initiatedByUser
	 * @param tranType
	 * @return
	 */
	private TransactionDetails createTransaction(String senderAccountNo, String receiverAccountNo, 
			double amountToBeTransferred, String initiatedByUser, String tranType) {
		Transactions newTransaction = new Transactions();
		//Time Stamp is TranId.
		long tranId = System.currentTimeMillis();
		newTransaction.setTransactionId(tranId);
		newTransaction.setTransactionType(tranType);
		Date transactionDate = new Date(tranId);
		newTransaction.setTransactionTime(transactionDate);
		//For DEBIT/CREDIT only.
		if(!WalletUtil.isVoid(receiverAccountNo)) {
			newTransaction.setFromAccountNo(senderAccountNo);
			newTransaction.setToAccountNumber(receiverAccountNo);
		}
		newTransaction.setInitiatedByUser(initiatedByUser);
		newTransaction.setTransactionAmount(amountToBeTransferred);
		//For Novopay its double as its coming from Sender and Receiver.
		newTransaction.setTransactionCharges(amountToBeTransferred*CHARGES_RATE*2);
		newTransaction.setTransactionCommission(amountToBeTransferred*COMISSION_RATE*2);
		newTransaction.setTransactionStatus(SUCCESS);
		transactionsRepo.save(newTransaction);
		return getTransactionDetails(tranId, tranType, transactionDate, amountToBeTransferred);		 
	}
	
	private TransactionDetails getTransactionDetails(long tranId, String tranType, Date transactionDate,
			double amountToBeTransferred) {
		TransactionDetails transactionDetails = new TransactionDetails();
		transactionDetails.setTransactionId(tranId);
		transactionDetails.setTransactionType(tranType);
		transactionDetails.setTransactionTime(transactionDate);
		transactionDetails.setCharges(amountToBeTransferred*COMISSION_RATE);
		transactionDetails.setCommissions(amountToBeTransferred*CHARGES_RATE);
		transactionDetails.setTransactionStatus(SUCCESS);		
		return transactionDetails;
	}

	/**
	 * Updates Balance in User's Account after the end of the transaction.
	 * @param senderAccount
	 * @param receiverAccount
	 * @param amountToBeExchanged
	 * @param tranType
	 */
	private void modifyBalanceInUserAccounts(UserAccount senderAccount, UserAccount receiverAccount, 
											 double amountToBeExchanged, String tranType) {
		
		if(tranType.equals(DEBIT_OR_CREDIT)) {
			double currentSenderBalance = senderAccount.getCurrentBalance();
			double amountToTransferAFterChargesAndCommission = amountToBeExchanged + amountToBeExchanged*(COMISSION_RATE+CHARGES_RATE);
			double newSenderBalance = currentSenderBalance - amountToTransferAFterChargesAndCommission;
			senderAccount.setCurrentBalance(newSenderBalance);
					
			double currentReceiverBalance = receiverAccount.getCurrentBalance();
			double amountToReceiveAFterChargesAndCommission = amountToBeExchanged - amountToBeExchanged*(COMISSION_RATE+CHARGES_RATE);
			double newReceiverBalance = currentReceiverBalance + amountToReceiveAFterChargesAndCommission;
			receiverAccount.setCurrentBalance(newReceiverBalance);
					
			List<UserAccount> modifyAccounts = new LinkedList<UserAccount>();
			modifyAccounts.add(senderAccount);
			modifyAccounts.add(receiverAccount);
			userAccountRepo.saveAll(modifyAccounts);	
		}else if(tranType.equals(TOP_UP)) {
			double currentUserBalance = senderAccount.getCurrentBalance();
			double amountToCreditedAFterChargesAndCommission = amountToBeExchanged + amountToBeExchanged*(COMISSION_RATE+CHARGES_RATE);
			double newUserBalance = currentUserBalance - amountToCreditedAFterChargesAndCommission;
			senderAccount.setCurrentBalance(newUserBalance);
			userAccountRepo.save(senderAccount);			
		}
	}
	
	/**
	 * Checks if Sender has adequate funds for Transfer.
	 * @param senderBalance
	 * @param amountToBeTransferred
	 * @return
	 */
	private boolean isSendersBalanceSufficientForTransfer(double senderBalance, double amountToBeTransferred) {	
		double amountAfterExternalCharges = amountToBeTransferred + amountToBeTransferred*(COMISSION_RATE+CHARGES_RATE);
		if(amountAfterExternalCharges<=senderBalance)
			return true;
		return false;
	}
	
	/**
	 * Returns details of a single transaction.
	 * @param loginForm
	 * @param transactionId
	 * @return
	 */
	public Transactions getTransactionDetails(LoginForm loginForm, long transactionId) {
		if(userServices.isValidCredentials(loginForm))
			return transactionsRepo.findById(transactionId).get();
		return null;
	}
	
	/**
	 * Returns list of transactions made by a User.
	 * @param loginForm
	 * @param userId
	 * @return
	 */
	public List<UserTransactions> getUserTransactionDetails(LoginForm loginForm, String userId) {
		if(userServices.isValidCredentials(loginForm))
			return (List<UserTransactions>) userTransactionsRepo.findByUserId(userId);
		return null;
	}
	
	/**
	 * Returns all transactions done so far.
	 * @param loginForm
	 * @return
	 */
	public List<Transactions> getAllTransactions(LoginForm loginForm) {	
		String userId = loginForm.getUserId();
		String password = loginForm.getPassword();
		if(userServices.validCredentialsForNovopayAssociate(userId, password))
			return (List<Transactions>) transactionsRepo.findAll();
		return null;
	}
	
	/**
	 * Returns the UserAccount for the User.
	 * @param loginForm
	 * @param userId
	 * @return
	 */
	public UserAccount getUserAccount(LoginForm loginForm, String userId) {
		if(userServices.isValidCredentials(loginForm))
			return userAccountRepo.findById(userId).get();
		return null;
	}

	public String topUpAccount(TopUpForm topUpForm) {
		String userId = topUpForm.getUserId();
		String password = topUpForm.getPassword();
		Users user = userServices.validateAndFetchUserIfExists(userId, password);
		if(!WalletUtil.isVoid(user)) {
			double amountToBeToppedUp = topUpForm.getAmountToBeToppedUp();
			UserAccount userAccount = userAccountRepo.findById(userId).get();
			return initiateTopUp(userAccount, amountToBeToppedUp);			
		}else
			return "TopUp Failed -- User with entered credentials dont exist";		
	}
	
	/**
	 * Initiates TopUp operation.
	 * @param userAccount
	 * @param amountToBeToppedUp
	 * @return
	 */
	private String initiateTopUp(UserAccount userAccount, double amountToBeToppedUp) {
		String initiatedByUser = userAccount.getUserId();
		TransactionDetails transactionDetails = createTransaction(null, 
				null, amountToBeToppedUp, initiatedByUser, TOP_UP);
		long transactionId = createUserTransactions(userAccount, null, transactionDetails, amountToBeToppedUp);
		modifyBalanceInUserAccounts(userAccount, null, amountToBeToppedUp, 
						transactionDetails.getTransactionType());
		return "TopUp Successful -- TransactionID: " + String.valueOf(transactionId);
	}
	
	/**
	 * Get Status of a transaction. 
	 * @param loginForm
	 * @param transactionId
	 * @return
	 */
	public String getTransactionStatus(LoginForm loginForm, long transactionId) {		
		if(userServices.isValidCredentials(loginForm)) {
			Transactions transaction = transactionsRepo.findById(transactionId).get();		
			return "Status of the Transaction : " + transactionId +" = "+ transaction.getTransactionStatus();
		}
		return "User Authentication Failed";
	}

	
}
