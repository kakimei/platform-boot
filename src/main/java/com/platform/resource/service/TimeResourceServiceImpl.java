package com.platform.resource.service;

import com.platform.reserve.repository.ReservationInfoRepository;
import com.platform.reserve.repository.entity.ActivityType;
import com.platform.reserve.repository.entity.ReservationInfo;
import com.platform.resource.repository.MetaInfoRepository;
import com.platform.resource.repository.TimeResourceRepository;
import com.platform.resource.repository.entity.IntervalUnit;
import com.platform.resource.repository.entity.MetaInfo;
import com.platform.resource.repository.entity.MetaType;
import com.platform.resource.repository.entity.TimeResource;
import com.platform.resource.service.dto.TimeResourceDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
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

    @Value("#{environment['week.single.max']}")
    private String weekSingleMax;

    @Value("#{environment['people.number.threshold']}")
    private int peopleNumberThreshold;

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private MetaInfoRepository metaInfoRepository;

    @Autowired
    private TimeResourceRepository timeResourceRepository;

    @Autowired
    private ReservationInfoRepository reservationInfoRepository;

    private static final String TIME_FORMAT = "^(\\d+):(\\d+) ~ (\\d+):(\\d+)";

    public static final Pattern TIME_PATTERN = Pattern.compile(TIME_FORMAT);

    private static final String TIME_STRING_FORMAT = "%s:%s ~ %s:%s";

    @Override
    public TimeResourceDto buildTimeResourceDto() {
        TimeResourceDto timeResourceDto = new TimeResourceDto();
        LocalDate rangeStart = LocalDate.now().plusDays(Integer.valueOf(dayOffset));
        LocalDate rangeEnd = rangeStart.plusMonths(Long.valueOf(monthRange));
        List<MetaInfo> teamMetaInfoList = metaInfoRepository.findByMetaTypeAndDeletedFalse(MetaType.TEAM);
        Map<IntervalUnit, List<MetaInfo>> teamIntervalUnitMap = teamMetaInfoList.stream().collect(
                Collectors.groupingBy(MetaInfo::getIntervalUnit));
        for (Map.Entry<IntervalUnit, List<MetaInfo>> entry : teamIntervalUnitMap.entrySet()) {
            IntervalUnit intervalUnit = entry.getKey();
            List<MetaInfo> metaInfos = entry.getValue();
            switch (intervalUnit) {
                case DAY:
                    timeResourceDto.setValidDateMapDayForTEAM(buildDayResource(metaInfos, rangeStart, rangeEnd));
                    break;
                case WEEK:
                    timeResourceDto.setValidDateMapWeekForTEAM(buildWeekResource(metaInfos, rangeStart, rangeEnd));
                    break;
            }
        }

        List<MetaInfo> singleMetaInfoList = metaInfoRepository.findByMetaTypeAndDeletedFalse(MetaType.SINGLE);
        Map<IntervalUnit, List<MetaInfo>> singleIntervalUnitMap = singleMetaInfoList.stream().collect(
                Collectors.groupingBy(MetaInfo::getIntervalUnit));
        for (Map.Entry<IntervalUnit, List<MetaInfo>> entry : singleIntervalUnitMap.entrySet()) {
            IntervalUnit intervalUnit = entry.getKey();
            List<MetaInfo> metaInfos = entry.getValue();
            switch (intervalUnit) {
                case DAY:
                    timeResourceDto.setValidDateMapDayForSINGLE(buildDayResource(metaInfos, rangeStart, rangeEnd));
                    break;
                case WEEK:
                    timeResourceDto.setValidDateMapWeekForSINGLE(buildWeekResource(metaInfos, rangeStart, rangeEnd));
                    break;
            }
        }
        return timeResourceDto;
    }

    private Map<LocalDate, List<TimeResourceDto.TimeDTO>> buildDayResource(List<MetaInfo> metaInfos, LocalDate rangeStart, LocalDate rangeEnd) {
        Map<LocalDate, List<TimeResourceDto.TimeDTO>> result = new HashMap<>();
        LocalDate day = rangeStart;
        while (day.isBefore(rangeEnd)) {
            List<TimeResourceDto.TimeDTO> timeDTOList = new ArrayList<>();
            for (MetaInfo metaInfo : metaInfos) {
                Integer times = metaInfo.getTimes();
                Integer hourBegin = metaInfo.getHourBegin();
                Integer minuteBegin = metaInfo.getMinuteBegin();
                Integer hourEnd = metaInfo.getHourEnd();
                Integer minuteEnd = metaInfo.getMinuteEnd();
                timeDTOList.add(new TimeResourceDto.TimeDTO(hourBegin, minuteBegin, hourEnd, minuteEnd, times));
            }
            result.put(day, timeDTOList);
            day = day.plusDays(1);
        }
        return result;
    }

    private Map<LocalDate, List<TimeResourceDto.TimeDTO>> buildWeekResource(List<MetaInfo> metaInfos, LocalDate rangeStart, LocalDate rangeEnd) {
        Map<LocalDate, List<TimeResourceDto.TimeDTO>> result = new HashMap<>();
        for (MetaInfo metaInfo : metaInfos) {
            Integer times = metaInfo.getTimes();
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
                result.get(day).add(new TimeResourceDto.TimeDTO(hourBegin, minuteBegin, hourEnd, minuteEnd, times));
                day = day.plusDays(7);
            }
        }
        return result;
    }

    private Boolean containsTime(List<TimeResourceDto.TimeDTO> timeDTOList, Integer beginHour, Integer beginMinute, Integer endHour,
                                 Integer endMinute, Integer times) {
        for (TimeResourceDto.TimeDTO timeDTO : timeDTOList) {
            if (timeDTO.getBeginHour() == beginHour && timeDTO.getBeginMinute() == beginMinute && timeDTO.getEndHour() == endHour
                    && timeDTO.getEndMinute() == endMinute && timeDTO.getTimes() >= times) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean isInValidTimeResource(Date reserveDate, String timeString, MetaType metaType, Integer peopleCount) {
        if (reserveDate == null || StringUtils.isBlank(timeString)) {
            log.warn("reserve date is null or time string is null");
            return false;
        }

        String dateString = SDF.format(reserveDate);
        if (MetaType.TEAM.equals(metaType)) {
            List<Map.Entry<String, List<TimeResourceDto.TimeDTO>>> teamValidTimeResource = getTeamValidTimeResource();
            for (Map.Entry<String, List<TimeResourceDto.TimeDTO>> entry : teamValidTimeResource) {
                if (dateString.equals(entry.getKey()) && containsTime(entry.getValue(), getBeginHour(timeString), getBeginMinute(timeString),
                        getEndHour(timeString), getEndMinute(timeString), 1)) {
                    return true;
                }
            }
        } else {
            List<Map.Entry<String, List<TimeResourceDto.TimeDTO>>> singleValidTimeResource = getSingleValidTimeResource();
            for (Map.Entry<String, List<TimeResourceDto.TimeDTO>> entry : singleValidTimeResource) {
                if (dateString.equals(entry.getKey()) && containsTime(entry.getValue(), getBeginHour(timeString), getBeginMinute(timeString),
                        getEndHour(timeString), getEndMinute(timeString), peopleCount)) {
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
        List<TimeResource> validTeamTimeResource = timeResourceRepository.findByMetaTypeAndRemainTimesGreaterThanAndActiveIsTrueOrderByReservableDateAscHourBeginAsc(MetaType.TEAM, 0);
        return buildSortedList(validTeamTimeResource);
    }

    @Override
    public List<Map.Entry<String, List<TimeResourceDto.TimeDTO>>> getSingleValidTimeResource() {
        List<TimeResource> validSingleTimeResource = timeResourceRepository.findByMetaTypeAndRemainTimesGreaterThanAndActiveIsTrueOrderByReservableDateAscHourBeginAsc(MetaType.SINGLE, 0);
        return buildSortedList(validSingleTimeResource);
    }

    private List<Map.Entry<String, List<TimeResourceDto.TimeDTO>>> buildSortedList(List<TimeResource> validTimeResource){
        Map<String, List<TimeResourceDto.TimeDTO>> result = new HashMap<>();
        for(TimeResource timeResource : validTimeResource){
            String reservableDate = SDF.format(timeResource.getReservableDate());
            List<TimeResourceDto.TimeDTO> timeDTOList = result.getOrDefault(reservableDate, new ArrayList<>());
            TimeResourceDto.TimeDTO timeDTO = new TimeResourceDto.TimeDTO(timeResource.getHourBegin(), timeResource.getMinuteBegin(), timeResource.getHourEnd(), timeResource.getMinuteEnd(), timeResource.getRemainTimes());
            timeDTOList.add(timeDTO);
            result.putIfAbsent(reservableDate, timeDTOList);
        }
        List<Map.Entry<String, List<TimeResourceDto.TimeDTO>>> sortedList = result.entrySet().stream().sorted(
                (Comparator.comparing(Map.Entry::getKey))).collect(Collectors.toList());
        return sortedList;
    }

//    private void removeReservedDateTimeFromMap(List<ReservationInfoDto> reservedList, Map<String, List<TimeResourceDto.TimeDTO>> map,
//                                               MetaType metaType) {
//        Map<String, Integer> weekGroupPeopleCount = reservedList.stream().collect(
//                Collectors.groupingBy(ReservationInfoDto::getYearWeek, Collectors.summingInt(ReservationInfoDto::getPeopleCount)));
//        for (ReservationInfoDto reservationInfoDto : reservedList) {
//            List<TimeResourceDto.TimeDTO> timeDTOList = map.get(SDF.format(reservationInfoDto.getReserveDate()));
//            if (CollectionUtils.isEmpty(timeDTOList)) {
//                continue;
//            }
//
//            if (MetaType.SINGLE.equals(metaType)) {
//                Integer peopleCount = reservationInfoDto.getPeopleCount();
//                Integer weekHasUsed = weekGroupPeopleCount.get(reservationInfoDto.getYearWeek());
//                Integer weekRemained = Integer.valueOf(weekSingleMax) - weekHasUsed;
//                log.info("weekRemained: {}", weekRemained);
//                timeDTOList = calculateTimeList(timeDTOList, reservationInfoDto.getReserveBeginHH(), reservationInfoDto.getReserveBeginMM(),
//                        reservationInfoDto.getReserveEndHH(), reservationInfoDto.getReserveEndMM(), peopleCount, weekRemained);
//            } else {
//                timeDTOList = calculateTimeList(timeDTOList, reservationInfoDto.getReserveBeginHH(), reservationInfoDto.getReserveBeginMM(),
//                        reservationInfoDto.getReserveEndHH(), reservationInfoDto.getReserveEndMM(), 1, null);
//            }
//            if (CollectionUtils.isEmpty(timeDTOList)) {
//                map.remove(SDF.format(reservationInfoDto.getReserveDate()));
//            } else {
//                map.put(SDF.format(reservationInfoDto.getReserveDate()), timeDTOList);
//            }
//        }
//    }

//    private List<TimeResourceDto.TimeDTO> calculateTimeList(List<TimeResourceDto.TimeDTO> timeDTOList, Integer beginHour, Integer beginMinute,
//                                                            Integer endHour,
//                                                            Integer endMinute, Integer times, Integer globalRemained) {
//        List<TimeResourceDto.TimeDTO> result = new ArrayList<>();
//        for (TimeResourceDto.TimeDTO timeDTO : timeDTOList) {
//            TimeResourceDto.TimeDTO timeDTO1 = new TimeResourceDto.TimeDTO(timeDTO.getBeginHour(), timeDTO.getBeginMinute(), timeDTO.getEndHour(),
//                    timeDTO.getEndMinute(), timeDTO.getTimes());
//            if (timeDTO1.getBeginHour() == beginHour && timeDTO1.getBeginMinute() == beginMinute && timeDTO1.getEndHour() == endHour
//                    && timeDTO1.getEndMinute() == endMinute) {
//                Integer remainTimes;
//                // Single
//                if (globalRemained != null) {
//                    log.info("timeDTO1.getTimes(): {}", timeDTO1.getTimes());
//                    remainTimes = timeDTO1.getTimes() < globalRemained ? timeDTO1.getTimes() : globalRemained;
//                } else {
//                    //Team
//                    remainTimes = timeDTO1.getTimes() - times;
//                }
//                if (remainTimes <= 0) {
//                    continue;
//                }
//                timeDTO1.setTimes(remainTimes);
//            }
//            result.add(timeDTO1);
//        }
//        return result;
//    }

    @Override
    public String getFormatTimeString(Integer beginHour, Integer beginMinute, Integer endHour, Integer endMinute) {
        return String.format(TIME_STRING_FORMAT,
                formatTimeNumber(beginHour),
                formatTimeNumber(beginMinute),
                formatTimeNumber(endHour),
                formatTimeNumber(endMinute));
    }

    private String formatTimeNumber(Integer timeNumber) {
        if (timeNumber == null) {
            return "00";
        } else if (timeNumber < 10) {
            return "0" + String.valueOf(timeNumber);
        } else {
            return String.valueOf(timeNumber);
        }
    }

    @Scheduled(initialDelay = 1L, fixedDelay = 1 * 24 * 60 * 60 * 1000L)
    public void buildTimeResource() {
        TimeResourceDto timeResourceDto = buildTimeResourceDto();
        List<TimeResource> allTimeResource = timeResourceRepository.findAll();
        if (CollectionUtils.isEmpty(allTimeResource)) {
            addTimeResource(
                    timeResourceDto.getValidDateMapWeekForSINGLE(),
                    timeResourceDto.getValidDateMapWeekForTEAM(),
                    timeResourceDto.getValidDateMapDayForSINGLE(),
                    timeResourceDto.getValidDateMapDayForTEAM());
        } else {
            LocalDate validStartDateForWeekSingle = timeResourceDto.getValidDateMapWeekForSINGLE().keySet().stream().sorted().findFirst().orElse(null);
            LocalDate validStartDateForWeekTeam = timeResourceDto.getValidDateMapWeekForTEAM().keySet().stream().sorted().findFirst().orElse(null);

            TimeResource teamTimeResource = timeResourceRepository.findFirstByMetaTypeAndActiveIsTrueOrderByReservableDateDesc(MetaType.TEAM);
            TimeResource singleTimeResource = timeResourceRepository.findFirstByMetaTypeAndActiveIsTrueOrderByReservableDateDesc(MetaType.SINGLE);
            LocalDate latestTeamReservableDate = teamTimeResource != null ?
                    LocalDateTime.ofInstant(teamTimeResource.getReservableDate().toInstant(), ZoneId.systemDefault()).toLocalDate() : null;
            LocalDate latestSingleReservableDate = singleTimeResource != null ?
                    LocalDateTime.ofInstant(singleTimeResource.getReservableDate().toInstant(), ZoneId.systemDefault()).toLocalDate() : null;
            Map<LocalDate, List<TimeResourceDto.TimeDTO>> validDateMapWeekForSINGLE = timeResourceDto.getValidDateMapWeekForSINGLE();
            Map<LocalDate, List<TimeResourceDto.TimeDTO>> validDateMapWeekForTEAM = timeResourceDto.getValidDateMapWeekForTEAM();
            Map<LocalDate, List<TimeResourceDto.TimeDTO>> validDateMapDayForSINGLE = timeResourceDto.getValidDateMapDayForSINGLE();
            Map<LocalDate, List<TimeResourceDto.TimeDTO>> validDateMapDayForTEAM = timeResourceDto.getValidDateMapDayForTEAM();
            validDateMapWeekForSINGLE = filterAfterDate(validDateMapWeekForSINGLE, latestSingleReservableDate);
            validDateMapWeekForTEAM = filterAfterDate(validDateMapWeekForTEAM, latestTeamReservableDate);
            validDateMapDayForSINGLE = filterAfterDate(validDateMapDayForSINGLE, latestSingleReservableDate);
            validDateMapDayForTEAM = filterAfterDate(validDateMapDayForTEAM, latestTeamReservableDate);
            addTimeResource(validDateMapWeekForSINGLE, validDateMapWeekForTEAM, validDateMapDayForSINGLE, validDateMapDayForTEAM);
            removeOverTimeResource(validStartDateForWeekSingle, validStartDateForWeekTeam);
        }
    }

    private void removeOverTimeResource(LocalDate validStartDateForWeekSingle, LocalDate validStartDateForWeekTeam) {
        removeOverTimeResource(validStartDateForWeekSingle, MetaType.SINGLE);
        removeOverTimeResource(validStartDateForWeekTeam, MetaType.TEAM);
    }

    private void removeOverTimeResource(LocalDate localDate, MetaType metaType) {
        if (localDate != null) {
            List<TimeResource> timeResourceList = timeResourceRepository.findByMetaTypeAndReservableDateBeforeAndActiveIsTrue(metaType, Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            timeResourceList.forEach(timeResource -> timeResource.setActive(false));
            timeResourceRepository.save(timeResourceList);
        }
    }

    private Map<LocalDate, List<TimeResourceDto.TimeDTO>> filterAfterDate(Map<LocalDate, List<TimeResourceDto.TimeDTO>> map, LocalDate localDate) {
        if (localDate != null) {
            return map.entrySet().stream().filter(e -> e.getKey().isAfter(localDate)).collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
        }
        return map;
    }

    private void addTimeResource(Map<LocalDate, List<TimeResourceDto.TimeDTO>> validDateMapWeekForSINGLE,
                                 Map<LocalDate, List<TimeResourceDto.TimeDTO>> validDateMapWeekForTEAM,
                                 Map<LocalDate, List<TimeResourceDto.TimeDTO>> validDateMapDayForSINGLE,
                                 Map<LocalDate, List<TimeResourceDto.TimeDTO>> validDateMapDayForTEAM) {
        List<TimeResource> readyToSave = new ArrayList<>();
        readyToSave.addAll(addTimeResource(validDateMapWeekForSINGLE, MetaType.SINGLE));
        readyToSave.addAll(addTimeResource(validDateMapWeekForTEAM, MetaType.TEAM));
        readyToSave.addAll(addTimeResource(validDateMapDayForSINGLE, MetaType.SINGLE));
        readyToSave.addAll(addTimeResource(validDateMapDayForTEAM, MetaType.TEAM));
        timeResourceRepository.save(readyToSave);
    }

    private List<TimeResource> addTimeResource(Map<LocalDate, List<TimeResourceDto.TimeDTO>> validDateMap, MetaType metaType) {
        List<TimeResource> readyToSave = new ArrayList<>();
        for (Map.Entry<LocalDate, List<TimeResourceDto.TimeDTO>> entry : validDateMap.entrySet()) {
            readyToSave.addAll(buildTimeResourceDB(entry.getKey(), entry.getValue(), metaType));
        }
        return readyToSave;
    }

    private List<TimeResource> buildTimeResourceDB(LocalDate localDate, List<TimeResourceDto.TimeDTO> timeDTOList, MetaType metaType) {
        List<TimeResource> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(timeDTOList) || metaType == null || localDate == null) {
            log.warn("parameter is not correct.");
            return result;
        }
        Date reservableDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        for (TimeResourceDto.TimeDTO timeDTO : timeDTOList) {
            TimeResource timeResource = new TimeResource();
            timeResource.setMetaType(metaType);
            timeResource.setReservableDate(reservableDate);
            timeResource.setHourBegin(timeDTO.getBeginHour());
            timeResource.setMinuteBegin(timeDTO.getBeginMinute());
            timeResource.setHourEnd(timeDTO.getEndHour());
            timeResource.setMinuteEnd(timeDTO.getEndMinute());
            timeResource.setRemainTimes(timeDTO.getTimes());
            timeResource.setActive(true);
            result.add(timeResource);
        }
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRemainedTimes(Date reserveDate, Integer reserveBeginHH, Integer reserveBeginMM, Integer reserveEndHH, Integer reserveEndMM, ActivityType activityType, Integer peopleCount) throws Exception{
        Integer optionTimes = 0;
        if(activityType.isTeam()){
            optionTimes = peopleCount > 0 ? 0 : -1;
        }else{
            optionTimes = peopleCount;
        }
        TimeResource timeResource = timeResourceRepository.findByMetaTypeAndReservableDateAndHourBeginAndMinuteBeginAndAndHourEndAndMinuteEndAndRemainTimesGreaterThanAndActiveIsTrue(MetaType.valueOf(activityType.name()), reserveDate, reserveBeginHH, reserveBeginMM, reserveEndHH, reserveEndMM, optionTimes);
        if(timeResource == null){
            log.error("the time resource does not exist. {}, {}, {}, {}, {}, {}", activityType.name(), reserveDate, reserveBeginHH, reserveBeginMM, reserveEndHH, reserveEndMM);
            throw new TimeResourceNotExistException("the time resource does not exist.");
        }
        Integer remainTimes = timeResource.getRemainTimes();
        if(activityType.isTeam()) {
            List<TimeResource> dayTimeResources = timeResourceRepository.findByMetaTypeAndReservableDateAndRemainTimesGreaterThanAndActiveIsTrue(MetaType.valueOf(activityType.name()), reserveDate, optionTimes);
            dayTimeResources.forEach(timeResource1 -> timeResource1.setRemainTimes(timeResource1.getRemainTimes() - (peopleCount < 0 ? -1 : 1)));
            timeResourceRepository.save(dayTimeResources);
        }else{
            if(remainTimes < peopleCount){
                log.error("the time resource not enough. {}, {}, {}, {}, {}, {}, remained {}, request: {}", activityType.name(), reserveDate, reserveBeginHH, reserveBeginMM, reserveEndHH, reserveEndMM, remainTimes, peopleCount);
                throw new TimeResourceNotEnoughException("the time resource not enough.");
            }
            LocalDate localDate = LocalDateTime.ofInstant(reserveDate.toInstant(), ZoneId.systemDefault()).toLocalDate();
            int dayOfWeek = localDate.getDayOfWeek().getValue();
            LocalDateTime firstDayOfWeek = localDate.minusDays(dayOfWeek - 1).atTime(0, 0,0);
            LocalDateTime endDayOfWeek = localDate.plusDays(7 - dayOfWeek).atTime(23, 59, 59);
            List<TimeResource> timeResourceList = timeResourceRepository.findByMetaTypeAndReservableDateBetweenAndActiveIsTrue(
                    MetaType.valueOf(activityType.name()),
                    Date.from(firstDayOfWeek.atZone(ZoneId.systemDefault()).toInstant()),
                    Date.from(endDayOfWeek.atZone(ZoneId.systemDefault()).toInstant()));
            List<ReservationInfo> hasUsed = reservationInfoRepository.findByActivityTypeAndReserveDateBetweenAndDeletedFalse(
                    activityType,
                    Date.from(firstDayOfWeek.atZone(ZoneId.systemDefault()).toInstant()),
                    Date.from(endDayOfWeek.atZone(ZoneId.systemDefault()).toInstant()));
            final Integer weekRemained = (CollectionUtils.isEmpty(hasUsed) ? Integer.valueOf(weekSingleMax) : (Integer.valueOf(weekSingleMax) - hasUsed.stream().collect(Collectors.summingInt(reservationInfo -> reservationInfo.getPeopleCount())))) - peopleCount;

            if(weekRemained < 0){
                log.warn("the week time resource not enough. {}, {}, {}, {}, {}, {}, weekRemained {}, request: {}", activityType.name(), reserveDate, reserveBeginHH, reserveBeginMM, reserveEndHH, reserveEndMM, weekRemained + peopleCount, peopleCount);
                throw new TimeResourceNotEnoughException("the week time resource not enough.");
            }

            timeResourceList.forEach(timeResource1 -> {
                if(weekRemained < peopleNumberThreshold){
                    timeResource1.setRemainTimes(weekRemained);
                }
                if(weekRemained > peopleNumberThreshold){
                    timeResource1.setRemainTimes(peopleNumberThreshold);
                }
            });
            timeResourceRepository.save(timeResourceList);
        }
    }
}
