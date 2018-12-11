package com.platform.user.repository.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User {

	@Id
	@Column(name = "user_id", unique = true)
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long userId;

	@Column(name = "user_name", nullable = false)
	private String userName;

	@Column(name = "password", nullable = true)
	private String password;

	@Column(name = "active", nullable = false, columnDefinition = "BIT")
	private Boolean active;
}
