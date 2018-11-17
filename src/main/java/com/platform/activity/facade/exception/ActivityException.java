package com.platform.activity.facade.exception;

public class ActivityException extends Exception{
	private String errorMsg;

	public ActivityException(String errorMsg, Throwable cause) {
		super(errorMsg, cause);
		this.errorMsg = errorMsg;
	}

	public ActivityException(String errorMsg){
		super(errorMsg);
		this.errorMsg = errorMsg;
	}
}
