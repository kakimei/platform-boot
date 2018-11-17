package com.platform.reserve.service;

import com.platform.reserve.controller.vo.ReserveVO;
import com.platform.reserve.repository.entity.ReservationInfo;
import com.platform.reserve.service.dto.ReservationInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReserveDtoTransferBuilder {

	public ReservationInfoDto toDto(ReserveVO reserveVO){
		if(reserveVO == null){
			log.warn("reserveVO is null");
			return null;
		}
		ReservationInfoDto reservationInfoDto = new ReservationInfoDto();
		BeanUtils.copyProperties(reserveVO, reservationInfoDto);
		return reservationInfoDto;
	}

	public ReservationInfoDto toDto(ReservationInfo reservationInfo){
		if(reservationInfo == null){
			log.warn("reservationInfo is null");
			return null;
		}
		ReservationInfoDto reservationInfoDto = new ReservationInfoDto();
		BeanUtils.copyProperties(reservationInfo, reservationInfoDto);
		return reservationInfoDto;
	}

	public ReservationInfo toEntity(ReservationInfoDto reservationInfoDto){
		if(reservationInfoDto == null){
			log.warn("reservationInfoDto is null");
			return null;
		}
		ReservationInfo reservationInfo = new ReservationInfo();
		BeanUtils.copyProperties(reservationInfoDto, reservationInfo, "reservationInfoId");
		return reservationInfo;
	}
}
