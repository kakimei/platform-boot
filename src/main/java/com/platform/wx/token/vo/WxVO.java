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
	private String openid;

	private String nickname;

	private String sex;

	private String province;

	private String city;

	private String country;

	private String headimgurl;
}
