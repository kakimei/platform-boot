package com.platform.sign.repository;

import com.platform.sign.repository.entity.SignReservationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SignReservationInfoRepository extends JpaRepository<SignReservationInfo, Long> {

	List<SignReservationInfo> findByUserNameAndAndReservationInfoIdAndSignInTrue(String userName, Long reservationInfoId);

	List<SignReservationInfo> findByUserNameAndSignInTrue(String userName);
}
