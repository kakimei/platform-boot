package com.platform.activity.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Builder
@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class ActivityVO {

	private Long activityId;
	private ActivityType activityType;
}
