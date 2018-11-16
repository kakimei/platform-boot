package com.platform.controller.reserve;

import com.platform.controller.reserve.vo.ReserveVO;
import com.platform.controller.reserve.vo.ResourceType;
import com.platform.facade.Request;
import com.platform.facade.Response;
import com.platform.facade.reserve.ReserveFacade;
import com.platform.facade.reserve.exception.ReserveException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping(path = "/resource")
public class ResourceController {

	@Autowired
	private ReserveFacade reserveFacade;

	@RequestMapping(path = "/team/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public @ResponseBody
	List<ReserveVO> getReserveTeamResource() {
		try {
			return getReserveResource(ResourceType.TEAM);
		} catch (ReserveException e) {
			log.error("/resource/team/list error happened. {}", e.getMessage());
		}
		return new ArrayList<ReserveVO>();
	}

	@RequestMapping(path = "/single/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public @ResponseBody
	List<ReserveVO> getReserveSingleResource() {
		try {
			return getReserveResource(ResourceType.SINGLE);
		} catch (ReserveException e) {
			log.error("/resource/single/list error happened. {}", e.getMessage());
		}
		return new ArrayList<ReserveVO>();
	}

	private List<ReserveVO> getReserveResource(ResourceType resourceType) throws ReserveException {
		Response<List<ReserveVO>> response = reserveFacade.list(Request.builder().entity(resourceType).build());
		if (response.getResponseType().isSuccess()) {
			return response.getEntity();
		}
		throw new ReserveException(response.getErrMsg());
	}

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
