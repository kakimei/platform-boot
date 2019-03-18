package com.platform.reserve.service.dto;

import com.platform.reserve.repository.entity.ActivityType;
import com.platform.reserve.repository.entity.Sex;
import com.platform.feedback.service.dto.FeedBackDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationInfoDto {

	private Long reservationInfoId;

	private String linkManName;

	private Sex sex;

	private Integer age;

	private String phoneNumber;

	private Date reserveDate;

	private Integer reserveBeginHH;

	private Integer reserveBeginMM;

	private Integer reserveEndHH;

	private Integer reserveEndMM;

	private Integer peopleCount;

	private Long activityId;

	private ActivityType activityType;

	private Boolean hasFeedback;

	private String userName;

	private String yearWeek;
}
