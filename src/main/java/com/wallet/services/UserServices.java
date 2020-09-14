package com.wallet.services;

import java.sql.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wallet.forms.user.ChangePasswordForm;
import com.wallet.forms.user.LoginForm;
import com.wallet.repositories.user.UserAccountRepositoryImpl;
import com.wallet.repositories.user.UserRepositoryImpl;
import com.wallet.util.AESEncryptionDecryption;
import com.wallet.util.WalletUtil;
import com.wallet.entities.transactions.UserTransactions;
import com.wallet.entities.user.UserAccount;
import com.wallet.entities.user.Users;

/**
 * This class provides a list of User Services.
 * @author Sai Tarun
 *
 */
@Service
public class UserServices {

	@Autowired
	UserRepositoryImpl userRepo;
	@Autowired
	UserAccountRepositoryImpl userAccountRepo;
	@Autowired
	AESEncryptionDecryption encryptDecryptUtil;
	
	/**
	 * User Creation.
	 * @param user
	 * @return
	 */
	public String createUser(Users user) {		
		String newUserId = user.getUserId();
		String newPassword = user.getPassword();
		String newUserType = user.getUserType();
		if(!newUserId.isEmpty() && !newPassword.isEmpty() && !newUserType.isEmpty()) {
			if(UserAlreadyExists(newUserId)) {
				return "User Creation Failed -- User Already Exists";
			}else {			
				String encryptedPassword = encryptThePassword(newPassword);
				user.setPassword(encryptedPassword);
				addUser(user);
				createAccountForUser(user);
				return "User Successfully Created";
			}
		}else
			return "User Creation Failed -- Mandatory parameters Missing !!!";
	}

	private void createAccountForUser(Users user) {
		UserAccount newUserAccount = new UserAccount();
		initialiseUserAccount(newUserAccount, user);			
	}
	
	/**
	 * Creates a UserAccount upon Signup.
	 * @param newUserAccount
	 * @param user
	 */
	private void initialiseUserAccount(UserAccount newUserAccount, Users user) {
		String userId = user.getUserId();
		newUserAccount.setUserId(userId);
		newUserAccount.setCurrentBalance(1000.00);
		//newUserAccount.setUserTransactions(new LinkedList<UserTransactions>());
		long millis=System.currentTimeMillis();
		newUserAccount.setAccountNo(String.valueOf(millis));
		newUserAccount.setAccountCreatedDate(new Date(millis));		
		userAccountRepo.save(newUserAccount);			
	}

	private void addUser(Users user) {
		userRepo.save(user);		
	}
	
	/**
	 * Encrypts the password.
	 * @param newPassword
	 * @return
	 */
	private String encryptThePassword(String newPassword) {
		String encryptedPassword = encryptDecryptUtil.encrypt(newPassword, "secret");
		return encryptedPassword;
	}
	
	/**
	 * Checks if user already exists.
	 * @param newUserId
	 * @return
	 */
	private boolean UserAlreadyExists(String newUserId) {
		if(userRepo.existsById(newUserId))
			return true;		
		return false;
	}

	public int changePassword(ChangePasswordForm changePassswordForm) {
		
		String userId = changePassswordForm.getUserId();		
		if(!UserAlreadyExists(userId))
			return 201;
		String oldPassword = changePassswordForm.getOldPassword();
		String confirmOldPassword = changePassswordForm.getConfirmOldPassword();
		if(!oldPassword.equals(confirmOldPassword))
			return 202;
		String newPassword = changePassswordForm.getNewPassword();
		if(!newPassword.equals(oldPassword)) {
			String encryptedNewPassword = encryptThePassword(newPassword);
			updateNewPassword(userId, encryptedNewPassword);
			return 100;
		}
		//If old and new password are the same.
		return 203;			
	}
	
	/**
	 * Update new password.
	 * @param userId
	 * @param newPassword
	 */
	private void updateNewPassword(String userId, String newPassword) {
		Optional<Users> optionalUser = userRepo.findById(userId);
		Users user = optionalUser.get();
		user.setPassword(newPassword);
		userRepo.save(user);		
	}

	public Users getUserDetails(LoginForm loginForm, String userId) {
		String userIdUsedToLogin = loginForm.getUserId();
		String passwordUsedToLogin = loginForm.getPassword();
		if(validCredentialsForNovopayAssociate(userIdUsedToLogin, passwordUsedToLogin))
			return userRepo.findById(userIdUsedToLogin).get();		
		return null;
	}	
	
	/**
	 * Validates credentials and checks if User is a NOVOPAY associate.
	 * @param userIdUsedToLogin
	 * @param passwordUsedToLogin
	 * @return
	 */
	public boolean validCredentialsForNovopayAssociate(String userIdUsedToLogin, String passwordUsedToLogin) {
		Optional<Users> optionalUser = userRepo.findById(userIdUsedToLogin);
		Users user = optionalUser.get();		
		if(!WalletUtil.isVoid(user) && user.getUserType().equals("NOVO_ASSOCIATE")) {
			String password = user.getPassword();
			if(!password.isEmpty() && password.equals(encryptThePassword(passwordUsedToLogin)))
				return true;
		}
		return false;
	}
	
	/**
	 * Validates the credentials and returns User if exists.
	 * @param userId
	 * @param password
	 * @return
	 */
	public Users validateAndFetchUserIfExists(String userId, String password) {
		Users user = userRepo.findById(userId).get();
		if(!WalletUtil.isVoid(user)) {
			String userPassword = user.getPassword();
			if(!userPassword.isEmpty() && userPassword.equals(encryptThePassword(password)))
				return user;
		}
		return null;
	}
	
	/**
	 * Checks if the credentials are valid.
	 * @param loginForm
	 * @return
	 */
	public boolean isValidCredentials(LoginForm loginForm) {
		String userId = loginForm.getUserId();
		String password = loginForm.getPassword();
		Users user = userRepo.findById(userId).get();
		if(!WalletUtil.isVoid(user)) {
			String userPassword = user.getPassword();
			if(!userPassword.isEmpty() && userPassword.equals(encryptThePassword(password)))
				return true;
		}
		return false;
	}
	
	/**
	 * Checks if the given userId Exists.
	 * @param userId
	 * @return
	 */
	public boolean checkIfUserIdExists(String userId) {
		if(userRepo.existsById(userId))
			return true;
		return false;
	}
	
	/**
	 * Gets details of a User.
	 * @param loginForm
	 * @return
	 */
	public List<Users> getAllUserDetails(LoginForm loginForm) {
		String userIdUsedToLogin = loginForm.getUserId();
		String passwordUsedToLogin = loginForm.getPassword();
		if(validCredentialsForNovopayAssociate(userIdUsedToLogin, passwordUsedToLogin)) {			
			return getListofAllUsers();
		}
		return null;
	}
	
	/**
	 * Gets a list of all Users.
	 * @return
	 */
	private List<Users> getListofAllUsers() {
		return (List<Users>) userRepo.findAll();		 
	}

}
