package com.platform.wx.token;

import com.platform.wx.token.vo.WxVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static com.platform.wx.cache.WxCacheServiceImpl.WEIXIN_APP_ID;
import static com.platform.wx.cache.WxCacheServiceImpl.WEIXIN_APP_SECRET;

@RestController
@RequestMapping(path = "/weixin")
@Slf4j
public class TokenController {

	private static final String WEIXIN_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";

	private static final Integer RESPONSE_OK = 200;

	@RequestMapping(path = "/userInfo/get", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public @ResponseBody
	WxVO confirmToken(@RequestParam String code) {
		CloseableHttpClient httpCilent = HttpClients.createDefault();
		String accessTokenRrl =
			WEIXIN_ACCESS_TOKEN_URL + "?appid=" + WEIXIN_APP_ID + "&secret=" + WEIXIN_APP_SECRET + "&code=" + code + "&grant_type=authorization_code";

		HttpGet httpGet = new HttpGet(accessTokenRrl);
		try {
			HttpResponse httpResponse = httpCilent.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == RESPONSE_OK) {
				String srtResult = EntityUtils.toString(httpResponse.getEntity());
				JSONObject jsonObject = new JSONObject(srtResult);
				return WxVO.builder()
					.openid(jsonObject.getString("openid"))
					.access_token(jsonObject.getString("access_token"))
					.expires_in(jsonObject.getInt("expires_in"))
					.refresh_token(jsonObject.getString("refresh_token"))
					.scope(jsonObject.getString("scope"))
					.build();
			}
		} catch (IOException e) {
			log.error(e.getMessage());
		} finally {
			try {
				httpCilent.close();
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
		return null;
	}
}
