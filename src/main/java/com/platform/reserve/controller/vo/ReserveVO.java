package com.platform.reserve.controller.vo;

import com.platform.resource.service.dto.TimeResourceDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class ReserveVO {
	private Long reservationInfoId;
	private String linkManName;
	private String identityCard;
	private Sex sex;
	private Integer age;
	private String phoneNumber;
	private Date reserveDay;
	private String timeString;
	private Integer peopleCount;
	private ActivityType activityType;
	private Integer peopleNumberThreshold = 15;
	private String userName;
	private Integer feedBack;
	private List<Map.Entry<String, List<TimeResourceDto.TimeDTO>>> resourceList;
	private Boolean hasSigned;
	private Boolean inactiveTime;
	private Boolean hasFeedback;
	private Boolean canModify;
	private Operator operator;

	public boolean canReserve(){
		if(StringUtils.isEmpty(this.linkManName)){
			log.error("linkMan name can not be null.");
			return false;
		}
		if(StringUtils.isEmpty(this.identityCard)){
			log.error("identityCard can not be null.");
			return false;
		}
		if(StringUtils.isEmpty(this.phoneNumber)){
			log.error("phoneNumber can not be null.");
			return false;
		}
		if(reserveDay == null){
			log.error("reserve date is null");
			return false;
		}
		reserveDay = DateUtils.truncate(reserveDay, Calendar.DATE);
		if(!todayBeforeCurrentDay()){
			log.error("can not reserve because reserve begin date is after today.");
			return false;
		}
		if(peopleCount == null || peopleCount <=0 ){
			log.error("people can not be 0.");
			return false;
		}
		if(activityType == null){
			log.error("activity can not be null");
			return false;
		}
		if(!teamActivityNumberCheck()){
			log.error("activity type does not match people number");
			return false;
		}
		return true;
	}

	private boolean todayBeforeCurrentDay(){
		LocalDate today = LocalDate.now();
		LocalDate beginDate = reserveDay.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return today.isBefore(beginDate);
	}

	private boolean teamActivityNumberCheck(){
		return activityType.isTeam() ? (peopleCount > peopleNumberThreshold) : (peopleCount <= peopleNumberThreshold);
	}

	public boolean isBoChannel() {
		return Operator.BO.equals(operator);
	}
}
