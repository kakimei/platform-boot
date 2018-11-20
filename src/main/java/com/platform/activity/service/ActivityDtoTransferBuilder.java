package com.platform.activity.service;

import com.platform.activity.controller.vo.ActivityVO;
import com.platform.activity.repository.entity.Activity;
import com.platform.activity.repository.entity.ActivityType;
import com.platform.activity.service.dto.ActivityDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ActivityDtoTransferBuilder {

	public ActivityDto toDto(ActivityVO activityVO){
		if(activityVO == null){
			log.warn("activityVO is null");
			return null;
		}
		ActivityDto activityDto = new ActivityDto();
		BeanUtils.copyProperties(activityVO, activityDto, "activityType");
		activityDto.setActivityType(toActivityType(activityVO.getActivityType()));
		return activityDto;
	}

	public ActivityVO toVO(ActivityDto activityDto){
		if(activityDto == null){
			log.warn("activityDto is null");
			return null;
		}
		ActivityVO activityVO = new ActivityVO();
		BeanUtils.copyProperties(activityDto, activityVO, "activityType");
		activityVO.setActivityType(toActivityType(activityDto.getActivityType()));
		return activityVO;
	}

	public ActivityDto toDto(Activity activity){
		if(activity == null){
			log.warn("activity is null");
			return null;
		}
		ActivityDto activityDto = new ActivityDto();
		BeanUtils.copyProperties(activity, activityDto);
		return activityDto;
	}

	public Activity toEntity(ActivityDto activityDto){
		if(activityDto == null){
			log.warn("activityDto is null");
			return null;
		}
		Activity activity = new Activity();
		BeanUtils.copyProperties(activityDto, activity);
		return activity;
	}

	public ActivityType toActivityType(com.platform.activity.controller.vo.ActivityType activityType){
		return ActivityType.valueOf(activityType.name());
	}

	public com.platform.activity.controller.vo.ActivityType toActivityType(ActivityType activityType){
		return com.platform.activity.controller.vo.ActivityType.valueOf(activityType.name());
	}
}
