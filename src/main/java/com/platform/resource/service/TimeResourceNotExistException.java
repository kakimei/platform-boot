package com.platform.resource.service;

import lombok.Getter;

@Getter
public class TimeResourceNotExistException extends Exception{

    private String errorMsg;

    public TimeResourceNotExistException(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
