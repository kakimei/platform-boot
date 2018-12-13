package com.platform.bo.output.service.dto;

import com.platform.bo.output.service.annotation.ExcelColumn;
import com.platform.bo.output.service.annotation.ExcelObject;
import com.platform.reserve.controller.vo.ActivityType;
import com.platform.reserve.controller.vo.Sex;
import lombok.Data;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.util.Date;

@Data
@ExcelObject(sheetName = "预约信息")
public class ReservationExcelDTO {
	@ExcelColumn(columnName = "联系人", columnStyle = HorizontalAlignment.LEFT)
	private String linkManName;
	@ExcelColumn(columnName = "性别", columnStyle = HorizontalAlignment.LEFT)
	private Sex sex;
	@ExcelColumn(columnName = "年龄", columnStyle = HorizontalAlignment.LEFT)
	private Integer age;
	@ExcelColumn(columnName = "电话", columnStyle = HorizontalAlignment.LEFT)
	private String phoneNumber;
	@ExcelColumn(columnName = "预约日", columnStyle = HorizontalAlignment.LEFT)
	private Date reserveDay;
	@ExcelColumn(columnName = "预约时间", columnStyle = HorizontalAlignment.LEFT)
	private String timeString;
	@ExcelColumn(columnName = "预约人数", columnStyle = HorizontalAlignment.LEFT)
	private Integer peopleCount;
	@ExcelColumn(columnName = "类型", columnStyle = HorizontalAlignment.LEFT)
	private ActivityType activityType;
}
