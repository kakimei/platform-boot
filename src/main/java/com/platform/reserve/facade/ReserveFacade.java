package com.platform.reserve.facade;

import com.platform.common.util.MailService;
import com.platform.reserve.controller.vo.ActivityType;
import com.platform.reserve.controller.vo.ReserveVO;
import com.platform.facade.Request;
import com.platform.facade.Response;
import com.platform.facade.ResponseType;
import com.platform.reserve.controller.vo.Sex;
import com.platform.reserve.service.ReservationInfoService;
import com.platform.reserve.service.ReserveDtoTransferBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Slf4j
public class ReserveFacade {

	@Autowired
	private MailService mailService;

	@Autowired
	private ReservationInfoService reservationInfoService;

	@Autowired
	private ReserveDtoTransferBuilder reserveDtoTransferBuilder;

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

	@Transactional(propagation = Propagation.REQUIRED)
	public Response<ReserveVO> reserve(Request<ReserveVO> request){
		ReserveVO reserveVO = request.getEntity();
		reservationInfoService.save(reserveDtoTransferBuilder.toDto(reserveVO));
		mailService.sendMail(emailReceiver, emailSubject, buildEmailContent(reserveVO, emailContentPlatform));
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
			.replace(ACTIVITY_TYPE, activityType.name())
			.replace(PEOPLE_COUNT, String.valueOf(peopleCount))
			.replace(PHONE_NUMBER, phoneNumber)
			.replace(RESERVE_BEGIN, DateFormatUtils.format(reserveBegin, DATE_FORMAT))
			.replace(RESERVE_END, DateFormatUtils.format(reserveEnd, DATE_FORMAT))
			.replace(SEX, sex.name())
			.replace(AGE, String.valueOf(age));
	}
}
