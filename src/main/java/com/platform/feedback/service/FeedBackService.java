package com.platform.feedback.service;

import com.platform.feedback.service.dto.FeedBackDto;

public interface FeedBackService {

	void save(FeedBackDto feedBackDto);

	void update(FeedBackDto feedBackDto);

	FeedBackDto findFeedBackByReservationInfoId(Long reservationInfoId);
}
