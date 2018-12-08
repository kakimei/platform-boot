package com.platform.reserve.service;

import com.platform.reserve.controller.vo.ReserveVO;
import com.platform.reserve.repository.entity.ActivityType;
import com.platform.reserve.repository.entity.ReservationInfo;
import com.platform.reserve.repository.entity.Sex;
import com.platform.reserve.service.dto.ReservationInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class ReserveDtoTransferBuilder {

	private static final String TIME_FORMAT = "^(\\d+):(\\d+) ~ (\\d+):(\\d+)";

	public static final Pattern TIME_PATTERN = Pattern.compile(TIME_FORMAT);

	public ReservationInfoDto toDto(ReserveVO reserveVO) {
		if (reserveVO == null) {
			log.warn("reserveVO is null");
			return null;
		}
		ReservationInfoDto reservationInfoDto = new ReservationInfoDto();
		BeanUtils.copyProperties(reserveVO, reservationInfoDto);
		reservationInfoDto.setActivityType(toActivityType(reserveVO.getActivityType()));
		reservationInfoDto.setSex(toSex(reserveVO.getSex()));
		Matcher m = TIME_PATTERN.matcher(reserveVO.getTimeString());
		if (m.find( )) {
			reservationInfoDto.setReserveBeginHH(Integer.valueOf(m.group(1)));
			reservationInfoDto.setReserveBeginMM(Integer.valueOf(m.group(2)));
			reservationInfoDto.setReserveEndHH(Integer.valueOf(m.group(3)));
			reservationInfoDto.setReserveEndMM(Integer.valueOf(m.group(4)));
		}
		reservationInfoDto.setReserveDate(reserveVO.getReserveDay());
		return reservationInfoDto;
	}

	public ReservationInfoDto toDto(ReservationInfo reservationInfo) {
		if (reservationInfo == null) {
			log.warn("reservationInfo is null");
			return null;
		}
		ReservationInfoDto reservationInfoDto = new ReservationInfoDto();
		BeanUtils.copyProperties(reservationInfo, reservationInfoDto);
		return reservationInfoDto;
	}

	public ReservationInfo toEntity(ReservationInfoDto reservationInfoDto) {
		if (reservationInfoDto == null) {
			log.warn("reservationInfoDto is null");
			return null;
		}
		ReservationInfo reservationInfo = new ReservationInfo();
		BeanUtils.copyProperties(reservationInfoDto, reservationInfo, "reservationInfoId", "signIn");
		return reservationInfo;
	}

	public ActivityType toActivityType(com.platform.reserve.controller.vo.ActivityType activityType) {
		return ActivityType.valueOf(activityType.name());
	}

	public com.platform.reserve.controller.vo.ActivityType toActivityType(ActivityType activityType) {
		return com.platform.reserve.controller.vo.ActivityType.valueOf(activityType.name());
	}

	public Sex toSex(com.platform.reserve.controller.vo.Sex sex) {
		return Sex.valueOf(sex.name());
	}

	public com.platform.reserve.controller.vo.Sex toSex(Sex sex) {
		return com.platform.reserve.controller.vo.Sex.valueOf(sex.name());
	}

	public ReserveVO toVO(ReservationInfoDto reservationInfoDto) {
		if (reservationInfoDto == null) {
			log.warn("reservationInfoDto is null");
			return null;
		}
		ReserveVO reserveVO = new ReserveVO();
		BeanUtils.copyProperties(reservationInfoDto, reserveVO);
		reserveVO.setActivityType(toActivityType(reservationInfoDto.getActivityType()));
		reserveVO.setSex(toSex(reservationInfoDto.getSex()));
		reserveVO.setTimeString(
			reservationInfoDto.getReserveBeginHH() + ":" + reservationInfoDto.getReserveBeginMM() + " ~ " + reservationInfoDto.getReserveEndHH()
				+ ":" + reservationInfoDto.getReserveEndMM());
		reserveVO.setReserveDay(reservationInfoDto.getReserveDate());
		return reserveVO;
	}

	public Integer getBeginHour(String timeString){
		Matcher m = TIME_PATTERN.matcher(timeString);
		if (m.find( )) {
			return Integer.valueOf(m.group(1));
		}
		return 0;
	}

	public Integer getBeginMinute(String timeString){
		Matcher m = TIME_PATTERN.matcher(timeString);
		if (m.find( )) {
			return Integer.valueOf(m.group(2));
		}
		return 0;
	}

	public Integer getEndHour(String timeString){
		Matcher m = TIME_PATTERN.matcher(timeString);
		if (m.find( )) {
			return Integer.valueOf(m.group(3));
		}
		return 0;
	}

	public Integer getEndMinute(String timeString){
		Matcher m = TIME_PATTERN.matcher(timeString);
		if (m.find( )) {
			return Integer.valueOf(m.group(4));
		}
		return 0;
	}
}
