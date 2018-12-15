package com.platform.bo.output.service;

import com.platform.bo.output.service.annotation.ExcelColumn;
import com.platform.bo.output.service.annotation.ExcelObject;
import com.platform.bo.output.service.dto.ReservationExcelDTO;
import com.platform.reserve.controller.vo.ActivityType;
import com.platform.reserve.controller.vo.Sex;
import com.platform.reserve.service.ReservationInfoService;
import com.platform.reserve.service.ReserveDtoTransferBuilder;
import com.platform.reserve.service.dto.ReservationInfoDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OutPutServiceImpl implements OutPutService {

	@Autowired
	private ReservationInfoService reservationInfoService;

	@Autowired
	private ReserveDtoTransferBuilder reserveDtoTransferBuilder;

	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public HSSFWorkbook outputExcelByReserveDate(Date reserveDate) {
		if (reserveDate == null) {
			log.warn("reserve date is null, output all reservation info.");
			return outputExcelAll();
		}
		List<ReservationInfoDto> reservationInfoDtoList = reservationInfoService.findReservationInfoByDate(reserveDate);
		return outputExcelForReservation(reservationInfoDtoList);
	}

	@Override
	public HSSFWorkbook outputExcelByReserveDateTime(Date reserveDate, String timeString) {
		return null;
	}

	@Override
	public HSSFWorkbook outputExcelAll() {
		List<ReservationInfoDto> reservationInfoDtoList = reservationInfoService.findAllActiveReservationInfo();
		return outputExcelForReservation(reservationInfoDtoList);
	}

	private HSSFWorkbook outputExcelForReservation(List<ReservationInfoDto> reservationInfoDtoList) {
		if (CollectionUtils.isEmpty(reservationInfoDtoList)) {
			return getReservationExcelPlatform(null, "预约表", ReservationExcelDTO.class);
		}
		HSSFWorkbook result = null;
		Map<Date, List<ReservationInfoDto>> reservationMap = reservationInfoDtoList.stream().collect(
			Collectors.groupingBy(ReservationInfoDto::getReserveDate));
		for(Map.Entry<Date, List<ReservationInfoDto>> entry : reservationMap.entrySet()){
			Date reserveDate = entry.getKey();
			List<ReservationInfoDto> reservationInfoDtos = entry.getValue();
			List<ReservationExcelDTO> excelData = reservationInfoDtos.stream().map(reservationInfoDto -> {
				ReservationExcelDTO reservationExcelDTO = new ReservationExcelDTO();
				reservationExcelDTO.setActivityType(ActivityType.valueOf(reservationInfoDto.getActivityType().name()));
				reservationExcelDTO.setAge(reservationInfoDto.getAge());
				reservationExcelDTO.setSex(Sex.valueOf(reservationInfoDto.getSex().name()));
				reservationExcelDTO.setLinkManName(reservationInfoDto.getLinkManName());
				reservationExcelDTO.setPeopleCount(reservationInfoDto.getPeopleCount());
				reservationExcelDTO.setPhoneNumber(reservationInfoDto.getPhoneNumber());
				reservationExcelDTO.setReserveDay(reservationInfoDto.getReserveDate());
				reservationExcelDTO.setTimeString(reserveDtoTransferBuilder.buildTimeString(
					reservationInfoDto.getReserveBeginHH(),
					reservationInfoDto.getReserveBeginMM(),
					reservationInfoDto.getReserveEndHH(),
					reservationInfoDto.getReserveEndMM()));
				return reservationExcelDTO;
			}).collect(Collectors.toList());
			result = buildWorkBook(result, reserveDate, excelData);
		}
		return result;
	}

	private HSSFWorkbook buildWorkBook(HSSFWorkbook wb, Date reserveDate, List<ReservationExcelDTO> reservationExcelDTOList) {
		ExcelObject excelObject = ReservationExcelDTO.class.getDeclaredAnnotation(ExcelObject.class);
		String sheetName = SDF.format(reserveDate);
		HSSFWorkbook workbook = getReservationExcelPlatform(wb, sheetName, ReservationExcelDTO.class);
		HSSFCell cell;
		HSSFCellStyle style = workbook.createCellStyle();
		style.setAlignment(HorizontalAlignment.LEFT);
		for (int i = 0; i < reservationExcelDTOList.size(); i++) {
			int rowIndex = i + 1;
			ReservationExcelDTO reservationExcelDTO = reservationExcelDTOList.get(i);
			HSSFSheet sheet = workbook.getSheet(sheetName);
			HSSFRow row = sheet.createRow(rowIndex);
			cell = row.createCell(0);
			cell.setCellValue(reservationExcelDTO.getLinkManName());
			cell.setCellStyle(style);

			cell = row.createCell(1);
			cell.setCellValue(reservationExcelDTO.getSex().getDisplayName());
			cell.setCellStyle(style);

			cell = row.createCell(2);
			cell.setCellValue(reservationExcelDTO.getAge());
			cell.setCellStyle(style);

			cell = row.createCell(3);
			cell.setCellValue(reservationExcelDTO.getPhoneNumber());
			cell.setCellStyle(style);

			cell = row.createCell(4);
			cell.setCellValue(SDF.format(reservationExcelDTO.getReserveDay()));
			cell.setCellStyle(style);

			cell = row.createCell(5);
			cell.setCellValue(reservationExcelDTO.getTimeString());
			style.setAlignment(HorizontalAlignment.LEFT);
			cell.setCellStyle(style);

			cell = row.createCell(6);
			cell.setCellValue(reservationExcelDTO.getPeopleCount());
			cell.setCellStyle(style);

			cell = row.createCell(7);
			cell.setCellValue(reservationExcelDTO.getActivityType().getDisplayName());
			cell.setCellStyle(style);
		}
		return workbook;
	}

	private HSSFWorkbook getReservationExcelPlatform(HSSFWorkbook wb, String sheetName, Class clazz) {
		ExcelObject excelObject = (ExcelObject) clazz.getDeclaredAnnotation(ExcelObject.class);
		if (excelObject == null) {
			return null;
		}
		if(wb == null) {
			wb = new HSSFWorkbook();
		}
		HSSFSheet sheet = wb.createSheet(sheetName);
		HSSFRow row = sheet.createRow(0);
		HSSFCellStyle style = wb.createCellStyle();
		HSSFCell cell = null;
		Field[] allFields = clazz.getDeclaredFields();
		int index = 0;
		for (Field field : allFields) {
			ExcelColumn excelColumn = field.getDeclaredAnnotation(ExcelColumn.class);
			if (excelColumn == null) {
				continue;
			}
			cell = row.createCell(index);
			cell.setCellValue(excelColumn.columnName());
			style.setAlignment(excelColumn.columnStyle());
			cell.setCellStyle(style);
			index++;
		}
		return wb;
	}
}
