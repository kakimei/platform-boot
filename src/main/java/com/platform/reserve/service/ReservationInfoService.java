package com.platform.reserve.service;

import com.platform.reserve.service.dto.ReservationInfoDto;

import java.util.Date;
import java.util.List;

public interface ReservationInfoService {

	void save(ReservationInfoDto reservationInfoDto);

	ReservationInfoDto findReservationInfoById(String user, Long reservationInfoId);

	List<ReservationInfoDto> findReservationInfoByLinkman(String linkmanName);

	List<ReservationInfoDto> findReservationInfoByUser(String userName);

	List<ReservationInfoDto> findAllActiveReservationInfo();

	ReservationInfoDto cancel(String user, Long reservationInfoId);

	List<ReservationInfoDto> findReservationInfoByDateAndTime(Date reserveDate, String timeString);

	List<ReservationInfoDto> findReservationInfoAndFeedbackByUserNameAndId(String userName, List<Long> reservationInfoIdList);
}
