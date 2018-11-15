package com.platform.module.element;

import com.platform.module.visitor.Visitor;

public interface Element {

	//接受一个抽象访问者访问
	void accept(Visitor visitor);
}
