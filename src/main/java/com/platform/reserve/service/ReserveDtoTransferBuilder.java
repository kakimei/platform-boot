package com.platform.reserve.service;

import com.platform.reserve.controller.vo.ReserveVO;
import com.platform.reserve.repository.entity.ActivityType;
import com.platform.reserve.repository.entity.ReservationInfo;
import com.platform.reserve.repository.entity.Sex;
import com.platform.reserve.service.dto.ReservationInfoDto;
import com.platform.resource.service.TimeResourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReserveDtoTransferBuilder {

	@Autowired
	private TimeResourceService timeResourceService;

	public ReservationInfoDto toDto(ReserveVO reserveVO) {
		if (reserveVO == null) {
			log.warn("reserveVO is null");
			return null;
		}
		ReservationInfoDto reservationInfoDto = new ReservationInfoDto();
		BeanUtils.copyProperties(reserveVO, reservationInfoDto);
		reservationInfoDto.setActivityType(toActivityType(reserveVO.getActivityType()));
		reservationInfoDto.setSex(toSex(reserveVO.getSex()));
		reservationInfoDto.setReserveBeginHH(timeResourceService.getBeginHour(reserveVO.getTimeString()));
		reservationInfoDto.setReserveBeginMM(timeResourceService.getBeginMinute(reserveVO.getTimeString()));
		reservationInfoDto.setReserveEndHH(timeResourceService.getEndHour(reserveVO.getTimeString()));
		reservationInfoDto.setReserveEndMM(timeResourceService.getEndMinute(reserveVO.getTimeString()));

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
			buildTimeString(
				reservationInfoDto.getReserveBeginHH(),
				reservationInfoDto.getReserveBeginMM(),
				reservationInfoDto.getReserveEndHH(),
				reservationInfoDto.getReserveEndMM()));
		reserveVO.setReserveDay(reservationInfoDto.getReserveDate());
		return reserveVO;
	}

	public String buildTimeString(Integer beginHour, Integer beginMinute, Integer endHour, Integer endMinute) {
		StringBuffer result = new StringBuffer();
		if (String.valueOf(beginHour).length() == 1) {
			result.append("0").append(beginHour);
		} else {
			result.append(beginHour);
		}
		if (String.valueOf(beginMinute).length() == 1) {
			result.append(":0").append(beginMinute);
		} else {
			result.append(":").append(beginMinute);
		}
		if (String.valueOf(endHour).length() == 1) {
			result.append(" ~ 0").append(endHour);
		} else {
			result.append(" ~ ").append(endHour);
		}
		if (String.valueOf(endMinute).length() == 1) {
			result.append(":0").append(endMinute);
		} else {
			result.append(":").append(endMinute);
		}
		return result.toString();
	}

	public String buildFormatString(String timeString) {
		return buildTimeString(
			timeResourceService.getBeginHour(timeString),
			timeResourceService.getBeginMinute(timeString),
			timeResourceService.getEndHour(timeString),
			timeResourceService.getEndMinute(timeString));
	}

}
