package com.platform.user.facade;

import com.platform.facade.Request;
import com.platform.facade.Response;
import com.platform.facade.ResponseType;
import com.platform.reserve.facade.ReserveResponse;
import com.platform.user.controller.vo.UserVO;
import com.platform.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserFacade {

	@Autowired
	private UserService userService;

	public Response<UserVO> login(Request<UserVO> request){
		UserVO userVO = request.getEntity();
		String token = userService.registerAndLogin(userVO.getUserName());
		if(StringUtils.isNotBlank(token)){
			userVO.setToken(token);
			return ReserveResponse.<UserVO>builder().responseType(ResponseType.SUCCESS).entity(userVO).build();
		}
		return ReserveResponse.<UserVO>builder().responseType(ResponseType.FAIL).entity(userVO).build();
	}

	public Response<UserVO> register(Request<UserVO> request){
		UserVO userVO = request.getEntity();
		try {
			userService.save(userVO.getUserName(), userVO.getPassword());
			return ReserveResponse.<UserVO>builder().responseType(ResponseType.SUCCESS).entity(userVO).build();
		}catch (Exception e){
			return ReserveResponse.<UserVO>builder().responseType(ResponseType.FAIL).entity(userVO).build();
		}
	}

}
