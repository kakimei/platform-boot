package com.platform.bo.userinfo.repository.entity;

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
@Table(name = "bo_user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BoUser {

	@Id
	@Column(name = "bo_user_id", unique = true)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long boUserId;

	@Column(name = "bo_user_name", nullable = false)
	private String boUserName;

	@Column(name = "bo_password", nullable = false)
	private String boPassword;

	@Column(name = "bo_role_type", nullable = false)
	@Enumerated(value = EnumType.STRING)
	private RoleType roleType;

	@Column(name = "active", nullable = false, columnDefinition = "BIT")
	private Boolean active;
}
