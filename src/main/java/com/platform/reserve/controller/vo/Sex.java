package com.platform.reserve.controller.vo;

import lombok.Getter;

@Getter
public enum Sex {
	MALE("男"), FEMALE("女");

	private String displayName;
	Sex(String displayName) {
		this.displayName = displayName;
	}
}
