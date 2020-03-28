package com.platform.user.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.platform.user.repository.UserRepository;
import com.platform.user.repository.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class UserServiceImpl implements UserService{

	@Autowired
	private UserRepository userRepository;

	private static final Cache<String, String> userCache = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).build();

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public void registerAndLogin(String userName) {
		User dbUser = userRepository.findByUserNameAndActiveIsTrue(userName);
		if(dbUser == null){
			dbUser = new User();
			dbUser.setUserName(userName);
			dbUser.setActive(true);
			userRepository.save(dbUser);
		}

		userCache.put(userName, userName);
	}

	@Override
	public String check(String userName) {
		if(StringUtils.isBlank(userName)){
			return "";
		}
		String user = null;
		try {
			user = userCache.get(userName, () -> {
				User user1 = userRepository.findByUserNameAndActiveIsTrue(userName);
				return user1 == null ? "" : user1.getUserName();
			});
		} catch (ExecutionException e) {
			log.error("cache error : {}", e.getMessage());
		}
		return user;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(String userName, String password) {
		if(StringUtils.isBlank(userName) || StringUtils.isBlank(password)){
			log.warn("user name and password can not be blank.");
			return;
		}
		User dbUser = userRepository.findByUserNameAndActiveIsTrue(userName);
		if(dbUser != null){
			log.warn("user has existed. user name : {}", userName);
			return;
		}
		dbUser = new User();
		dbUser.setUserName(userName);
		dbUser.setPassword(password);
		dbUser.setActive(true);
		userRepository.save(dbUser);
	}
}
