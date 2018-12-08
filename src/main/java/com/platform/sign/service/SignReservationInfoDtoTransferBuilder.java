package com.platform.sign.service;

import com.platform.sign.controller.vo.SignReservationInfoVO;
import com.platform.sign.repository.entity.SignReservationInfo;
import com.platform.sign.service.dto.SignReservationInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SignReservationInfoDtoTransferBuilder {

	public SignReservationInfo toEntity(SignReservationInfoDTO signReservationInfoDTO) {
		if (signReservationInfoDTO == null) {
			log.warn("signReservationInfoDTO is null");
			return null;
		}
		SignReservationInfo signReservationInfo = new SignReservationInfo();
		BeanUtils.copyProperties(signReservationInfoDTO, signReservationInfo);
		return signReservationInfo;
	}

	public SignReservationInfoDTO toDTO(SignReservationInfoVO signReservationInfoVO){
		if (signReservationInfoVO == null) {
			log.warn("signReservationInfoVO is null");
			return null;
		}
		SignReservationInfoDTO signReservationInfoDTO = new SignReservationInfoDTO();
		BeanUtils.copyProperties(signReservationInfoVO, signReservationInfoDTO);
		return signReservationInfoDTO;
	}
}
