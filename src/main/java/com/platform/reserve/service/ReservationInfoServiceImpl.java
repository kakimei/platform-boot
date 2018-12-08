package com.platform.reserve.service;

import com.platform.feedback.repository.FeedBackRepository;
import com.platform.feedback.repository.entity.FeedBack;
import com.platform.feedback.service.FeedBackService;
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
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
public class ReservationInfoServiceImpl implements ReservationInfoService {

	@Autowired
	private ReservationInfoRepository reservationInfoRepository;

	@Autowired
	private ReserveDtoTransferBuilder reserveDtoTransferBuilder;

	@Autowired
	private FeedBackRepository feedBackRepository;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(ReservationInfoDto reservationInfoDto) {
		if (reservationInfoDto.getReservationInfoId() != null) {
			ReservationInfo reservationInfo = reservationInfoRepository.findByReservationInfoIdAndDeletedFalse(
				reservationInfoDto.getReservationInfoId());
			if (reservationInfo != null) {
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
		if (reservationInfoId == null || StringUtils.isBlank(user)) {
			log.warn("reservationInfoId is null or user is null.");
			return null;
		}
		ReservationInfo reservationInfo = reservationInfoRepository.findByReservationInfoIdAndUserNameAndDeletedFalse(reservationInfoId, user);
		if (reservationInfo == null) {
			log.warn("this reservationInfoId does not exist. reservationInfoId : {}", reservationInfoId);
			return null;
		}
		return reserveDtoTransferBuilder.toDto(reservationInfo);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public List<ReservationInfoDto> findReservationInfoByLinkman(String linkmanName) {
		List<ReservationInfoDto> result = new ArrayList<>();
		if (StringUtils.isBlank(linkmanName)) {
			log.warn("linkmanName is null.");
			return result;
		}
		List<ReservationInfo> reservationInfoList = reservationInfoRepository.findByLinkManNameAndDeletedFalse(linkmanName);
		if (CollectionUtils.isEmpty(reservationInfoList)) {
			return result;
		}
		reservationInfoList.forEach(reservationInfo -> result.add(reserveDtoTransferBuilder.toDto(reservationInfo)));
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public List<ReservationInfoDto> findReservationInfoByUser(String userName) {
		List<ReservationInfoDto> result = new ArrayList<>();
		if (StringUtils.isBlank(userName)) {
			log.warn("userName is null.");
			return result;
		}
		List<ReservationInfo> reservationInfoList = reservationInfoRepository.findByUserNameAndDeletedFalseOrderByReservationInfoId(userName);
		if (CollectionUtils.isEmpty(reservationInfoList)) {
			return result;
		}
		reservationInfoList.forEach(reservationInfo -> result.add(reserveDtoTransferBuilder.toDto(reservationInfo)));
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public List<ReservationInfoDto> findAllActiveReservationInfo() {
		List<ReservationInfoDto> result = new ArrayList<>();
		List<ReservationInfo> reservationInfoList = reservationInfoRepository.findByDeletedFalse();
		if (CollectionUtils.isEmpty(reservationInfoList)) {
			return result;
		}
		reservationInfoList.forEach(reservationInfo -> result.add(reserveDtoTransferBuilder.toDto(reservationInfo)));
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public ReservationInfoDto cancel(String user, Long reservationInfoId) {
		if (StringUtils.isBlank(user) || reservationInfoId == null || reservationInfoId == 0L) {
			log.warn("user or reservationInfoId is null.");
			return null;
		}
		ReservationInfo reservationInfo = reservationInfoRepository.findByReservationInfoIdAndUserNameAndDeletedFalse(
			reservationInfoId, user);
		if (reservationInfo == null) {
			log.warn("the reservation does not exist. user : {}, reservationInfoId : {}", user, reservationInfoId);
			return null;
		}
		reservationInfo.setDeleted(true);
		ReservationInfo saved = reservationInfoRepository.save(reservationInfo);
		return reserveDtoTransferBuilder.toDto(saved);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public List<ReservationInfoDto> findReservationInfoByDateAndTime(Date reserveDate, String timeString) {
		List<ReservationInfoDto> result = new ArrayList<>();
		List<ReservationInfo> reservationInfoList = reservationInfoRepository.findByReserveDateAndReserveBeginHHAndReserveBeginMMAndReserveEndHHAndReserveEndMMAndDeletedFalse(
			reserveDate,
			reserveDtoTransferBuilder.getBeginHour(timeString),
			reserveDtoTransferBuilder.getBeginMinute(timeString),
			reserveDtoTransferBuilder.getEndHour(timeString),
			reserveDtoTransferBuilder.getEndMinute(timeString));
		if(CollectionUtils.isEmpty(reservationInfoList)){
			return result;
		}
		reservationInfoList.forEach(reservationInfo -> result.add(reserveDtoTransferBuilder.toDto(reservationInfo)));
		return result;
	}

	@Override
	public List<ReservationInfoDto> findReservationInfoAndFeedbackByUserNameAndId(String userName, List<Long> reservationInfoIdList) {
		List<ReservationInfoDto> result = new ArrayList<>();
		if(CollectionUtils.isEmpty(reservationInfoIdList)){
			return result;
		}
		List<ReservationInfo> reservationInfoList = reservationInfoRepository.findByReservationInfoIdInAndDeletedFalse(
			reservationInfoIdList);
		if(CollectionUtils.isEmpty(reservationInfoList)){
			return result;
		}
		reservationInfoList.forEach(reservationInfo -> {
			ReservationInfoDto reservationInfoDto = reserveDtoTransferBuilder.toDto(reservationInfo);
			List<FeedBack> feedBackList = feedBackRepository.findByUserNameAndReservationInfoId(userName,
				reservationInfo.getReservationInfoId());
			reservationInfoDto.setHasFeedback(!CollectionUtils.isEmpty(feedBackList));
			result.add(reservationInfoDto);
		});
		return result;
	}
}
