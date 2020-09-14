package com.wallet.entities.user;

import java.sql.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


import com.wallet.entities.transactions.UserTransactions;

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
@Table(name="useraccounts")
public class UserAccount {

	@Id
	String userId;
	String accountNo;	
	double currentBalance;
	Date accountCreatedDate;	
		
}
