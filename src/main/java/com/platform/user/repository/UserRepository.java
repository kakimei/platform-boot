package com.platform.user.repository;

import com.platform.user.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository  extends JpaRepository<User, Long> {

	User findByUserNameAndActiveIsTrue(String userName);
}
