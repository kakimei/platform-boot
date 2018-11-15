package com.platform.repository;

import com.platform.repository.entity.ReservationInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationInfoRepository extends JpaRepository<ReservationInfo, Long> {
}
