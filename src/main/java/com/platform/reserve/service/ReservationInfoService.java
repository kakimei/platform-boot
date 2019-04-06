package com.platform.reserve.service;

import com.platform.reserve.service.dto.ReservationInfoDto;

import java.util.Date;
import java.util.List;

public interface ReservationInfoService {

	Long save(ReservationInfoDto reservationInfoDto) throws Exception;

	void update(ReservationInfoDto reservationInfoDto) throws Exception;

	ReservationInfoDto findReservationInfoByIdAndUser(String user, Long reservationInfoId);

	ReservationInfoDto findReservationInfoById(Long reservationInfoId);

	List<ReservationInfoDto> findReservationInfoByLinkman(String userName, String linkmanName);

	List<ReservationInfoDto> findReservationInfoByPhoneNumber(String userName, String phoneNumber);

	List<ReservationInfoDto> findReservationInfoByActivityType(String userName, String activityType);

	List<ReservationInfoDto> findReservationInfoByUser(String userName);

	List<ReservationInfoDto> findReservationInfoByUserAndActivityType(String userName, String activityType);

	List<ReservationInfoDto> findAllActiveReservationInfo();

	List<ReservationInfoDto> findAllActiveTeamReservationInfo();

	List<ReservationInfoDto> findAllActiveSingleReservationInfo();

	ReservationInfoDto cancel(String user, Long reservationInfoId) throws Exception;

	ReservationInfoDto cancel(Long reservationInfoId) throws Exception;

	List<ReservationInfoDto> findReservationInfoByDateAndTime(Date reserveDate, String timeString);

	List<ReservationInfoDto> findReservationInfoByDate(Date reserveDate);

//	List<ReservationInfoDto> findReservationInfoAndFeedbackByUserNameAndId(String userName, List<Long> reservationInfoIdList);
}
