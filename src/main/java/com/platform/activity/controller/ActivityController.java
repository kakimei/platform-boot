package com.platform.activity.controller;

import com.platform.activity.controller.vo.ActivityType;
import com.platform.activity.controller.vo.ActivityVO;
import com.platform.activity.facade.ActivityFacade;
import com.platform.activity.facade.exception.ActivityException;
import com.platform.facade.Request;
import com.platform.facade.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping(path = "/activity")
public class ActivityController {

	@Autowired
	private ActivityFacade activityFacade;

	@RequestMapping(path = "/team/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public @ResponseBody
	List<ActivityVO> getReserveTeamResource() {
		try {
			return getReserveResource(ActivityType.TEAM);
		} catch (ActivityException e) {
			log.error("/activity/team/list error happened. {}", e.getMessage());
		}
		return new ArrayList<ActivityVO>();
	}

	@RequestMapping(path = "/single/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public @ResponseBody
	List<ActivityVO> getReserveSingleResource() {
		try {
			return getReserveResource(ActivityType.SINGLE);
		} catch (ActivityException e) {
			log.error("/activity/single/list error happened. {}", e.getMessage());
		}
		return new ArrayList<ActivityVO>();
	}

	private List<ActivityVO> getReserveResource(ActivityType activityType) throws ActivityException {
		Response<List<ActivityVO>> response = activityFacade.list(Request.builder().entity(activityType).build());
		if (response.getResponseType().isSuccess()) {
			return response.getEntity();
		}
		throw new ActivityException(response.getErrMsg());
	}

}
