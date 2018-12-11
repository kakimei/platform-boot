package com.platform.resource.service;

import com.platform.resource.repository.entity.MetaType;
import com.platform.resource.service.dto.TimeResourceDto;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface TimeResourceService {

	TimeResourceDto buildTimeResourceDto();

	List<Map.Entry<String, List<TimeResourceDto.TimeDTO>>> getTeamValidTimeResource();

	List<Map.Entry<String, List<TimeResourceDto.TimeDTO>>> getSingleValidTimeResource();

	Boolean isInValidTimeResource(Date reserveDate, String timeString, MetaType metaType, Integer peopleCount);

	Integer getBeginHour(String timeString);

	Integer getBeginMinute(String timeString);

	Integer getEndHour(String timeString);

	Integer getEndMinute(String timeString);
}
