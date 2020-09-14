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
public class ChangePasswordForm {

	String userId;
	String oldPassword;
	String confirmOldPassword;
	String newPassword;
	
}
