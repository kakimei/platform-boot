package com.platform.reserve.service;

import com.platform.reserve.repository.ReservationInfoRepository;
import com.platform.reserve.repository.entity.ReservationInfo;
import com.platform.reserve.service.dto.ReservationInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
public class ReservationInfoServiceImpl implements ReservationInfoService{

	@Autowired
	private ReservationInfoRepository reservationInfoRepository;

	@Autowired
	private ReserveDtoTransferBuilder reserveDtoTransferBuilder;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(ReservationInfoDto reservationInfoDto) {
		if(reservationInfoDto.getReservationInfoId() != null){
			ReservationInfo reservationInfo = reservationInfoRepository.findByReservationInfoIdAndDeleteFalse(
				reservationInfoDto.getReservationInfoId());
			if(reservationInfo != null){
				BeanUtils.copyProperties(reservationInfoDto, reservationInfo, "reservationInfoId");
				reservationInfoRepository.save(reservationInfo);
				return;
			}
			log.warn("this reservation has been deleted. reservation id : {}", reservationInfoDto.getReservationInfoId());
			return;
		}
		reservationInfoRepository.save(reserveDtoTransferBuilder.toEntity(reservationInfoDto));
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public ReservationInfoDto findReservationInfoById(Long reservationInfoId) {
		if(reservationInfoId == null){
			log.warn("reservationInfoId is null.");
			return null;
		}
		ReservationInfo reservationInfo = reservationInfoRepository.findByReservationInfoIdAndDeleteFalse(reservationInfoId);
		if(reservationInfo == null){
			log.warn("this reservationInfoId does not exist. reservationInfoId : {}", reservationInfoId);
			return null;
		}
		return reserveDtoTransferBuilder.toDto(reservationInfo);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public List<ReservationInfoDto> findReservationInfoByLinkman(String linkmanName) {
		List<ReservationInfoDto> result = new ArrayList<>();
		if(StringUtils.isBlank(linkmanName)){
			log.warn("linkmanName is null.");
			return result;
		}
		List<ReservationInfo> reservationInfoList = reservationInfoRepository.findByLinkManNameAndDeleteFalse(linkmanName);
		if(CollectionUtils.isEmpty(reservationInfoList)){
			return result;
		}
		reservationInfoList.forEach(reservationInfo -> result.add(reserveDtoTransferBuilder.toDto(reservationInfo)));
		return result;
	}
}
