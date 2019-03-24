package com.platform.reserve.service;

import com.platform.feedback.repository.FeedBackRepository;
import com.platform.feedback.repository.entity.FeedBack;
import com.platform.reserve.repository.ReservationInfoRepository;
import com.platform.reserve.repository.entity.ActivityType;
import com.platform.reserve.repository.entity.ReservationInfo;
import com.platform.reserve.service.dto.ReservationInfoDto;
import com.platform.resource.service.TimeResourceService;
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
import java.util.stream.Collectors;

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

	@Autowired
	private TimeResourceService timeResourceService;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Long save(ReservationInfoDto reservationInfoDto) throws Exception{
		if (reservationInfoDto.getReservationInfoId() != null) {
			update(reservationInfoDto);
			return reservationInfoDto.getReservationInfoId();
		}

		try {
			timeResourceService.updateRemainedTimes(
					reservationInfoDto.getReserveDate(),
					reservationInfoDto.getReserveBeginHH(),
					reservationInfoDto.getReserveBeginMM(),
					reservationInfoDto.getReserveEndHH(),
					reservationInfoDto.getReserveEndMM(),
					reservationInfoDto.getActivityType(),
					reservationInfoDto.getPeopleCount());
			ReservationInfo saved = reservationInfoRepository.save(reserveDtoTransferBuilder.toEntity(reservationInfoDto));
			return saved.getReservationInfoId();
		}catch (Exception e) {
			log.error("save failed. cause{}", e.getMessage());
			throw e;
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void update(ReservationInfoDto reservationInfoDto) {
		if (reservationInfoDto.getReservationInfoId() == null) {
			log.warn("parameter is not correct.");
			return;
		}
		ReservationInfo reservationInfo = reservationInfoRepository.findByReservationInfoIdAndDeletedFalse(
			reservationInfoDto.getReservationInfoId());
		if (reservationInfo == null) {
			log.warn("the reservation does not exist.");
		}
		BeanUtils.copyProperties(reservationInfoDto, reservationInfo, "reservationInfoId", "userName");
		reservationInfoRepository.save(reservationInfo);
		return;
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public ReservationInfoDto findReservationInfoByIdAndUser(String user, Long reservationInfoId) {
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
	public ReservationInfoDto findReservationInfoById(Long reservationInfoId) {
		if (reservationInfoId == null) {
			log.warn("reservationInfoId is null.");
			return null;
		}
		ReservationInfo reservationInfo = reservationInfoRepository.findByReservationInfoIdAndDeletedFalse(reservationInfoId);
		if (reservationInfo == null) {
			log.warn("this reservationInfoId does not exist. reservationInfoId : {}", reservationInfoId);
			return null;
		}
		return reserveDtoTransferBuilder.toDto(reservationInfo);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public List<ReservationInfoDto> findReservationInfoByLinkman(String userName, String linkmanName) {
		List<ReservationInfoDto> result = new ArrayList<>();
		if (StringUtils.isBlank(linkmanName) || StringUtils.isBlank(userName)) {
			log.warn("linkmanName is null.");
			return result;
		}
		List<ReservationInfo> reservationInfoList = reservationInfoRepository.findByUserNameAndLinkManNameAndDeletedFalse(userName, linkmanName);
		if (CollectionUtils.isEmpty(reservationInfoList)) {
			return result;
		}
		reservationInfoList.forEach(reservationInfo -> result.add(reserveDtoTransferBuilder.toDto(reservationInfo)));
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public List<ReservationInfoDto> findReservationInfoByPhoneNumber(String userName, String phoneNumber) {
		List<ReservationInfoDto> result = new ArrayList<>();
		if (StringUtils.isBlank(phoneNumber) || StringUtils.isBlank(userName)) {
			log.warn("phoneNumber is null.");
			return result;
		}
		List<ReservationInfo> reservationInfoList = reservationInfoRepository.findByUserNameAndPhoneNumberAndDeletedFalse(userName, phoneNumber);
		if (CollectionUtils.isEmpty(reservationInfoList)) {
			return result;
		}
		reservationInfoList.forEach(reservationInfo -> result.add(reserveDtoTransferBuilder.toDto(reservationInfo)));
		return result;
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public List<ReservationInfoDto> findReservationInfoByActivityType(String userName, String activityType) {
		List<ReservationInfoDto> result = new ArrayList<>();
		if (StringUtils.isBlank(activityType)) {
			log.warn("activityType is null.");
			return result;
		}
		List<ReservationInfo> reservationInfoList = reservationInfoRepository.findByActivityTypeAndDeletedFalseOrderByReservationInfoIdDesc(ActivityType.valueOf(activityType));
		if (CollectionUtils.isEmpty(reservationInfoList)) {
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

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public List<ReservationInfoDto> findReservationInfoByUser(String userName) {
		List<ReservationInfoDto> result = new ArrayList<>();
		if (StringUtils.isBlank(userName)) {
			log.warn("userName is null.");
			return result;
		}
		List<ReservationInfo> reservationInfoList = reservationInfoRepository.findByUserNameAndDeletedFalseOrderByReservationInfoIdDesc(userName);
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
	public List<ReservationInfoDto> findAllActiveTeamReservationInfo() {
		List<ReservationInfoDto> result = new ArrayList<>();
		List<ReservationInfo> reservationInfoList = reservationInfoRepository.findByActivityTypeAndDeletedFalse(ActivityType.TEAM);
		if (CollectionUtils.isEmpty(reservationInfoList)) {
			return result;
		}
		reservationInfoList.forEach(reservationInfo -> result.add(reserveDtoTransferBuilder.toDto(reservationInfo)));
		return result;
	}

	@Override
	public List<ReservationInfoDto> findAllActiveSingleReservationInfo() {
		List<ReservationInfoDto> result = new ArrayList<>();
		List<ReservationInfo> reservationInfoList = reservationInfoRepository.findByActivityTypeAndDeletedFalse(ActivityType.SINGLE);
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
	public ReservationInfoDto cancel(Long reservationInfoId) {
		if (reservationInfoId == null || reservationInfoId == 0L) {
			log.warn("reservationInfoId is null.");
			return null;
		}
		ReservationInfo reservationInfo = reservationInfoRepository.findByReservationInfoIdAndDeletedFalse(
			reservationInfoId);
		if (reservationInfo == null) {
			log.warn("the reservation does not exist. reservationInfoId : {}", reservationInfoId);
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
			timeResourceService.getBeginHour(timeString),
			timeResourceService.getBeginMinute(timeString),
			timeResourceService.getEndHour(timeString),
			timeResourceService.getEndMinute(timeString));
		if (CollectionUtils.isEmpty(reservationInfoList)) {
			return result;
		}
		reservationInfoList.forEach(reservationInfo -> result.add(reserveDtoTransferBuilder.toDto(reservationInfo)));
		return result;
	}

//	@Override
//	public List<ReservationInfoDto> findReservationInfoAndFeedbackByUserNameAndId(String userName, List<Long> reservationInfoIdList) {
//		List<ReservationInfoDto> result = new ArrayList<>();
//		if (CollectionUtils.isEmpty(reservationInfoIdList)) {
//			return result;
//		}
//		List<ReservationInfo> reservationInfoList = reservationInfoRepository.findByReservationInfoIdInAndDeletedFalse(
//			reservationInfoIdList);
//		if (CollectionUtils.isEmpty(reservationInfoList)) {
//			return result;
//		}
//		reservationInfoList.forEach(reservationInfo -> {
//			ReservationInfoDto reservationInfoDto = reserveDtoTransferBuilder.toDto(reservationInfo);
//			List<FeedBack> feedBackList = feedBackRepository.findByUserNameAndReservationInfoId(userName,
//				reservationInfo.getReservationInfoId());
//			reservationInfoDto.setHasFeedback(!CollectionUtils.isEmpty(feedBackList));
//			result.add(reservationInfoDto);
//		});
//		return result;
//	}

	@Override
	public List<ReservationInfoDto> findReservationInfoByDate(Date reserveDate) {
		if (reserveDate == null) {
			return new ArrayList<>();
		}
		List<ReservationInfo> reservationInfoList = reservationInfoRepository.findByReserveDateAndDeletedFalse(reserveDate);
		if (CollectionUtils.isEmpty(reservationInfoList)) {
			return new ArrayList<>();
		}
		return reservationInfoList.stream().map(reservationInfo -> reserveDtoTransferBuilder.toDto(reservationInfo)).collect(Collectors.toList());
	}
}
