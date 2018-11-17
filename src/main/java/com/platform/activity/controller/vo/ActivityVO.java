package com.platform.activity.controller.vo;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Builder
@Data
@Slf4j
@NoArgsConstructor
public class ActivityVO {

	private Long activityId;
	private ActivityType activityType;
}
