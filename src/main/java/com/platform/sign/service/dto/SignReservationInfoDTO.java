package com.platform.sign.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignReservationInfoDTO {

	private Long reservationInfoId;

	private String userName;

	private Boolean signIn;
}
