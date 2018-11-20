package com.platform.activity.facade;

import com.platform.activity.controller.vo.ActivityType;
import com.platform.activity.controller.vo.ActivityVO;
import com.platform.activity.facade.exception.ActivityException;
import com.platform.activity.service.ActivityDtoTransferBuilder;
import com.platform.activity.service.ActivityService;
import com.platform.activity.service.dto.ActivityDto;
import com.platform.facade.Request;
import com.platform.facade.Response;
import com.platform.facade.ResponseType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
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

	@Transactional(propagation = Propagation.REQUIRED)
	public Response<ActivityVO> save(Request<ActivityVO> request){
		ActivityVO activityVO = request.getEntity();
		try {
			activityService.saveOrUpdate(activityDtoTransferBuilder.toDto(activityVO));
			return ActivityResponse.<ActivityVO>builder().entity(activityVO).responseType(ResponseType.SUCCESS).build();
		}catch (ActivityException e){
			log.error("activity save error happened. {}", e.getMessage());
		}
		return ActivityResponse.<ActivityVO>builder().responseType(ResponseType.FAIL).build();
	}
}
