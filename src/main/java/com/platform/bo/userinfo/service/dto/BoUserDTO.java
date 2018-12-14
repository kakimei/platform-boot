package com.platform.bo.userinfo.service.dto;

import com.platform.bo.userinfo.repository.entity.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoUserDTO {

	private String boUserName;

	private String token;

	private RoleType roleType;
}
