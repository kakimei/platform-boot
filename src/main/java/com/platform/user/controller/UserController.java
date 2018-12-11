package com.platform.user.controller;

import com.platform.facade.Request;
import com.platform.facade.Response;
import com.platform.user.controller.vo.UserVO;
import com.platform.user.facade.UserFacade;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "/user")
public class UserController {

	@Autowired
	private UserFacade userFacade;

	@RequestMapping(path = "/login", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public @ResponseBody
	UserVO login(@RequestBody UserVO userVO) {
		if (userVO == null || StringUtils.isBlank(userVO.getUserName())) {
			return userVO;
		}
		Request<UserVO> request = Request.<UserVO>builder().entity(userVO).build();
		Response<UserVO> userVOResponse = userFacade.login(request);
		if (!userVOResponse.getResponseType().isSuccess()) {
			log.warn("login failed. user name : {}", userVO.getUserName());
		}
		return userVOResponse.getEntity();
	}

	@RequestMapping(path = "/register", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public @ResponseBody
	Boolean register(@RequestBody UserVO userVO) {
		if (userVO == null || StringUtils.isBlank(userVO.getUserName()) || StringUtils.isBlank(userVO.getPassword())) {
			return false;
		}
		Request<UserVO> request = Request.<UserVO>builder().entity(userVO).build();
		Response<UserVO> userVOResponse = userFacade.register(request);
		if (!userVOResponse.getResponseType().isSuccess()) {
			log.warn("register failed. user name : {}", userVO.getUserName());
		}
		return userVOResponse.getResponseType().isSuccess();
	}
}
