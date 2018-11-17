package com.platform.reserve.service.dto;

import com.platform.reserve.repository.entity.Sex;
import com.platform.activity.service.dto.ActivityDto;
import com.platform.feedback.service.dto.FeedBackDto;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Builder
@Data
public class ReservationInfoDto {

	private Long reservationInfoId;

	private String linkManName;

	private Sex sex;

	private Integer age;

	private String phoneNumber;

	private Date reserveBegin;

	private Date reserveEnd;

	private Integer peopleCount;

	private Boolean signIn;

	private ActivityDto activityDto;

	private FeedBackDto feedBackDto;
}
