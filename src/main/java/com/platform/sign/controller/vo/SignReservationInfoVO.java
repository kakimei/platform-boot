package com.platform.sign.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class SignReservationInfoVO {

	private Long reservationInfoId;

	private String userName;

	private Boolean signIn;
}
