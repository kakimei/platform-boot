package com.platform.service;

import com.platform.service.dto.ReservationInfoDto;

import java.util.List;

public interface ReservationInfoService {

	void save(ReservationInfoDto reservationInfoDto);

	ReservationInfoDto findReservationInfoById(Long id);

	List<ReservationInfoDto> findReservationInfoByLinkman(String linkmanName);

	void update(ReservationInfoDto reservationInfoDto);
}
