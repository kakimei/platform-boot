package com.platform.reserve.facade;

import com.platform.common.util.MailService;
import com.platform.reserve.controller.vo.ActivityType;
import com.platform.reserve.controller.vo.ReserveVO;
import com.platform.facade.Request;
import com.platform.facade.Response;
import com.platform.facade.ResponseType;
import com.platform.reserve.controller.vo.Sex;
import com.platform.reserve.facade.exception.ReserveException;
import com.platform.reserve.service.ReservationInfoService;
import com.platform.reserve.service.ReserveDtoTransferBuilder;
import com.platform.reserve.service.dto.ReservationInfoDto;
import com.platform.resource.repository.entity.MetaType;
import com.platform.resource.service.TimeResourceService;
import com.platform.sign.service.SignReservationInfoService;
import com.platform.sign.service.dto.SignReservationInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReserveFacade {

	@Autowired
	private MailService mailService;

	@Autowired
	private ReserveDtoTransferBuilder reserveDtoTransferBuilder;

	@Autowired
	private TimeResourceService timeResourceService;

	@Autowired
	private SignReservationInfoService signReservationInfoService;

	@Autowired
	private ReservationInfoService reservationInfoService;

	@Value("#{environment['receiver.email.address']}")
	private String emailReceiver;

	@Value("#{environment['receiver.email.subject']}")
	private String emailSubject;

	@Value("#{environment['receiver.email.content.platform']}")
	private String emailContentPlatform;

	private static final String LINK_MAN_NAME = "{linkManName}";
	private static final String ACTIVITY_TYPE = "{activityType}";
	private static final String PEOPLE_COUNT = "{peopleCount}";
	private static final String PHONE_NUMBER = "{phoneNumber}";
	private static final String RESERVE_DAY = "{reserveDay}";
	private static final String RESERVE_TIME = "{reserveTime}";
	private static final String SEX = "{sex}";
	private static final String AGE = "{age}";

	private static final String DATE_FORMAT = "yyyy-MM-dd";

	public Response<ReserveVO> reserve(Request<ReserveVO> request) {
		ReserveVO reserveVO = request.getEntity();
		try {
			if(!timeResourceService.isInValidTimeResource(reserveVO.getReserveDay(), reserveVO.getTimeString(), MetaType.valueOf(reserveVO.getActivityType().name()), reserveVO.getPeopleCount())){
				throw new ReserveException("the date time is not valid, Please choose valid date time.");
			}
			mailService.sendMail(emailReceiver, emailSubject, buildEmailContent(reserveVO, emailContentPlatform),
				() -> reservationInfoService.save(reserveDtoTransferBuilder.toDto(reserveVO)));
		} catch (EmailException | ReserveException e) {
			log.error(e.getMessage(), e);
			return ReserveResponse.<ReserveVO>builder().responseType(ResponseType.FAIL).entity(reserveVO).build();
		}
		return ReserveResponse.<ReserveVO>builder().responseType(ResponseType.SUCCESS).entity(reserveVO).build();
	}

	private String buildEmailContent(ReserveVO reserveVO, String contentPlatform) {
		if (reserveVO == null) {
			return contentPlatform;
		}
		String linkManName = reserveVO.getLinkManName();
		ActivityType activityType = reserveVO.getActivityType();
		Integer peopleCount = reserveVO.getPeopleCount();
		String phoneNumber = reserveVO.getPhoneNumber();
		Date reserveBegin = reserveVO.getReserveDay();
		Sex sex = reserveVO.getSex();
		Integer age = reserveVO.getAge();
		String timeString = reserveVO.getTimeString();

		return contentPlatform.replace(LINK_MAN_NAME, linkManName)
			.replace(ACTIVITY_TYPE, activityType.getDisplayName())
			.replace(PEOPLE_COUNT, String.valueOf(peopleCount))
			.replace(PHONE_NUMBER, phoneNumber)
			.replace(RESERVE_DAY, DateFormatUtils.format(reserveBegin, DATE_FORMAT))
			.replace(SEX, sex.getDisplayName())
			.replace(AGE, String.valueOf(age))
			.replace(RESERVE_TIME, reserveDtoTransferBuilder.buildFormatString(timeString));
	}

	public Response<List<ReserveVO>> getReservationListByUserName(Request<ReserveVO> request) {
		ReserveVO reserveVO = request.getEntity();
		List<ReserveVO> result = new ArrayList<>();
		try {
			List<ReservationInfoDto> reservationList = reservationInfoService.findReservationInfoByUser(reserveVO.getUserName());
			if (!CollectionUtils.isEmpty(reservationList)) {
				reservationList.forEach(reservationInfoDto -> result.add(reserveDtoTransferBuilder.toVO(reservationInfoDto)));
			}
			return ReserveResponse.<List<ReserveVO>>builder().responseType(ResponseType.SUCCESS).entity(result).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ReserveResponse.<List<ReserveVO>>builder().responseType(ResponseType.FAIL).entity(result).build();
		}
	}

	public Response<List<ReserveVO>> getReservationListBySignIn(Request<ReserveVO> request) {
		ReserveVO reserveVO = request.getEntity();
		List<ReserveVO> result = new ArrayList<>();
		try {
			List<SignReservationInfoDTO> signReservationInfoDTOList = signReservationInfoService.getSignedListByUserName(reserveVO.getUserName());
			if (CollectionUtils.isEmpty(signReservationInfoDTOList)) {
				return ReserveResponse.<List<ReserveVO>>builder().responseType(ResponseType.SUCCESS).entity(result).build();
			}
			List<Long> reservationInfoIdList = signReservationInfoDTOList.stream().map(
				signReservationInfoDTO -> signReservationInfoDTO.getReservationInfoId()).collect(Collectors.toList());
			List<ReservationInfoDto> reservationList = reservationInfoService.findReservationInfoAndFeedbackByUserNameAndId(reserveVO.getUserName(),
				reservationInfoIdList);
			if (!CollectionUtils.isEmpty(reservationList)) {
				reservationList.forEach(reservationInfoDto -> result.add(reserveDtoTransferBuilder.toVO(reservationInfoDto)));
			}
			return ReserveResponse.<List<ReserveVO>>builder().responseType(ResponseType.SUCCESS).entity(result).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ReserveResponse.<List<ReserveVO>>builder().responseType(ResponseType.FAIL).entity(result).build();
		}
	}

	public Response<List<ReserveVO>> getActiveReservationList() {
		List<ReserveVO> result = new ArrayList<>();
		try {
			List<ReservationInfoDto> reservationList = reservationInfoService.findAllActiveReservationInfo();
			if (!CollectionUtils.isEmpty(reservationList)) {
				reservationList.forEach(reservationInfoDto -> {
					ReserveVO reserveVO = reserveDtoTransferBuilder.toVO(reservationInfoDto);
					Date reserveDay = reserveVO.getReserveDay();
					LocalDate localDate = LocalDateTime.ofInstant(reserveDay.toInstant(), ZoneId.systemDefault()).toLocalDate();
					LocalDate today = LocalDate.now();
					reserveVO.setInactiveTime(localDate.isAfter(today));
					result.add(reserveVO);
				});
			}
			return ReserveResponse.<List<ReserveVO>>builder().responseType(ResponseType.SUCCESS).entity(result).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ReserveResponse.<List<ReserveVO>>builder().responseType(ResponseType.FAIL).entity(result).build();
		}
	}

	public Response<ReserveVO> findByReservationInfoId(Request<ReserveVO> request) {
		ReserveVO reserveVO = request.getEntity();
		ReservationInfoDto reservationInfo = reservationInfoService.findReservationInfoById(reserveVO.getUserName(),
			reserveVO.getReservationInfoId());
		if (reservationInfo == null) {
			return ReserveResponse.<ReserveVO>builder().responseType(ResponseType.FAIL).entity(reserveVO).build();
		}
		return ReserveResponse.<ReserveVO>builder().responseType(ResponseType.SUCCESS).entity(
			reserveDtoTransferBuilder.toVO(reservationInfo)).build();
	}

	public Response<ReserveVO> cancel(Request<ReserveVO> request) {
		ReserveVO reserveVO = request.getEntity();
		ReservationInfoDto reservationInfoDto = reservationInfoService.cancel(reserveVO.getUserName(), reserveVO.getReservationInfoId());
		if (reservationInfoDto == null) {
			return ReserveResponse.<ReserveVO>builder().responseType(ResponseType.FAIL).entity(reserveVO).build();
		}
		return ReserveResponse.<ReserveVO>builder().responseType(ResponseType.SUCCESS).entity(
			reserveDtoTransferBuilder.toVO(reservationInfoDto)).build();
	}

	public Response<ReserveVO> getTeamValidDateTime() {
		ReserveVO reserveVO = new ReserveVO();
		reserveVO.setResourceList(timeResourceService.getTeamValidTimeResource());
		return ReserveResponse.<ReserveVO>builder().responseType(ResponseType.SUCCESS).entity(reserveVO).build();
	}

	public Response<ReserveVO> getSingleValidDateTime() {
		ReserveVO reserveVO = new ReserveVO();
		reserveVO.setResourceList(timeResourceService.getSingleValidTimeResource());
		return ReserveResponse.<ReserveVO>builder().responseType(ResponseType.SUCCESS).entity(reserveVO).build();
	}

	public Response<List<ReserveVO>> getReservationListByReserveDate(Request<ReserveVO> request) {
		ReserveVO reserveVO = request.getEntity();
		try {
			List<ReservationInfoDto> reservationInfoList = reservationInfoService.findReservationInfoByDate(reserveVO.getReserveDay());
			if (CollectionUtils.isEmpty(reservationInfoList)) {
				return ReserveResponse.<List<ReserveVO>>builder().responseType(ResponseType.SUCCESS).entity(new ArrayList<>()).build();
			}
			List<ReserveVO> result = reservationInfoList.stream().map(
				reservationInfoDto -> reserveDtoTransferBuilder.toVO(reservationInfoDto)).collect(Collectors.toList());
			return ReserveResponse.<List<ReserveVO>>builder().responseType(ResponseType.SUCCESS).entity(result).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ReserveResponse.<List<ReserveVO>>builder().responseType(ResponseType.FAIL).entity(new ArrayList<>()).build();
		}
	}
}
