package com.platform.user.service;

public interface UserService {

	String registerAndLogin(String userName);

	String check(String token);

	void save(String userName, String password);
}
