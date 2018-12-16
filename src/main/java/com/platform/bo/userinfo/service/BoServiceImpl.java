package com.platform.bo.userinfo.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.platform.bo.userinfo.repository.BoUserRepository;
import com.platform.bo.userinfo.repository.entity.BoUser;
import com.platform.bo.userinfo.repository.entity.RoleType;
import com.platform.bo.userinfo.service.dto.BoUserDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
public class BoServiceImpl implements BoUserService {

	@Autowired
	private BoUserRepository boUserRepository;

	private static final Cache<String, String> boUserCache = CacheBuilder.newBuilder().softValues().expireAfterAccess(30, TimeUnit.MINUTES).build();

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public BoUser save(String boUserName, String boPassword, RoleType roleType) {
		if(StringUtils.isBlank(boUserName) || StringUtils.isBlank(boPassword) || roleType == null){
			return null;
		}
		BoUser boUserDb = boUserRepository.findByBoUserNameAndActiveTrue(boUserName);
		if(boUserDb != null){
			log.warn("user does exist. bo user name : {}", boUserName);
			return null;
		}
		BoUser boUser = BoUser.builder().boUserName(boUserName).boPassword(boPassword).roleType(roleType).active(true).build();
		boUser = boUserRepository.save(boUser);
		return boUser;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public BoUser resetPassword(Long boUserId) {
		if(boUserId == null || boUserId == 0){
			return null;
		}
		BoUser boUserDb = boUserRepository.findByBoUserIdAndActiveTrue(boUserId);
		if(boUserDb == null){
			log.warn("user does not exist. bo user id : {}", boUserId);
			return null;
		}
		boUserDb.setBoPassword("11111111");
		boUserDb = boUserRepository.save(boUserDb);
		return boUserDb;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public BoUser updateById(Long boUserId, String boUserName, RoleType roleType) {
		if(boUserId == null || boUserId == 0 || StringUtils.isBlank(boUserName) || roleType == null){
			return null;
		}
		BoUser boUserDb = boUserRepository.findByBoUserIdAndActiveTrue(boUserId);
		if(boUserDb == null){
			log.warn("user does not exist. bo user name : {}", boUserName);
			return null;
		}
		boUserDb.setBoUserName(boUserName);
		boUserDb.setRoleType(roleType);
		boUserDb = boUserRepository.save(boUserDb);
		return boUserDb;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void disableBoUser(String boUserName) {
		BoUser boUser = boUserRepository.findByBoUserNameAndActiveTrue(boUserName);
		if(boUser == null){
			return;
		}
		boUser.setActive(false);
		boUserRepository.save(boUser);
		ConcurrentMap<String, String> boUserMap = boUserCache.asMap();
		for(Map.Entry<String, String> entry : boUserMap.entrySet()){
			if(boUser.getBoUserName().equals(entry.getValue())){
				entry.setValue(null);
			}
		}
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public String check(String boUserToken, String boUserName) {
		try {
			return boUserCache.get(boUserToken, () -> {
				BoUser boUser = findByName(boUserName);
				return boUser == null ? null : boUser.getBoUserName();
			});
		} catch (ExecutionException e) {
			log.warn("bo user cache error. {}", e.getMessage());
		}
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public BoUser findByName(String boUserName) {
		if(StringUtils.isBlank(boUserName)){
			return null;
		}
		return boUserRepository.findByBoUserNameAndActiveTrue(boUserName);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public BoUser findById(Long boUserId) {
		if(boUserId == null || boUserId == 0){
			return null;
		}
		return boUserRepository.findByBoUserIdAndActiveTrue(boUserId);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public BoUserDTO login(String boUserName, String boPassword) {
		if(StringUtils.isBlank(boUserName) || StringUtils.isBlank(boPassword)){
			return null;
		}
		BoUser boUser = boUserRepository.findByBoUserNameAndActiveTrue(boUserName);
		if(boUser == null){
			return null;
		}
		if(boPassword.equals(boUser.getBoPassword())){
			String boUserToken = UUID.randomUUID().toString();
			boUserCache.put(boUserToken, boUserName);
			return BoUserDTO.builder()
				.boUserName(boUser.getBoUserName())
				.roleType(boUser.getRoleType())
				.token(boUserToken).build();
		}
		return null;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public BoUser updatePassword(String boUserName, String oldPassword, String newPassword) {
		if(StringUtils.isBlank(boUserName) || StringUtils.isBlank(oldPassword) || StringUtils.isBlank(newPassword)){
			return null;
		}
		BoUser boUserDb = boUserRepository.findByBoUserNameAndActiveTrue(boUserName);
		if(boUserDb == null || !oldPassword.equals(boUserDb.getBoPassword())){
			return null;
		}
		boUserDb.setBoPassword(newPassword);
		boUserDb = boUserRepository.save(boUserDb);
		return boUserDb;
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public List<BoUser> findAllUser() {
		return boUserRepository.findByActiveTrueAndBoUserNameNot("admin");
	}
}
