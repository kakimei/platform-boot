package com.platform.module.element;

import com.platform.module.visitor.Visitor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Student implements Element{

	private String name; // 学生姓名
	private int grade; // 成绩
	private int paperCount; // 论文数

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
