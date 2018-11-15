package com.platform.service.dto;

import com.platform.repository.entity.ActivityType;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ActivityDto {
	private ActivityType activityType;
	// TODO add some other resource about activity
}
