package com.platform.feedback.service;

import com.platform.feedback.repository.entity.FeedBack;
import com.platform.feedback.repository.entity.FeedBackType;
import com.platform.feedback.service.dto.FeedBackDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FeedBackDtoTransferBuilder {

	public FeedBackType toFeedBackType(com.platform.feedback.controller.vo.FeedBackType feedBackType){
		return FeedBackType.valueOf(feedBackType.name());
	}

	public FeedBack toEntity(FeedBackDto feedBackDto){
		if(feedBackDto == null){
			log.warn("feedBackDto is null");
			return null;
		}
		FeedBack feedBack = new FeedBack();
		BeanUtils.copyProperties(feedBackDto, feedBack, "reservationInfoId");
		return feedBack;
	}

	public FeedBackDto toDto(FeedBack feedBack){
		if(feedBack == null){
			log.warn("feedBack is null");
			return null;
		}
		FeedBackDto feedBackDto = new FeedBackDto();
		BeanUtils.copyProperties(feedBack, feedBackDto);
		return feedBackDto;
	}
}
