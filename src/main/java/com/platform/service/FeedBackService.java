package com.platform.service;

import com.platform.service.dto.FeedBackDto;

public interface FeedBackService {

	void save(FeedBackDto feedBackDto);

	void update(FeedBackDto feedBackDto);

	FeedBackDto findFeedBackByReservationInfoId(Long reservationInfoId);
}
