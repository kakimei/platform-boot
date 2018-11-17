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

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional(propagation = Propagation.REQUIRED)
public class FeedBackServiceImpl implements FeedBackService{

	@Autowired
	private FeedBackRepository feedBackRepository;

	@Autowired
	private FeedBackDtoTransferBuilder feedBackDtoTransferBuilder;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveOrUpdate(FeedBackDto feedBackDto) {
		if(feedBackDto == null){
			log.warn("no feedback need to save.");
			return;
		}
		if(feedBackDto.getReservationInfoId() == null){
			FeedBack feedBack = feedBackDtoTransferBuilder.toEntity(feedBackDto);
			feedBack.setCount(1);
			feedBackRepository.save(feedBack);
		}else{
			FeedBack feedBack = feedBackRepository.findByReservationInfoIdAndFeedBackType(
				feedBackDto.getReservationInfoId(), feedBackDto.getFeedBackType());
			if(feedBack == null){
				feedBack = feedBackDtoTransferBuilder.toEntity(feedBackDto);
				feedBack.setCount(1);
				feedBackRepository.save(feedBack);
				return;
			}
			feedBack.setCount(feedBack.getCount() + 1);
			feedBackRepository.save(feedBack);
		}
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public List<FeedBackDto> findFeedBackByReservationInfoId(Long reservationInfoId) {
		List<FeedBackDto> result = new ArrayList<>();
		if(reservationInfoId == null){
			return result;
		}
		List<FeedBack> feedBackList = feedBackRepository.findByReservationInfoId(reservationInfoId);
		if(CollectionUtils.isEmpty(feedBackList)){
			return result;
		}
		feedBackList.forEach(feedBack -> result.add(feedBackDtoTransferBuilder.toDto(feedBack)));
		return result;
	}
}
