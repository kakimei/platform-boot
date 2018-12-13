package com.platform.bo.userinfo.controller.vo;

import com.platform.bo.userinfo.repository.entity.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class BoUserVO {

	private String boUserName;
	private String oldBoUserPass;
	private String boUserPass;
	private RoleType roleType;
}
