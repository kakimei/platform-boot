package com.platform.sign.controller;

import com.platform.facade.Request;
import com.platform.facade.Response;
import com.platform.sign.controller.vo.SignReservationInfoVO;
import com.platform.sign.facade.SignReservationInfoFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping(path = "/sign")
public class SignReservationInfoController {

	@Autowired
	private SignReservationInfoFacade signReservationInfoFacade;

	@RequestMapping(method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public @ResponseBody
	Boolean sign(@RequestBody SignReservationInfoVO signReservationInfoVO, HttpServletRequest httpServletRequest) {
		if (signReservationInfoVO == null) {
			return false;
		}
		signReservationInfoVO.setUserName((String) httpServletRequest.getAttribute("user"));
		Request<SignReservationInfoVO> request = Request.<SignReservationInfoVO>builder().entity(signReservationInfoVO).build();
		Response<SignReservationInfoVO> response = signReservationInfoFacade.sign(request);
		return response.getResponseType().isSuccess();
	}
}
