package com.platform.feedback.service;

import com.platform.feedback.service.dto.FeedBackDto;

import java.util.List;

public interface FeedBackService {

	void saveOrUpdate(FeedBackDto feedBackDto);

	List<FeedBackDto> findFeedBackByReservationInfoId(Long reservationInfoId);
}