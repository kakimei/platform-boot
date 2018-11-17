package com.platform.activity.facade;

import com.platform.activity.controller.vo.ActivityType;
import com.platform.activity.controller.vo.ActivityVO;
import com.platform.activity.service.ActivityDtoTransferBuilder;
import com.platform.activity.service.ActivityService;
import com.platform.activity.service.dto.ActivityDto;
import com.platform.facade.Request;
import com.platform.facade.Response;
import com.platform.facade.ResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ActivityFacade {

	@Autowired
	private ActivityService activityService;

	@Autowired
	private ActivityDtoTransferBuilder activityDtoTransferBuilder;

	public Response<List<ActivityVO>> list(Request<ActivityType> request){
		List<ActivityVO> result = new ArrayList<>();
		ActivityType activityType = request.getEntity();
		if(activityType == null){
			return ActivityResponse.<List<ActivityVO>>builder().responseType(ResponseType.FAIL).entity(result).build();
		}
		List<ActivityDto> activityDtoList = activityService.findActivityByType(activityDtoTransferBuilder.toActivityType(activityType));
		if(CollectionUtils.isEmpty(activityDtoList)){
			return ActivityResponse.<List<ActivityVO>>builder().responseType(ResponseType.SUCCESS).entity(result).build();
		}

		activityDtoList.forEach(activityDto -> result.add(activityDtoTransferBuilder.toVO(activityDto)));
		return ActivityResponse.<List<ActivityVO>>builder().responseType(ResponseType.SUCCESS).entity(result).build();
	}
}
