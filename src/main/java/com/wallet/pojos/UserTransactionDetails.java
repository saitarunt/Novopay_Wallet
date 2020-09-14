package com.wallet.pojos;

import java.sql.Date;
import java.util.List;

import javax.persistence.Entity;

import com.wallet.entities.transactions.UserTransactions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class UserTransactionDetails {

	double accountDebitOrCredit;
	double oldBalance;
	double newBalance;
	String userId;
	String accountNumber;
}
