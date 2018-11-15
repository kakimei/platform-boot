package com.platform.controller;

import com.platform.module.ObjectStructure;
import com.platform.module.element.Element;
import com.platform.module.element.Student;
import com.platform.module.element.Teacher;
import com.platform.module.visitor.GradeSelection;
import com.platform.module.visitor.ResearcherSelection;
import com.platform.module.visitor.Visitor;

public class VisitorClient {

	public static void main(String[] args) {
		// 初始化元素
		Element stu1 = new Student("Student Jim", 92, 3);
		Element stu2 = new Student("Student Ana", 89, 1);
		Element t1 = new Teacher("Teacher Mike", 83, 10);
		Element t2 = new Teacher("Teacher Lee", 88, 7);
		// 初始化对象结构
		ObjectStructure objectStructure = new ObjectStructure();
		objectStructure.addElement(stu1);
		objectStructure.addElement(stu2);
		objectStructure.addElement(t1);
		objectStructure.addElement(t2);
		// 定义具体访问者，选拔成绩优秀者
		Visitor gradeSelection = new GradeSelection();
		// 具体的访问操作，打印输出访问结果
		objectStructure.accept(gradeSelection);
		System.out.println("----结构不变，操作易变----");
		// 数据结构是没有变化的，如果我们还想增加选拔科研优秀者的操作，那么如下。
		Visitor researcherSelection = new ResearcherSelection();
		objectStructure.accept(researcherSelection);
	}
}
