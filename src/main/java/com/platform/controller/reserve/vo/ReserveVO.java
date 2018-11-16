package com.platform.controller.reserve.vo;

import com.platform.repository.entity.Sex;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class ReserveVO {
	private String linkManName;
	private Sex sex;
	private Integer age;
	private String phoneNumber;
	private Date reserveBegin;
	private Date reserveEnd;
	private Integer peopleCount;
	private Long activityId;

	public boolean canReserve(){
		if(StringUtils.isEmpty(this.linkManName)){
			log.error("linkMan name can not be null.");
			return false;
		}
		if(StringUtils.isEmpty(this.phoneNumber)){
			log.error("phoneNumber can not be null.");
			return false;
		}
		if(reserveBegin == null || reserveEnd == null){
			log.error("reserve date can not be null.");
			return false;
		}
		if(peopleCount == null || peopleCount <=0 ){
			log.error("people can not be 0.");
			return false;
		}
		if(activityId == null){
			log.error("activity can not be null");
			return false;
		}
		return true;
	}
}
