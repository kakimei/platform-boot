package com.platform.reserve.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "reservation_info")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReservationInfo {

	@Id
	@Column(name = "reservation_info_id", unique = true)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long reservationInfoId;

	@Column(name = "linkman_name", nullable = false)
	private String linkManName;

	@Column(name = "sex", nullable = false)
	@Enumerated(value = EnumType.STRING)
	private Sex sex;

	@Column(name = "age")
	private Integer age;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "reserve_date", nullable = false)
	private Date reserveDate;

	@Column(name = "reserve_begin_hour", nullable = false)
	private Integer reserveBeginHH;

	@Column(name = "reserve_begin_minute", nullable = false)
	private Integer reserveBeginMM;

	@Column(name = "reserve_end_hour", nullable = false)
	private Integer reserveEndHH;

	@Column(name = "reserve_end_minute", nullable = false)
	private Integer reserveEndMM;

	@Column(name = "people_count", nullable = false)
	private Integer peopleCount;

	@Column(name = "deleted", nullable = false)
	private Boolean deleted = false;

	@Column(name = "activity_type", nullable = false)
	@Enumerated(value = EnumType.STRING)
	private ActivityType activityType;

	@Column(name = "user_name", nullable = false)
	private String userName;
}
