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
@Table(name = "activity")
@Builder
public class Activity {

	@Id
	@Column(name = "activity_id", unique = true)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long activityId;

	@Column(name = "activityType", nullable = false)
	@Enumerated(value = EnumType.STRING)
	private ActivityType activityType;


}
