package com.platform.bo.output.controller;

import com.platform.bo.output.controller.vo.OutPutVO;
import com.platform.bo.output.service.OutPutService;
import com.platform.bo.userinfo.repository.entity.BoUser;
import com.platform.bo.userinfo.service.BoUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping(path = "/bo/output")
@Slf4j
public class OutPutController {

	@Autowired
	private OutPutService outPutService;

	@Autowired
	private BoUserService boUserService;

	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
	@RequestMapping(path = "/reservation/excel/date", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public void outputExcel(@RequestParam @DateTimeFormat(pattern="yyyy-MM-dd") Date reserveDate, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
//		String boUserName = (String) httpServletRequest.getAttribute("boUser");
//		BoUser boUser = boUserService.findByName(boUserName);
//		if (!boUser.getRoleType().canDownloadReservation()) {
//			return;
//		}
		if (reserveDate == null) {
			return;
		}

		try(HSSFWorkbook workbook = outPutService.outputExcelByReserveDate(reserveDate)) {
			setResponseHeader(httpServletResponse, "预约信息一览" + SDF.format(reserveDate) + ".xls");
 			OutputStream os = httpServletResponse.getOutputStream();
			workbook.write(os);
			os.flush();
			os.close();
		} catch (Exception e) {
			log.error("download error. {}", e.getMessage());
		}
	}

	private void setResponseHeader(HttpServletResponse response, String fileName) {
		try {
			fileName = new String(fileName.getBytes(),"ISO8859-1");
			response.setContentType("application/octet-stream;charset=ISO8859-1");
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			response.addHeader("Pargam", "no-cache");
			response.addHeader("Cache-Control", "no-cache");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
