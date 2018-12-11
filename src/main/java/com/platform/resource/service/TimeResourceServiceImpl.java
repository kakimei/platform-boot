package com.platform.resource.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.platform.reserve.service.ReservationInfoService;
import com.platform.reserve.service.dto.ReservationInfoDto;
import com.platform.resource.repository.MetaInfoRepository;
import com.platform.resource.repository.entity.IntervalUnit;
import com.platform.resource.repository.entity.MetaInfo;
import com.platform.resource.repository.entity.MetaType;
import com.platform.resource.service.dto.TimeResourceDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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


	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	private MetaInfoRepository metaInfoRepository;

	@Autowired
	private ReservationInfoService reservationInfoService;

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
				List<MetaInfo> teamMetaInfoList = metaInfoRepository.findByMetaTypeAndDeletedFalse(MetaType.TEAM);
				Map<IntervalUnit, List<MetaInfo>> teamIntervalUnitMap = teamMetaInfoList.stream().collect(Collectors.groupingBy(MetaInfo::getIntervalUnit));
				for(Map.Entry<IntervalUnit, List<MetaInfo>> entry : teamIntervalUnitMap.entrySet()){
					IntervalUnit intervalUnit = entry.getKey();
					List<MetaInfo> metaInfos = entry.getValue();
					switch (intervalUnit){
						case DAY:
							timeResourceDto.setValidDateMapDayForTEAM(buildDayResource(metaInfos, rangeStart, rangeEnd));
							break;
						case WEEK:
							timeResourceDto.setValidDateMapWeekForTEAM(buildWeekResource(metaInfos, rangeStart, rangeEnd));
							break;
					}
				}

				List<MetaInfo> singleMetaInfoList = metaInfoRepository.findByMetaTypeAndDeletedFalse(MetaType.SINGLE);
				Map<IntervalUnit, List<MetaInfo>> singleIntervalUnitMap = singleMetaInfoList.stream().collect(Collectors.groupingBy(MetaInfo::getIntervalUnit));
				for(Map.Entry<IntervalUnit, List<MetaInfo>> entry : singleIntervalUnitMap.entrySet()){
					IntervalUnit intervalUnit = entry.getKey();
					List<MetaInfo> metaInfos = entry.getValue();
					switch (intervalUnit){
						case DAY:
							timeResourceDto.setValidDateMapDayForSINGLE(buildDayResource(metaInfos, rangeStart, rangeEnd));
							break;
						case WEEK:
							timeResourceDto.setValidDateMapWeekForSINGLE(buildWeekResource(metaInfos, rangeStart, rangeEnd));
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
				Integer times = metaInfo.getTimes();
				for(int i = 0; i < times; i ++) {
					Integer hourBegin = metaInfo.getHourBegin();
					Integer minuteBegin = metaInfo.getMinuteBegin();
					Integer hourEnd = metaInfo.getHourEnd();
					Integer minuteEnd = metaInfo.getMinuteEnd();
					timeDTOList.add(new TimeResourceDto.TimeDTO(hourBegin, minuteBegin, hourEnd, minuteEnd));
				}
			}
			result.put(day, timeDTOList);
			day = day.plusDays(1);
		}
		return result;
	}

	private Map<LocalDate, List<TimeResourceDto.TimeDTO>> buildWeekResource(List<MetaInfo> metaInfos, LocalDate rangeStart, LocalDate rangeEnd){
		Map<LocalDate, List<TimeResourceDto.TimeDTO>> result = new HashMap<>();
		for(MetaInfo metaInfo : metaInfos) {
			Integer times = metaInfo.getTimes();
			for(int i = 0; i < times; i++) {
				LocalDate day = rangeStart;
				DayOfWeek dayOfWeek = day.getDayOfWeek();
				int d = dayOfWeek.getValue();
				Integer day1 = metaInfo.getDay();
				int offset = (d <= day1 ? (day1 - d) : (day1 + 7 - d));
				day = rangeStart.plusDays(offset);
				while (day.isBefore(rangeEnd)) {
					if (result.get(day) == null) {
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
		}
		return result;
	}

	@Override
	public Boolean isInValidTimeResource(Date reserveDate, String timeString, MetaType metaType, Integer peopleCount) {
		if(reserveDate == null || StringUtils.isBlank(timeString)){
			log.warn("reserve date is null or time string is null");
			return false;
		}
		TimeResourceDto.TimeDTO timeDTO = new TimeResourceDto.TimeDTO(
			getBeginHour(timeString),
			getBeginMinute(timeString),
			getEndHour(timeString),
			getEndMinute(timeString));
		String dateString = SDF.format(reserveDate);
		if(MetaType.TEAM.equals(metaType)) {
			List<Map.Entry<String, List<TimeResourceDto.TimeDTO>>> teamValidTimeResource = getTeamValidTimeResource();
			for(Map.Entry<String, List<TimeResourceDto.TimeDTO>> entry : teamValidTimeResource){
				if(dateString.equals(entry.getKey()) && entry.getValue().contains(timeDTO)){
					return true;
				}
			}
		}else{
			List<Map.Entry<String, List<TimeResourceDto.TimeDTO>>> singleValidTimeResource = getSingleValidTimeResource();
			for(Map.Entry<String, List<TimeResourceDto.TimeDTO>> entry : singleValidTimeResource){
				if(dateString.equals(entry.getKey()) && entry.getValue().contains(timeDTO) && Collections.frequency(entry.getValue(), timeDTO) >= peopleCount){
					return true;
				}
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

	@Override
	public List<Map.Entry<String, List<TimeResourceDto.TimeDTO>>> getTeamValidTimeResource() {
		TimeResourceDto timeResourceDto = buildTimeResourceDto();
		Map<LocalDate, List<TimeResourceDto.TimeDTO>> validDateMapWeekForTEAM = timeResourceDto.getValidDateMapWeekForTEAM();
		Map<String, List<TimeResourceDto.TimeDTO>> validMap = new HashMap<>();
		for (Map.Entry<LocalDate, List<TimeResourceDto.TimeDTO>> entry : validDateMapWeekForTEAM.entrySet()) {
			String formatDateString = entry.getKey().format(DateTimeFormatter.ISO_DATE);
			List<TimeResourceDto.TimeDTO> value = new ArrayList<>(entry.getValue());
			validMap.put(formatDateString, value);
		}
		List<ReservationInfoDto> allActiveReservationInfo = reservationInfoService.findAllActiveTeamReservationInfo();
		removeReservedDateTimeFromMap(allActiveReservationInfo, validMap, MetaType.TEAM);
		filterDuplicate(validMap);
		List<Map.Entry<String, List<TimeResourceDto.TimeDTO>>> sortedList = validMap.entrySet().stream().sorted(
			(Comparator.comparing(Map.Entry::getKey))).collect(Collectors.toList());
		return sortedList;
	}

	@Override
	public List<Map.Entry<String, List<TimeResourceDto.TimeDTO>>> getSingleValidTimeResource() {
		TimeResourceDto timeResourceDto = buildTimeResourceDto();
		Map<LocalDate, List<TimeResourceDto.TimeDTO>> validDateMapWeekForSingle = timeResourceDto.getValidDateMapWeekForSINGLE();
		Map<String, List<TimeResourceDto.TimeDTO>> validMap = new HashMap<>();
		for (Map.Entry<LocalDate, List<TimeResourceDto.TimeDTO>> entry : validDateMapWeekForSingle.entrySet()) {
			String formatDateString = entry.getKey().format(DateTimeFormatter.ISO_DATE);
			List<TimeResourceDto.TimeDTO> value = new ArrayList<>(entry.getValue());
			validMap.put(formatDateString, value);
		}
		List<ReservationInfoDto> allActiveReservationInfo = reservationInfoService.findAllActiveSingleReservationInfo();
		removeReservedDateTimeFromMap(allActiveReservationInfo, validMap, MetaType.SINGLE);
		filterDuplicate(validMap);
		List<Map.Entry<String, List<TimeResourceDto.TimeDTO>>> sortedList = validMap.entrySet().stream().sorted(
			(Comparator.comparing(Map.Entry::getKey))).collect(Collectors.toList());
		return sortedList;
	}

	private void removeReservedDateTimeFromMap(List<ReservationInfoDto> reservedList, Map<String, List<TimeResourceDto.TimeDTO>> map, MetaType metaType){
		for (ReservationInfoDto reservationInfoDto : reservedList) {
			List<TimeResourceDto.TimeDTO> timeDTOList = map.get(SDF.format(reservationInfoDto.getReserveDate()));
			if (CollectionUtils.isEmpty(timeDTOList)) {
				continue;
			}
			TimeResourceDto.TimeDTO timeDTO = new TimeResourceDto.TimeDTO(reservationInfoDto.getReserveBeginHH(),
				reservationInfoDto.getReserveBeginMM(),
				reservationInfoDto.getReserveEndHH(), reservationInfoDto.getReserveEndMM());
			if(MetaType.SINGLE.equals(metaType)) {
				Integer peopleCount = reservationInfoDto.getPeopleCount();
				for(int i = 0; i < peopleCount; i++) {
					timeDTOList.remove(timeDTO);
				}
			}else{
				timeDTOList.remove(timeDTO);
			}
			if (CollectionUtils.isEmpty(timeDTOList)) {
				map.remove(SDF.format(reservationInfoDto.getReserveDate()));
			}
		}
	}

	private void filterDuplicate(Map<String, List<TimeResourceDto.TimeDTO>> map){
		for(Map.Entry<String, List<TimeResourceDto.TimeDTO>> entry : map.entrySet()){
			List<TimeResourceDto.TimeDTO> list = entry.getValue();
			Set<TimeResourceDto.TimeDTO> timeDTOSet = list.stream().collect(Collectors.toSet());
			list.clear();
			list.addAll(timeDTOSet);
		}
	}
}
