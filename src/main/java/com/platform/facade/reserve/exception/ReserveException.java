package com.platform.facade.reserve.exception;


public class ReserveException extends Exception{

	private String errorMsg;

	public ReserveException(String errorMsg, Throwable cause) {
		super(errorMsg, cause);
		this.errorMsg = errorMsg;
	}

	public ReserveException(String errorMsg){
		super(errorMsg);
		this.errorMsg = errorMsg;
	}
}
