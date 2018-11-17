package com.platform.reserve.service;

import com.platform.reserve.service.dto.ReservationInfoDto;

import java.util.List;

public interface ReservationInfoService {

	void save(ReservationInfoDto reservationInfoDto);

	ReservationInfoDto findReservationInfoById(Long id);

	List<ReservationInfoDto> findReservationInfoByLinkman(String linkmanName);

	void update(ReservationInfoDto reservationInfoDto);
}
