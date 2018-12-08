package com.platform.feedback.facade;

import com.platform.facade.Request;
import com.platform.feedback.controller.vo.FeedBackType;
import com.platform.feedback.controller.vo.FeedBackVO;
import com.platform.feedback.service.FeedBackDtoTransferBuilder;
import com.platform.feedback.service.FeedBackService;
import com.platform.feedback.service.dto.FeedBackDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
	public void thumbsUp(Request<FeedBackVO> reservationInfoId) {
		thumbs(reservationInfoId.getEntity(), FeedBackType.GOOD);
	}

	public void thumbsDown(Request<FeedBackVO> reservationInfoId) {
		thumbs(reservationInfoId.getEntity(), FeedBackType.BAD);
	}

	public void thumbsNormal(Request<FeedBackVO> reservationInfoId) {
		thumbs(reservationInfoId.getEntity(), FeedBackType.NORMAL);
	}

	private void thumbs(FeedBackVO feedBackVO, FeedBackType feedBackType) {
		if(feedBackVO == null || StringUtils.isBlank(feedBackVO.getUserName()) || feedBackVO.getReservationInfoId() == null){
			log.warn("feed back failed, feed back info contains invalid field.");
			return;
		}
		FeedBackDto feedBackDto = FeedBackDto.builder().userName(feedBackVO.getUserName()).reservationInfoId(
			feedBackVO.getReservationInfoId()).feedBackType(
			feedBackDtoTransferBuilder.toFeedBackType(feedBackType)).build();
		feedBackService.save(feedBackDto);
	}
}
