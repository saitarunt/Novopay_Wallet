package com.wallet.forms.transactions;

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
public class TransferMoneyForm {

	String senderUserId;
	String senderPassword;
	double amountToBeTransferred;
	String receiverUserId;
	
}
