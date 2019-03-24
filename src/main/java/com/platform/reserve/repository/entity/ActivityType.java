package com.platform.reserve.repository.entity;

public enum ActivityType {

	TEAM, SINGLE;

	public boolean isTeam(){
		return TEAM.equals(this);
	}
}
