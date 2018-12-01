package com.platform.reserve.controller.vo;

import lombok.Getter;

@Getter
public enum ActivityType {

	TEAM("团队"), SINGLE("个人");

	private String displayName;
	ActivityType(String displayName) {
		this.displayName = displayName;
	}

	public boolean isTeam(){
		return TEAM.equals(this);
	}
}
