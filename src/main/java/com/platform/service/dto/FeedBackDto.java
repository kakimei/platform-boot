package com.platform.service.dto;

import com.platform.repository.entity.FeedBackType;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FeedBackDto {

	private Long reservationInfoId;

	private FeedBackType feedBackType;

	private Integer count;
}
