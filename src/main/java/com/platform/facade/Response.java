package com.platform.facade;

import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
public class Response<E> {

	private ResponseType responseType;

	private E entity;
}
