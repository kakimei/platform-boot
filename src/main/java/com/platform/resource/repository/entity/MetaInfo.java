package com.platform.resource.repository.entity;

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
@Table(name = "meta_info")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MetaInfo {

	@Id
	@Column(name = "meta_info_id", unique = true)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "interval_unit", nullable = false)
	@Enumerated(value = EnumType.STRING)
	private IntervalUnit intervalUnit;

	@Column(name = "day", nullable = true)
	private Integer day;

	@Column(name = "hourBegin", nullable = false)
	private Integer hourBegin;

	@Column(name = "minuteBegin", nullable = false)
	private Integer minuteBegin;

	@Column(name = "hourEnd", nullable = false)
	private Integer hourEnd;

	@Column(name = "minuteEnd", nullable = false)
	private Integer minuteEnd;

	@Column(name = "times", nullable = false)
	private Integer times;

	@Column(name = "deleted", nullable = false)
	private Boolean deleted;
}
