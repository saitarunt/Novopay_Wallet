package com.wallet.entities.transactions;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
@Table(name="transactions")
public class Transactions {

	@Id
	long transactionId;
	String referenceNo;
	String fromAccountNo;
	String toAccountNumber;
	Date transactionTime;
	double transactionAmount;
	String transactionType;
	double transactionCommission;
	double transactionCharges;
	String transactionStatus;
	String initiatedByUser;
	
}
