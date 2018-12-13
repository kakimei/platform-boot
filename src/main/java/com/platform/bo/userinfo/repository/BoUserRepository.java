package com.platform.bo.userinfo.repository;

import com.platform.bo.userinfo.repository.entity.BoUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoUserRepository extends JpaRepository<BoUser, Long> {

	BoUser findByBoUserNameAndActiveTrue(String boUserName);

	List<BoUser> findByActiveTrueAndBoUserNameNot(String boUserName);
}
