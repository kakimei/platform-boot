package com.platform.facade;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@Getter
@AllArgsConstructor
public class Request<E> {

	private E entity;

}
