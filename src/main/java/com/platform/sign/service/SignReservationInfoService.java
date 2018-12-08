package com.platform.sign.service;

import com.platform.sign.service.dto.SignReservationInfoDTO;

public interface SignReservationInfoService {

	void save(SignReservationInfoDTO signReservationInfoDTO);

	Boolean hasSignedByReservationInfoIdAndUserName(Long reservationInfoId, String userName);
}
