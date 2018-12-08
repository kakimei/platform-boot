package com.platform.reserve.repository;

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

	List<ReservationInfo> findByUserNameAndDeletedFalseOrderByReservationInfoId(String userName);

	List<ReservationInfo> findByDeletedFalse();

	List<ReservationInfo> findByReserveDateAndReserveBeginHHAndReserveBeginMMAndReserveEndHHAndReserveEndMMAndDeletedFalse(Date reserveDate,
		Integer reserveBeginHH, Integer reserveBeginMM, Integer reserveEndHH, Integer reserveEndMM);

	List<ReservationInfo> findByReservationInfoIdInAndDeletedFalse(List<Long> reservationInfoIdList);
}
