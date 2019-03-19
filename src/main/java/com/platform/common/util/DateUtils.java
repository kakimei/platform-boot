package com.platform.common.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class DateUtils {

	public String getFormatDateString(LocalDate localDate, String format){
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
		return localDate.format(dateTimeFormatter);
	}
}
