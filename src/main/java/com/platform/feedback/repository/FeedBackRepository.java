package com.platform.feedback.repository;

import com.platform.feedback.repository.entity.FeedBack;
import com.platform.feedback.repository.entity.FeedBackType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedBackRepository extends JpaRepository<FeedBack, Long> {

	FeedBack findByReservationInfoIdAndFeedBackType(Long reservationInfoId, FeedBackType feedBackType);

	List<FeedBack> findByReservationInfoId(Long reservationInfoId);
}
