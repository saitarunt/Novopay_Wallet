package com.wallet.repositories.user;
import org.springframework.data.repository.CrudRepository;
import com.wallet.entities.user.Users;

public interface UserRepositoryImpl extends CrudRepository<Users, String>{

}
