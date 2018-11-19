package com.platform.reserve.repository;

import com.platform.reserve.repository.entity.ReservationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationInfoRepository extends JpaRepository<ReservationInfo, Long> {

	ReservationInfo findByReservationInfoIdAndDeletedFalse(Long reservationInfoId);

	List<ReservationInfo> findByLinkManNameAndDeletedFalse(String linkManName);
}
