package com.platform.feedback.service;

import com.platform.feedback.repository.FeedBackRepository;
import com.platform.feedback.repository.entity.FeedBack;
import com.platform.feedback.service.dto.FeedBackDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
public class FeedBackServiceImpl implements FeedBackService {

	@Autowired
	private FeedBackRepository feedBackRepository;

	@Autowired
	private FeedBackDtoTransferBuilder feedBackDtoTransferBuilder;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(FeedBackDto feedBackDto) {
		if (feedBackDto == null || StringUtils.isEmpty(feedBackDto.getUserName())) {
			log.warn("no feedback need to save.");
			return;
		}

		List<FeedBack> feedBackList = feedBackRepository.findByUserNameAndReservationInfoId(feedBackDto.getUserName(),
			feedBackDto.getReservationInfoId());
		if (!CollectionUtils.isEmpty(feedBackList)) {
			log.warn("user {} has signed reservation {}, can not reserve again!", feedBackDto.getUserName(), feedBackDto.getReservationInfoId());
			return;
		}
		FeedBack feedBack = feedBackDtoTransferBuilder.toEntity(feedBackDto);
		feedBackRepository.save(feedBack);
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public List<FeedBackDto> findFeedBackByReservationInfoId(Long reservationInfoId) {
		List<FeedBackDto> result = new ArrayList<>();
		if (reservationInfoId == null) {
			return result;
		}
		List<FeedBack> feedBackList = feedBackRepository.findByReservationInfoId(reservationInfoId);
		if (CollectionUtils.isEmpty(feedBackList)) {
			return result;
		}
		feedBackList.forEach(feedBack -> result.add(feedBackDtoTransferBuilder.toDto(feedBack)));
		return result;
	}
}
