package com.platform.resource.service;

import com.platform.resource.service.dto.TimeResourceDto;

import java.util.Date;

public interface TimeResourceService {

	TimeResourceDto buildTimeResourceDto();

	Boolean isInValidTimeResource(Date reserveDate, String timeString);

	Integer getBeginHour(String timeString);

	Integer getBeginMinute(String timeString);

	Integer getEndHour(String timeString);

	Integer getEndMinute(String timeString);
}
