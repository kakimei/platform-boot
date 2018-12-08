package com.platform.sign.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "sign_reservation_info")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SignReservationInfo {

	@Id
	@Column(name = "sign_reservation_info_id", unique = true)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long signReservationInfoId;

	@Column(name = "reservation_info_id", nullable = false)
	private Long reservationInfoId;

	@Column(name = "user_name", nullable = false)
	private String userName;

	@Column(name = "sign_in", nullable = false)
	private Boolean signIn;
}
