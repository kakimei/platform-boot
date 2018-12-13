package com.platform.bo.output.service;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.Date;

public interface OutPutService {

	HSSFWorkbook outputExcelByReserveDate(Date reserveDate);

	HSSFWorkbook outputExcelByReserveDateTime(Date reserveDate, String timeString);

	HSSFWorkbook outputExcelAll();
}
