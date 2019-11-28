package com.platform.reserve.service.dto;

import com.platform.reserve.controller.vo.Operator;
import com.platform.reserve.repository.entity.ActivityType;
import com.platform.reserve.repository.entity.Sex;
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

	private String identityCard;

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

	private Boolean hasSigned;

	private String userName;

	private String yearWeek;

	private Operator operator;

	public boolean isFromUser() {
		return Operator.USER.equals(operator);
	}
}
