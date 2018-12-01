package com.platform.reserve.controller.vo;

import com.platform.feedback.controller.vo.FeedBackVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class ReserveVO {
	private Long reservationInfoId;
	private String linkManName;
	private Sex sex;
	private Integer age;
	private String phoneNumber;
	private Date reserveBegin;
	private Date reserveEnd;
	private Integer peopleCount;
	private ActivityType activityType;
	private Integer peopleNumberThreshold = 10;
	private String userName;
	private Boolean signIn;
	private Integer feedBack;

	public boolean canReserve(){
		if(StringUtils.isEmpty(this.linkManName)){
			log.error("linkMan name can not be null.");
			return false;
		}
		if(StringUtils.isEmpty(this.phoneNumber)){
			log.error("phoneNumber can not be null.");
			return false;
		}
		if(reserveBegin == null || reserveEnd == null || reserveEnd.before(reserveBegin)){
			log.error("reserve date can not be null or reserveEnd is before reserveBegin.");
			return false;
		}
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
		LocalDate beginDate = reserveBegin.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return today.isBefore(beginDate);
	}

	private boolean teamActivityNumberCheck(){
		return activityType.isTeam() ? (peopleCount > peopleNumberThreshold) : (peopleCount <= peopleNumberThreshold);
	}
}
