package com.platform.facade;

public enum ResponseType {
	SUCCESS, FAIL;

	public boolean isSuccess(){
		return SUCCESS.equals(this);
	}
}
