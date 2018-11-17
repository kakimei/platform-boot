package com.platform.activity.facade;

import com.platform.activity.controller.vo.ActivityType;
import com.platform.facade.Request;
import com.platform.facade.Response;
import com.platform.facade.ResponseType;
import com.platform.reserve.controller.vo.ReserveVO;
import com.platform.reserve.facade.ReserveResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActivityFacade {


	public Response<List<ReserveVO>> list(Request<ActivityType> request){
		List<ReserveVO> result = new ArrayList<ReserveVO>();
		return ReserveResponse.builder().responseType(ResponseType.SUCCESS).entity(result).build();
	}
}
