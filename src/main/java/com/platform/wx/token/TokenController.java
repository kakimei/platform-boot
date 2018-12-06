package com.platform.wx.token;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@RestController
@RequestMapping(path = "/token")
public class TokenController {

	private static final String TOKEN = "yangpu";

	@RequestMapping(path = "/confirm", method = RequestMethod.GET, produces = "text/plain; charset=utf-8")
	public String confirmToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){
		// 微信加密签名
		String signature = httpServletRequest.getParameter("signature");
		// 随机字符串
		String echostr = httpServletRequest.getParameter("echostr");
		// 时间戳
		String timestamp = httpServletRequest.getParameter("timestamp");
		// 随机数
		String nonce = httpServletRequest.getParameter("nonce");

		String[] str = { TOKEN, timestamp, nonce };
		Arrays.sort(str); // 字典序排序
		String bigStr = str[0] + str[1] + str[2];
		// SHA1加密
		String digest = DigestUtils.sha1Hex(bigStr.getBytes()).toLowerCase();

		// 确认请求来至微信
		if (digest.equals(signature)) {
			return echostr;
		}
		return "";
	}
}
