package com.platform.bo.userinfo.service;

import com.platform.bo.userinfo.repository.entity.BoUser;
import com.platform.bo.userinfo.repository.entity.RoleType;

import java.util.List;

public interface BoUserService {

	BoUser save(String boUserName, String boPassword, RoleType roleType);

	void disableBoUser(String boUserName);

	String check(String boUserToken, String boUserName);

	BoUser findByName(String boUserName);

	String login(String boUserName, String boPassword);

	BoUser updatePassword(String boUserName, String oldPassword, String newPassword);

	List<BoUser> findAllUser();
}
