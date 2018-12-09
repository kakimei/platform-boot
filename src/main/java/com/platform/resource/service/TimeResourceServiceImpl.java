package com.platform.resource.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.platform.resource.repository.MetaInfoRepository;
import com.platform.resource.repository.entity.IntervalUnit;
import com.platform.resource.repository.entity.MetaInfo;
import com.platform.resource.service.dto.TimeResourceDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TimeResourceServiceImpl implements TimeResourceService {

	@Value("#{environment['valid.resource.time.range']}")
	private String monthRange;

	@Value("#{environment['valid.resource.time.offset']}")
	private String dayOffset;

	@Autowired
	private MetaInfoRepository metaInfoRepository;

	private static final Cache<String, TimeResourceDto> timeResourceCache = CacheBuilder.newBuilder().softValues().expireAfterWrite(1, TimeUnit.DAYS).build();

	private static final String TIME_RESOURCE = "TIME_RESOURCE";

	private static final String TIME_FORMAT = "^(\\d+):(\\d+) ~ (\\d+):(\\d+)";

	public static final Pattern TIME_PATTERN = Pattern.compile(TIME_FORMAT);

	@Override
	public TimeResourceDto buildTimeResourceDto() {
		try {
			return timeResourceCache.get(TIME_RESOURCE, () -> {
				TimeResourceDto timeResourceDto = new TimeResourceDto();
				LocalDate rangeStart = LocalDate.now().plusDays(Integer.valueOf(dayOffset));
				LocalDate rangeEnd = rangeStart.plusMonths(Long.valueOf(monthRange));
				List<MetaInfo> metaInfoList = metaInfoRepository.findAllByDeletedFalse();
				Map<IntervalUnit, List<MetaInfo>> IntervalUnitMap = metaInfoList.stream().collect(Collectors.groupingBy(MetaInfo::getIntervalUnit));
				for(Map.Entry<IntervalUnit, List<MetaInfo>> entry : IntervalUnitMap.entrySet()){
					IntervalUnit intervalUnit = entry.getKey();
					List<MetaInfo> metaInfos = entry.getValue();
					switch (intervalUnit){
						case DAY:
							timeResourceDto.setValidDateMapDay(buildDayResource(metaInfos, rangeStart, rangeEnd));
							break;
						case WEEK:
							timeResourceDto.setValidDateMapWeek(buildWeekResource(metaInfos, rangeStart, rangeEnd));
							break;
					}
				}
				return timeResourceDto;
			});
		} catch (ExecutionException e) {
			log.error("time resource get failed. cause : {}", e.getMessage());
		}
		return null;
	}

	private Map<LocalDate, List<TimeResourceDto.TimeDTO>> buildDayResource(List<MetaInfo> metaInfos, LocalDate rangeStart, LocalDate rangeEnd){
		Map<LocalDate, List<TimeResourceDto.TimeDTO>> result = new HashMap<>();
		LocalDate day = rangeStart;
		while(day.isBefore(rangeEnd)){
			List<TimeResourceDto.TimeDTO> timeDTOList = new ArrayList<>();
			for(MetaInfo metaInfo : metaInfos){
				Integer hourBegin = metaInfo.getHourBegin();
				Integer minuteBegin = metaInfo.getMinuteBegin();
				Integer hourEnd = metaInfo.getHourEnd();
				Integer minuteEnd = metaInfo.getMinuteEnd();
				timeDTOList.add(new TimeResourceDto.TimeDTO(hourBegin, minuteBegin, hourEnd, minuteEnd));
			}
			result.put(day, timeDTOList);
			day = day.plusDays(1);
		}
		return result;
	}

	private Map<LocalDate, List<TimeResourceDto.TimeDTO>> buildWeekResource(List<MetaInfo> metaInfos, LocalDate rangeStart, LocalDate rangeEnd){
		Map<LocalDate, List<TimeResourceDto.TimeDTO>> result = new HashMap<>();
		for(MetaInfo metaInfo : metaInfos) {
			LocalDate day = rangeStart;
			DayOfWeek dayOfWeek = day.getDayOfWeek();
			int d = dayOfWeek.getValue();
			Integer day1 = metaInfo.getDay();
			int offset = (d <= day1 ? (day1-d) : (day1 + 7 - d));
			day = rangeStart.plusDays(offset);
			while (day.isBefore(rangeEnd)) {
				if(result.get(day) == null){
					result.put(day, new ArrayList<>());
				}
				Integer hourBegin = metaInfo.getHourBegin();
				Integer minuteBegin = metaInfo.getMinuteBegin();
				Integer hourEnd = metaInfo.getHourEnd();
				Integer minuteEnd = metaInfo.getMinuteEnd();
				result.get(day).add(new TimeResourceDto.TimeDTO(hourBegin, minuteBegin, hourEnd, minuteEnd));
				day = day.plusDays(7);
			}
		}
		return result;
	}

	@Override
	public Boolean isInValidTimeResource(Date reserveDate, String timeString) {
		if(reserveDate == null || StringUtils.isBlank(timeString)){
			log.warn("reserve date is null or time string is null");
			return false;
		}
		TimeResourceDto.TimeDTO timeDTO = new TimeResourceDto.TimeDTO(
			getBeginHour(timeString),
			getBeginMinute(timeString),
			getEndHour(timeString),
			getEndMinute(timeString));
		LocalDateTime localDateTime = LocalDateTime.ofInstant(reserveDate.toInstant(), ZoneId.systemDefault());
		LocalDate localDate = localDateTime.toLocalDate();
		TimeResourceDto timeResourceDto = buildTimeResourceDto();
		List<TimeResourceDto.TimeDTO> timeDTOList = timeResourceDto.getValidDateMapWeek().get(localDate);
		if(!CollectionUtils.isEmpty(timeDTOList)){
			if(timeDTOList.contains(timeDTO)){
				return true;
			}
		}

		timeDTOList = timeResourceDto.getValidDateMapDay().get(localDate);
		if(!CollectionUtils.isEmpty(timeDTOList)){
			if(timeDTOList.contains(timeDTO)){
				return true;
			}
		}
		return false;
	}

	@Override
	public Integer getBeginHour(String timeString) {
		Matcher m = TIME_PATTERN.matcher(timeString);
		if (m.find()) {
			return Integer.valueOf(m.group(1));
		}
		return 0;
	}

	@Override
	public Integer getBeginMinute(String timeString) {
		Matcher m = TIME_PATTERN.matcher(timeString);
		if (m.find()) {
			return Integer.valueOf(m.group(2));
		}
		return 0;
	}

	@Override
	public Integer getEndHour(String timeString) {
		Matcher m = TIME_PATTERN.matcher(timeString);
		if (m.find()) {
			return Integer.valueOf(m.group(3));
		}
		return 0;
	}

	@Override
	public Integer getEndMinute(String timeString) {
		Matcher m = TIME_PATTERN.matcher(timeString);
		if (m.find()) {
			return Integer.valueOf(m.group(4));
		}
		return 0;
	}
}
