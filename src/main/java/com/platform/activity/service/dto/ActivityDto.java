package com.platform.activity.service.dto;

import com.platform.activity.repository.entity.ActivityType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
public class ActivityDto {

	private Long activityId;
	private ActivityType activityType;
	// TODO add some other resource about activity
}
