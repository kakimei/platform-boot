package com.platform.module.visitor;

import com.platform.module.element.Student;
import com.platform.module.element.Teacher;

public interface Visitor {

	void visit(Student element);

	void visit(Teacher element);

}
