package com.platform.activity.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Activity {

	@Id
	@Column(name = "activity_id", unique = true)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long activityId;

	@Column(name = "activityType", nullable = false)
	@Enumerated(value = EnumType.STRING)
	private ActivityType activityType;


}
