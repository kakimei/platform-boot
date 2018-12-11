package com.platform.wx.token;

import com.platform.wx.token.vo.WxVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;

@RestController
@RequestMapping(path = "/weixin")
@Slf4j
public class TokenController {

	private static final String WEIXIN_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";

	private static final String WEIXIN_APP_ID = "wx22d16b90c898f61d";

	private static final String WEIXIN_APP_SECRET = "ebf24940e789946ef53b05a70391c95b";

	private static final Integer RESPONSE_OK = 200;
	@RequestMapping(path = "/userInfo/get", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public @ResponseBody
	WxVO confirmToken(@RequestParam String code){
		CloseableHttpClient httpCilent = HttpClients.createDefault();
		String url = WEIXIN_ACCESS_TOKEN_URL + "?appid=" + WEIXIN_APP_ID + "&secret=" + WEIXIN_APP_SECRET + "&code=" + code + "&grant_type=authorization_code";
		HttpGet httpGet = new HttpGet(url);
		try {
			HttpResponse httpResponse = httpCilent.execute(httpGet);
			if(httpResponse.getStatusLine().getStatusCode() == RESPONSE_OK){
				String srtResult = EntityUtils.toString(httpResponse.getEntity());
				log.info(srtResult);
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		}finally {
			try {
				httpCilent.close();
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
		return null;
	}
}
