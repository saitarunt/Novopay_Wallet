package com.wallet.forms.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
/**
 * {
 * 		"userId" : "",
 * 		"password" : ""
 * }
 * @author 724404
 *
 */
public class LoginForm {

	String userId;
	String password;
}
