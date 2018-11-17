package com.platform.feedback.service.dto;

import com.platform.feedback.repository.entity.FeedBackType;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FeedBackDto {

	private Long reservationInfoId;

	private FeedBackType feedBackType;

	private Integer count;
}
