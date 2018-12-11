package com.platform.user.service;

public interface UserService {

	void registerAndLogin(String userName);

	String check(String userName);

	void save(String userName, String password);
}
