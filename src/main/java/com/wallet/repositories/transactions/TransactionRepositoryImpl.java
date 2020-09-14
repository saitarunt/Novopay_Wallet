package com.wallet.repositories.transactions;

import org.springframework.data.repository.CrudRepository;

import com.wallet.entities.transactions.Transactions;

public interface TransactionRepositoryImpl extends CrudRepository<Transactions, Long> {

}
