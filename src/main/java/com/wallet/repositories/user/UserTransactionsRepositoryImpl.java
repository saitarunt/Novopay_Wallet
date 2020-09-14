package com.wallet.repositories.user;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.wallet.entities.transactions.UserTransactions;
import com.wallet.pojos.UserTransactionsId;

public interface UserTransactionsRepositoryImpl extends CrudRepository<UserTransactions, String> {

	public List<UserTransactions> findByUserId(String userId);
}
