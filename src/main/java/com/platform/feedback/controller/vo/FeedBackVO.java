package com.platform.feedback.controller.vo;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Builder
@Data
@Slf4j
@NoArgsConstructor
public class FeedBackVO {
	private Long reservationInfoId;

	private FeedBackType feedBackType;

	private Integer count;
}
