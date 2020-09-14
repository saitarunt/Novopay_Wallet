package com.wallet.entities.transactions;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.wallet.entities.user.UserAccount;
import com.wallet.pojos.UserTransactionsId;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name="user_transactions_table") //reserved JPA name "user_transactions"
public class UserTransactions implements Serializable{

	@Id
	String userTransactionId;
	String userId;
	long  transactionId;
	String referenceNo;
	Date transactionTime;
	double tansactionAmount;
	String transactionType;
	String transactionStatus;
	double transactionComission;
	double transactionCharges;
	double totalTransactionAmount;
	String accountNo;	
	String otherParty;
	double oldBalance;
	double newBalance;	
	/*
	 * @ManyToOne(fetch = FetchType.LAZY)
	 * 
	 * @JoinColumn(name = "useraccount_id", nullable = false) private UserAccount
	 * userAccount;
	 */
	
}
