package com.platform.bo.output.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class OutPutVO {

	private Date reserveDate;
	private String timeString;
}
