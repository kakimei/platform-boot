package com.platform.repository.entity;

import lombok.Builder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "feedback")
@Builder
public class FeedBack {

	@Id
	@Column(name = "feedback_id", unique = true)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long feedbackId;

	@Column(name = "reservation_info_id")
	private Long reservationInfoId;

	@Column(name = "feed_back_type", nullable = false)
	@Enumerated(value = EnumType.STRING)
	private FeedBackType feedBackType;

	@Column(name = "count", nullable = false)
	private Integer count;
}