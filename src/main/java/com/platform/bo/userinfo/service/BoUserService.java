package com.platform.bo.userinfo.service;

import com.platform.bo.userinfo.repository.entity.BoUser;
import com.platform.bo.userinfo.repository.entity.RoleType;
import com.platform.bo.userinfo.service.dto.BoUserDTO;

import java.util.List;

public interface BoUserService {

	BoUser save(String boUserName, String boPassword, RoleType roleType);

	BoUser resetPassword(Long boUserId);

	BoUser updateById(Long boUserId, String boUserName, RoleType roleType);

	void disableBoUser(String boUserName);

	String check(String boUserToken, String boUserName);

	BoUser findByName(String boUserName);

	BoUser findById(Long boUserId);

	BoUserDTO login(String boUserName, String boPassword);

	BoUser updatePassword(String boUserName, String oldPassword, String newPassword);

	List<BoUser> findAllUser();
}
