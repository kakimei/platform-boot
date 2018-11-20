package com.platform.activity.service;

import com.platform.activity.facade.exception.ActivityException;
import com.platform.activity.repository.ActivityRepository;
import com.platform.activity.repository.entity.Activity;
import com.platform.activity.repository.entity.ActivityType;
import com.platform.activity.service.dto.ActivityDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
public class ActivityServiceImpl implements ActivityService{

	@Autowired
	private ActivityRepository activityRepository;

	@Autowired
	private ActivityDtoTransferBuilder activityDtoTransferBuilder;

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public List<ActivityDto> findActivityByType(ActivityType activityType) {
		List<ActivityDto> result = new ArrayList<>();
		List<Activity> activityList = activityRepository.findActivitiesByActivityType(activityType);
		if(CollectionUtils.isEmpty(activityList)){
			return result;
		}

		activityList.forEach(activity -> result.add(activityDtoTransferBuilder.toDto(activity)));
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveOrUpdate(ActivityDto activityDto) throws ActivityException {
		try {
			if (activityDto.getActivityId() != null) {
				Activity activity = activityRepository.findActivityByActivityId(activityDto.getActivityId());
				BeanUtils.copyProperties(activityDto, activity, "id");
				activityRepository.save(activity);
			} else {
				activityRepository.save(activityDtoTransferBuilder.toEntity(activityDto));
			}
		}catch (Exception e){
			throw new ActivityException("activity saveOrUpdate error happened.", e);
		}
	}
}
