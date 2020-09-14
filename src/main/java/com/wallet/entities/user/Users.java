package com.wallet.entities.user;

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
@Table(name="users") //reserved JPA name "user"
public class Users {
	
	@Id
	String userId;
	String password;
	String userName;
	String mobileNo;
	String accountNumber;
	String userType;

}
