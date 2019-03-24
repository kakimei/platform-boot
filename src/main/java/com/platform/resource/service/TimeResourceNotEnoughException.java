package com.platform.resource.service;

import lombok.Getter;

@Getter
public class TimeResourceNotEnoughException extends Exception{

    private String errorMsg;

    public TimeResourceNotEnoughException(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
