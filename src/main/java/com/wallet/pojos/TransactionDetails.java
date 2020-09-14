package com.wallet.pojos;

import java.sql.Date;

import javax.persistence.Entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@ToString
public class TransactionDetails {

	long transactionId;
	Date transactionTime;
	String transactionType;
	String transactionStatus;
	double commissions;
	double charges;
}
