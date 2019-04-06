package com.platform.reserve.repository;

import com.platform.reserve.repository.entity.ActivityType;
import com.platform.reserve.repository.entity.ReservationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ReservationInfoRepository extends JpaRepository<ReservationInfo, Long> {

	ReservationInfo findByReservationInfoIdAndUserNameAndDeletedFalse(Long reservationInfoId, String userName);

	ReservationInfo findByReservationInfoIdAndDeletedFalse(Long reservationInfoId);

	List<ReservationInfo> findByLinkManNameAndDeletedFalse(String linkManName);

	List<ReservationInfo> findByUserNameAndLinkManNameAndDeletedFalse(String userName, String linkManName);

	List<ReservationInfo> findByUserNameAndPhoneNumberAndDeletedFalse(String userName, String phoneNumber);

	List<ReservationInfo> findByActivityTypeAndDeletedFalseOrderByReservationInfoIdDesc(ActivityType activityType);

	List<ReservationInfo> findByUserNameAndDeletedFalseOrderByReservationInfoIdDesc(String userName);

	List<ReservationInfo> findByUserNameAndActivityTypeAndDeletedFalseOrderByReservationInfoIdDesc(String userName, ActivityType activityType);

	List<ReservationInfo> findByDeletedFalse();

	List<ReservationInfo> findByActivityTypeAndDeletedFalse(ActivityType activityType);

	List<ReservationInfo> findByReserveDateAndReserveBeginHHAndReserveBeginMMAndReserveEndHHAndReserveEndMMAndDeletedFalse(Date reserveDate,
		Integer reserveBeginHH, Integer reserveBeginMM, Integer reserveEndHH, Integer reserveEndMM);

	List<ReservationInfo> findByReservationInfoIdInAndDeletedFalse(List<Long> reservationInfoIdList);

	List<ReservationInfo> findByReserveDateAndDeletedFalse(Date reserveDate);

	List<ReservationInfo> findByActivityTypeAndReserveDateBetweenAndDeletedFalse(ActivityType activityType, Date beginDate, Date endDate);
}
