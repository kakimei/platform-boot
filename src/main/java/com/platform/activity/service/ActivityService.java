package com.platform.activity.service;

import com.platform.activity.repository.entity.ActivityType;
import com.platform.activity.service.dto.ActivityDto;

import java.util.List;

public interface ActivityService {

	List<ActivityDto> findActivityByType(ActivityType activityType);

	void saveOrUpdate(ActivityDto activityDto);
}
