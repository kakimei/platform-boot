package com.platform.reserve.controller;

import com.platform.reserve.controller.vo.ReserveVO;
import com.platform.facade.Request;
import com.platform.facade.Response;
import com.platform.reserve.facade.ReserveFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping(path = "/reserve")
public class ReserveController {

	@Autowired
	private ReserveFacade reserveFacade;

	@Value("#{environment['people.number.threshold']}")
	protected int PEOPLE_NUMBER_THRESHOLD;

	@RequestMapping(method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public @ResponseBody Boolean reserve(@RequestBody ReserveVO reserveVO, HttpServletResponse servletResponse){
		servletResponse.setHeader("Access-Control-Allow-Origin", "*");
		if(reserveVO == null){
			return false;
		}
		reserveVO.setPeopleNumberThreshold(PEOPLE_NUMBER_THRESHOLD);
		if(!reserveVO.canReserve()){
			return false;
		}
		Request<ReserveVO> request = Request.<ReserveVO>builder().entity(reserveVO).build();
		Response<ReserveVO> response = reserveFacade.reserve(request);
		return response.getResponseType().isSuccess();
	}
}
