package com.platform.reserve.service;

import com.platform.reserve.service.dto.ReservationInfoDto;

import java.util.Date;
import java.util.List;

public interface ReservationInfoService {

	void save(ReservationInfoDto reservationInfoDto);

	void update(ReservationInfoDto reservationInfoDto);

	ReservationInfoDto findReservationInfoByIdAndUser(String user, Long reservationInfoId);

	ReservationInfoDto findReservationInfoById(Long reservationInfoId);

	List<ReservationInfoDto> findReservationInfoByLinkman(String userName, String linkmanName);

	List<ReservationInfoDto> findReservationInfoByPhoneNumber(String userName, String phoneNumber);

	List<ReservationInfoDto> findReservationInfoByActivityType(String userName, String activityType);

	List<ReservationInfoDto> findReservationInfoByUser(String userName);

	List<ReservationInfoDto> findAllActiveReservationInfo();

	List<ReservationInfoDto> findAllActiveTeamReservationInfo();

	List<ReservationInfoDto> findAllActiveSingleReservationInfo();

	ReservationInfoDto cancel(String user, Long reservationInfoId);

	ReservationInfoDto cancel(Long reservationInfoId);

	List<ReservationInfoDto> findReservationInfoByDateAndTime(Date reserveDate, String timeString);

	List<ReservationInfoDto> findReservationInfoByDate(Date reserveDate);

//	List<ReservationInfoDto> findReservationInfoAndFeedbackByUserNameAndId(String userName, List<Long> reservationInfoIdList);
}
