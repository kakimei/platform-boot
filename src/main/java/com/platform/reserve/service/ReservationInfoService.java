package com.platform.reserve.service;

import com.platform.reserve.service.dto.ReservationInfoDto;

import java.util.List;

public interface ReservationInfoService {

	void save(ReservationInfoDto reservationInfoDto);

	ReservationInfoDto findReservationInfoById(String user, Long reservationInfoId);

	List<ReservationInfoDto> findReservationInfoByLinkman(String linkmanName);

	List<ReservationInfoDto> findReservationInfoByUser(String userName);

	ReservationInfoDto singIn(String user, Long reservationInfoId);

	List<ReservationInfoDto> findAllActiveReservationInfo();
}
