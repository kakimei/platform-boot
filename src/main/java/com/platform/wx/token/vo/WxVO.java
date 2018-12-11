package com.platform.wx.token.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Builder
@Data
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class WxVO {
	private String access_token;

	private String expires_in;

	private String refresh_token;

	private String openid;

	private String scope;
}
