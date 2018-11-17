package com.platform.reserve.controller;

import com.platform.reserve.controller.vo.ReserveVO;
import com.platform.facade.Request;
import com.platform.facade.Response;
import com.platform.reserve.facade.ReserveFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@RequestMapping(path = "/resource")
public class ResourceController {

	@Autowired
	private ReserveFacade reserveFacade;

	@RequestMapping(path = "/reserve", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public @ResponseBody Boolean reserve(@RequestBody ReserveVO reserveVO){
		if(reserveVO == null || !reserveVO.canReserve()){
			return false;
		}
		Request<ReserveVO> request = Request.builder().entity(reserveVO).build();
		Response<ReserveVO> response = reserveFacade.reserve(request);
		return response.getResponseType().isSuccess();
	}
}
