package com.platform.service.dto;

import com.platform.repository.entity.Sex;
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
