package com.wallet.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.wallet.forms.user.ChangePasswordForm;
import com.wallet.forms.user.LoginForm;
import com.wallet.entities.user.Users;
import com.wallet.services.UserServices;

/**
 * This class provides Rest mappings for User Services.
 * @author Sai Tarun
 *
 */
@RestController
public class UserController {
	
	@Autowired
	UserServices userServices;
	
	/**
	 * Mapping to allow Users to Signup.
	 * @param user
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST, value="/Novopay/User/SignUp")
	public String createUser(@RequestBody Users user) {
		return userServices.createUser(user);		
	}
	
	/**
	 * Mapping to change password.
	 * @param changePassswordForm
	 * @return
	 */
	@RequestMapping(method= RequestMethod.POST, value="/Novopay/User/ChangePassword")
	public String changePassword(@RequestBody ChangePasswordForm changePassswordForm) {
		int response = userServices.changePassword(changePassswordForm);	
		switch(response) {
			case 100 : return "Password successfully changed!!";
			case 201 : return "Password Change Failed - User Unavailable";
			case 202 : return "Password Change Failed - Password Verification Failed";	
			default : return "Password Change Failed - Old and New passwords are same!!";	
		}
	}
	
	/**
	 * Mapping to get details of a User. 
	 * @param loginForm
	 * @param userId
	 * @return
	 */
	@RequestMapping(method= RequestMethod.POST, value="/Novopay/User/{userId}")
	public String getUserDetails(@RequestBody LoginForm loginForm, @PathVariable String userId) {
		Users userDetailsResponse =  userServices.getUserDetails(loginForm, userId);
		if(userDetailsResponse==null)
			return "User Details cant be displayed";
		else
			return userDetailsResponse.toString();
	}
	
	/**
	 * Mapping to get All Users.
	 * @param loginForm
	 * @return
	 */
	@RequestMapping(method= RequestMethod.POST, value = "/Novopay/User/GetAllUsers")
	public List<Users> getUserDetails(@RequestBody LoginForm loginForm) {
		return userServices.getAllUserDetails(loginForm);		
	}
	
	/**
	 * Test Mapping.
	 * @return
	 */
	@RequestMapping("/Hello")
	public String test() {
		return "hello";
	}
	
}
