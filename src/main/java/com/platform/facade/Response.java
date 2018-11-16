package com.platform.facade;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@Getter
public class Response<E> {

	private ResponseType responseType;

	private E entity;

	private String errMsg;

}
