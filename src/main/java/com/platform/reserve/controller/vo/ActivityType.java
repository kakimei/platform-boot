package com.platform.reserve.controller.vo;

public enum ActivityType {

	TEAM, SINGLE;

	public boolean isTeam(){
		return TEAM.equals(this);
	}
}
