package com.platform.feedback.facade;

import com.platform.facade.Request;
import com.platform.feedback.controller.vo.FeedBackType;
import com.platform.feedback.service.FeedBackDtoTransferBuilder;
import com.platform.feedback.service.FeedBackService;
import com.platform.feedback.service.dto.FeedBackDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class FeedBackFacade {

	@Autowired
	private FeedBackService feedBackService;

	@Autowired
	private FeedBackDtoTransferBuilder feedBackDtoTransferBuilder;

	@Transactional(propagation = Propagation.REQUIRED)
	public void thumbsUp(Request<Long> reservationInfoId){
		thumbs(reservationInfoId.getEntity(), FeedBackType.GOOD);
	}

	public void thumbsDown(Request<Long> reservationInfoId){
		thumbs(reservationInfoId.getEntity(), FeedBackType.BAD);
	}

	public void thumbsNormal(Request<Long> reservationInfoId){
		thumbs(reservationInfoId.getEntity(), FeedBackType.NORMAL);
	}

	private void thumbs(Long reservationInfoId, FeedBackType feedBackType){
		FeedBackDto feedBackDto = FeedBackDto.builder().reservationInfoId(reservationInfoId).feedBackType(
			feedBackDtoTransferBuilder.toFeedBackType(feedBackType)).build();
		feedBackService.saveOrUpdate(feedBackDto);
	}
}
