package com.platform.module.element;

import com.platform.module.visitor.Visitor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Teacher implements Element{

	private String name; // 教师姓名
	private int score; // 评价分数
	private int paperCount; // 论文数

	@Override
	public void accept(Visitor visitor) {
		visitor.visit(this);
	}
}
