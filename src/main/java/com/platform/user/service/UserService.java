package com.platform.user.service;

public interface UserService {

	String login(String userName, String password);

	String check(String token);

	void save(String userName, String password);
}
