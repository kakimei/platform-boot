package com.platform.sign.service;

import com.platform.sign.service.dto.SignReservationInfoDTO;

import java.util.List;

public interface SignReservationInfoService {

	void save(SignReservationInfoDTO signReservationInfoDTO);

	Boolean hasSignedByReservationInfoIdAndUserName(Long reservationInfoId, String userName);

	List<SignReservationInfoDTO> getSignedListByUserName(String userName);
}
