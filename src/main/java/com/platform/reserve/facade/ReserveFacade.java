package com.platform.reserve.facade;

import com.platform.common.util.MailService;
import com.platform.common.util.SendMailCallback;
import com.platform.feedback.service.FeedBackService;
import com.platform.feedback.service.dto.FeedBackDto;
import com.platform.reserve.controller.vo.ActivityType;
import com.platform.reserve.controller.vo.ReserveVO;
import com.platform.facade.Request;
import com.platform.facade.Response;
import com.platform.facade.ResponseType;
import com.platform.reserve.controller.vo.Sex;
import com.platform.reserve.service.ReservationInfoService;
import com.platform.reserve.service.ReserveDtoTransferBuilder;
import com.platform.reserve.service.dto.ReservationInfoDto;
import com.platform.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.mail.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class ReserveFacade {

	@Autowired
	private MailService mailService;

	@Autowired
	private ReservationInfoService reservationInfoService;

	@Autowired
	private ReserveDtoTransferBuilder reserveDtoTransferBuilder;

	@Autowired
	private FeedBackService feedBackService;

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
	private static final String RESERVE_BEGIN = "{reserveBegin}";
	private static final String RESERVE_END = "{reserveEnd}";
	private static final String SEX = "{sex}";
	private static final String AGE = "{age}";

	private static final String DATE_FORMAT = "yyyy-MM-dd";

	public Response<ReserveVO> reserve(Request<ReserveVO> request){
		ReserveVO reserveVO = request.getEntity();
		reserveVO.setSignIn(false);
		try {
			mailService.sendMail(emailReceiver, emailSubject, buildEmailContent(reserveVO, emailContentPlatform), new SendMailCallback() {
				@Override
				public void execute() {
					reservationInfoService.save(reserveDtoTransferBuilder.toDto(reserveVO));
				}
			});
		} catch (EmailException e) {
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
		Date reserveBegin = reserveVO.getReserveBegin();
		Date reserveEnd = reserveVO.getReserveEnd();
		Sex sex = reserveVO.getSex();
		Integer age = reserveVO.getAge();

		return contentPlatform.replace(LINK_MAN_NAME, linkManName)
			.replace(ACTIVITY_TYPE, activityType.getDisplayName())
			.replace(PEOPLE_COUNT, String.valueOf(peopleCount))
			.replace(PHONE_NUMBER, phoneNumber)
			.replace(RESERVE_BEGIN, DateFormatUtils.format(reserveBegin, DATE_FORMAT))
			.replace(RESERVE_END, DateFormatUtils.format(reserveEnd, DATE_FORMAT))
			.replace(SEX, sex.getDisplayName())
			.replace(AGE, String.valueOf(age));
	}

	public Response<List<ReserveVO>> getReservationListByUserName(Request<ReserveVO> request){
		ReserveVO reserveVO = request.getEntity();
		List<ReserveVO> result = new ArrayList<>();
		try {
			List<ReservationInfoDto> reservationList = reservationInfoService.findReservationInfoByUser(reserveVO.getUserName());
			if(!CollectionUtils.isEmpty(reservationList)){
				reservationList.forEach(reservationInfoDto -> result.add(reserveDtoTransferBuilder.toVO(reservationInfoDto)));
			}
			return ReserveResponse.<List<ReserveVO>>builder().responseType(ResponseType.SUCCESS).entity(result).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ReserveResponse.<List<ReserveVO>>builder().responseType(ResponseType.FAIL).entity(result).build();
		}
	}

	public Response<List<ReserveVO>> getActiveReservationList(){
		List<ReserveVO> result = new ArrayList<>();
		try {
			List<ReservationInfoDto> reservationList = reservationInfoService.findAllActiveReservationInfo();
			if(!CollectionUtils.isEmpty(reservationList)){
				reservationList.forEach(reservationInfoDto -> {
					ReserveVO reserveVO = reserveDtoTransferBuilder.toVO(reservationInfoDto);
					List<FeedBackDto> feedBackDtoList = feedBackService.findFeedBackByReservationInfoId(reserveVO.getReservationInfoId());
					reserveVO.setFeedBack(CollectionUtils.isEmpty(feedBackDtoList) ? 0 : feedBackDtoList.stream().mapToInt(FeedBackDto::getCount).sum());
					result.add(reserveVO);
				});
			}
			return ReserveResponse.<List<ReserveVO>>builder().responseType(ResponseType.SUCCESS).entity(result).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return ReserveResponse.<List<ReserveVO>>builder().responseType(ResponseType.FAIL).entity(result).build();
		}
	}

	public Response<ReserveVO> findByReservationInfoId(Request<ReserveVO> request){
		ReserveVO reserveVO = request.getEntity();
		ReservationInfoDto reservationInfo = reservationInfoService.findReservationInfoById(reserveVO.getUserName(),
			reserveVO.getReservationInfoId());
		if(reservationInfo == null){
			return ReserveResponse.<ReserveVO>builder().responseType(ResponseType.FAIL).entity(reserveVO).build();
		}
		return ReserveResponse.<ReserveVO>builder().responseType(ResponseType.SUCCESS).entity(reserveDtoTransferBuilder.toVO(reservationInfo)).build();
	}

	public Response<ReserveVO> signIn(Request<ReserveVO> request){
		ReserveVO reserveVO = request.getEntity();
		ReservationInfoDto reservationInfoDto = reservationInfoService.singIn(reserveVO.getUserName(), reserveVO.getReservationInfoId());
		if(reservationInfoDto == null){
			return ReserveResponse.<ReserveVO>builder().responseType(ResponseType.FAIL).entity(reserveVO).build();
		}
		return ReserveResponse.<ReserveVO>builder().responseType(ResponseType.SUCCESS).entity(reserveDtoTransferBuilder.toVO(reservationInfoDto)).build();
	}
}
