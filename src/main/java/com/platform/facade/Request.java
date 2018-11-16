package com.platform.facade;

import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
public class Request<E> {

	private E entity;

}
