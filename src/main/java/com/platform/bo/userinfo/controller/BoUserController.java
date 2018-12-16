package com.platform.bo.userinfo.controller;

import com.platform.bo.userinfo.controller.vo.BoUserVO;
import com.platform.bo.userinfo.repository.entity.BoUser;
import com.platform.bo.userinfo.service.BoUserService;
import com.platform.bo.userinfo.service.dto.BoUserDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping(path = "/bo/user")
public class BoUserController {

	@Autowired
	private BoUserService boUserService;

	@RequestMapping(path = "/addBoUser", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public @ResponseBody Boolean addBoUser(@RequestBody BoUserVO boUserVO, HttpServletRequest httpServletRequest){
		if(boUserVO == null || !checkInsertDelete(httpServletRequest)){
			return false;
		}

		BoUser boUserDb = boUserService.save(boUserVO.getBoUserName(), boUserVO.getBoUserPass(), boUserVO.getRoleType());
		return boUserDb != null;
	}

	@RequestMapping(path = "/password/reset", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public @ResponseBody Boolean resetPassword(@RequestBody BoUserVO boUserVO, HttpServletRequest httpServletRequest){
		if(boUserVO == null || !checkInsertDelete(httpServletRequest)){
			return false;
		}

		BoUser boUserDb = boUserService.resetPassword(boUserVO.getBoUserId());
		return boUserDb != null;
	}

	@RequestMapping(path = "/update", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public @ResponseBody Boolean updateBoUser(@RequestBody BoUserVO boUserVO, HttpServletRequest httpServletRequest){
		if(boUserVO == null || !checkInsertDelete(httpServletRequest)){
			return false;
		}

		BoUser boUserDb = boUserService.updateById(boUserVO.getBoUserId(), boUserVO.getBoUserName(), boUserVO.getRoleType());
		return boUserDb != null;
	}

	@RequestMapping(path = "/find", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public @ResponseBody BoUserVO find(@RequestParam String boUserId, HttpServletRequest httpServletRequest){
		if(StringUtils.isBlank(boUserId) || !checkInsertDelete(httpServletRequest)){
			return null;
		}
		BoUser boUser = boUserService.findById(Long.valueOf(boUserId));
		if(boUser == null){
			return null;
		}
		BoUserVO result = new BoUserVO();
		result.setBoUserName(boUser.getBoUserName());
		result.setRoleType(boUser.getRoleType());
		result.setBoUserId(boUser.getBoUserId());
		return result;
	}

	@RequestMapping(path = "/login", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public @ResponseBody BoUserVO login(@RequestBody BoUserVO boUserVO){
		if(boUserVO == null){
			return null;
		}
		BoUserDTO boUserDTO = boUserService.login(boUserVO.getBoUserName(), boUserVO.getBoUserPass());
		if(boUserDTO == null){
			return null;
		}
		BoUserVO result = new BoUserVO();
		result.setBoUserName(boUserDTO.getBoUserName());
		result.setRoleType(boUserDTO.getRoleType());
		result.setToken(boUserDTO.getToken());
		return result;
	}

	@RequestMapping(path = "/deleteBoUser", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public @ResponseBody Boolean deleteBoUser(@RequestBody BoUserVO boUserVO, HttpServletRequest httpServletRequest){
		if(boUserVO == null || !checkInsertDelete(httpServletRequest)){
			return false;
		}
		boUserService.disableBoUser(boUserVO.getBoUserName());
		return true;
	}

	private boolean checkInsertDelete(HttpServletRequest httpServletRequest){
		String boUserName = (String)httpServletRequest.getAttribute("boUser");
		BoUser boUser = boUserService.findByName(boUserName);
		if(!boUser.getRoleType().canModifyBoUser()){
			return false;
		}
		return true;
	}

	@RequestMapping(path = "/password/update", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public @ResponseBody Boolean updatePassword(@RequestBody BoUserVO boUserVO){
		if(boUserVO == null){
			return false;
		}
		BoUser boUser = boUserService.updatePassword(boUserVO.getBoUserName(), boUserVO.getOldBoUserPass(), boUserVO.getBoUserPass());
		return boUser != null;
	}

	@RequestMapping(path = "/all", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public @ResponseBody
	List<BoUserVO> allBoUser(HttpServletRequest httpServletRequest){
		List<BoUserVO> result = new ArrayList<>();
		String boUserName = (String)httpServletRequest.getAttribute("boUser");
		BoUser boUser = boUserService.findByName(boUserName);
		if(!boUser.getRoleType().canSeeAllBoUser()){
			return result;
		}
		List<BoUser> allUser = boUserService.findAllUser();
		return allUser.stream().filter(b -> !b.getBoUserName().equals(boUser.getBoUserName())).map(b -> {
			BoUserVO boUserVO = new BoUserVO();
			boUserVO.setBoUserId(b.getBoUserId());
			boUserVO.setBoUserName(b.getBoUserName());
			boUserVO.setRoleType(b.getRoleType());
			return boUserVO;
		}).collect(Collectors.toList());
	}
}
