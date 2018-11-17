package com.platform.reserve.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

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
public class ReservationInfo {

	@Id
	@Column(name = "id", unique = true)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "reservation_info_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
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

	@Column(name = "reserve_begin", nullable = false)
	private Date reserveBegin;

	@Column(name = "reserve_end", nullable = false)
	private Date reserveEnd;

	@Column(name = "people_count", nullable = false)
	private Integer peopleCount;

	@Column(name = "delete", nullable = false)
	private Boolean delete;

	@Column(name = "sign_in", nullable = false)
	private Boolean signIn;

	@Column(name = "activity_id", nullable = false)
	private Long activityId;

}
