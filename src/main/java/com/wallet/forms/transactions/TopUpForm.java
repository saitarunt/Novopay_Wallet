package com.wallet.forms.transactions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TopUpForm {

	String userId;
	String password;
	double amountToBeToppedUp;
}
