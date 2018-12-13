package com.platform.bo.userinfo.repository.entity;

public enum RoleType {
	ADMIN("管理人员"), OPERATOR("核销人员");

	private String description;

	public String getDescription() {
		return description;
	}

	RoleType(String description) {
		this.description = description;
	}

	public boolean canModifyReservation(){
		return ADMIN.equals(this);
	}

	public boolean canDownloadReservation(){
		return ADMIN.equals(this);
	}

	public boolean canModifyBoUser(){
		return ADMIN.equals(this);
	}

	public boolean canSeeAllBoUser(){
		return ADMIN.equals(this);
	}
}
