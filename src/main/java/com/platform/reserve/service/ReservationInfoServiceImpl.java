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
			ReservationInfo reservationInfo = reservationInfoRepository.findByReservationInfoIdAndDeletedFalse(
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
	public ReservationInfoDto findReservationInfoById(String user, Long reservationInfoId) {
		if(reservationInfoId == null || StringUtils.isBlank(user)){
			log.warn("reservationInfoId is null or user is null.");
			return null;
		}
		ReservationInfo reservationInfo = reservationInfoRepository.findByReservationInfoIdAndUserNameAndDeletedFalse(reservationInfoId, user);
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
		List<ReservationInfo> reservationInfoList = reservationInfoRepository.findByLinkManNameAndDeletedFalse(linkmanName);
		if(CollectionUtils.isEmpty(reservationInfoList)){
			return result;
		}
		reservationInfoList.forEach(reservationInfo -> result.add(reserveDtoTransferBuilder.toDto(reservationInfo)));
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public List<ReservationInfoDto> findReservationInfoByUser(String userName) {
		List<ReservationInfoDto> result = new ArrayList<>();
		if(StringUtils.isBlank(userName)){
			log.warn("userName is null.");
			return result;
		}
		List<ReservationInfo> reservationInfoList = reservationInfoRepository.findByUserNameOrderByReservationInfoId(userName);
		if(CollectionUtils.isEmpty(reservationInfoList)){
			return result;
		}
		reservationInfoList.forEach(reservationInfo -> result.add(reserveDtoTransferBuilder.toDto(reservationInfo)));
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public List<ReservationInfoDto> findAllActiveReservationInfo(){
		List<ReservationInfoDto> result = new ArrayList<>();
		List<ReservationInfo> reservationInfoList = reservationInfoRepository.findByDeletedFalse();
		if(CollectionUtils.isEmpty(reservationInfoList)){
			return result;
		}
		reservationInfoList.forEach(reservationInfo -> result.add(reserveDtoTransferBuilder.toDto(reservationInfo)));
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public ReservationInfoDto singIn(String user, Long reservationInfoId) {
		if(StringUtils.isBlank(user) || reservationInfoId == null || reservationInfoId == 0L){
			log.warn("user or reservationInfoId is null.");
			return null;
		}
		ReservationInfo reservationInfo = reservationInfoRepository.findByReservationInfoIdAndUserNameAndDeletedFalse(
			reservationInfoId, user);
		if(reservationInfo == null){
			log.warn("the reservation does not exist. user : {}, reservationInfoId : {}", user, reservationInfoId);
			return null;
		}
		reservationInfo.setSignIn(true);
		ReservationInfo saved = reservationInfoRepository.save(reservationInfo);
		return reserveDtoTransferBuilder.toDto(saved);
	}
}
