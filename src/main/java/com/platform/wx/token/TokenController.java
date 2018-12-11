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

@RestController
@RequestMapping(path = "/weixin")
@Slf4j
public class TokenController {

	private static final String WEIXIN_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";

	private static final String WEIXIN_APP_ID = "wx22d16b90c898f61d";

	private static final String WEIXIN_APP_SECRET = "ebf24940e789946ef53b05a70391c95b";

	private static final Integer RESPONSE_OK = 200;

	private static final String WEIXIN_USER_INFO_URL = "https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";

	private static final String WEIXIN_REFRESH_ACCESS_TOKEN = "https://api.weixin.qq.com/sns/oauth2/refresh_token";

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
				String accessToken = jsonObject.getString("access_token");
				log.info("access token : {}", accessToken);
				String openId = jsonObject.getString("openid");
				log.info("open id : {}", openId);
				String refreshAccessToken = jsonObject.getString("refresh_token");
				httpResponse = requestAccessToken(httpCilent, accessToken, openId, refreshAccessToken);
				srtResult = EntityUtils.toString(httpResponse.getEntity());
				log.info("---------"+srtResult);
				jsonObject = new JSONObject(srtResult);
				return buildWxVO(jsonObject);
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

	private WxVO buildWxVO(JSONObject jsonObject){
		return WxVO.builder()
			.openid(jsonObject.getString("openid"))
			.nickname(jsonObject.getString("nickname"))
			.sex(jsonObject.getString("sex"))
			.province(jsonObject.getString("province"))
			.city(jsonObject.getString("city"))
			.country(jsonObject.getString("country"))
			.headimgurl(jsonObject.getString("headimgurl"))
			.build();
	}

	private HttpResponse requestAccessToken(CloseableHttpClient httpCilent, String accessToken, String openId, String refreshToken)
		throws IOException {
		String userInfoUrl = WEIXIN_USER_INFO_URL + "?access_token=" + accessToken + "&openid=" + openId + "&lang=zh_CN";
		HttpResponse httpResponse = httpCilent.execute(new HttpGet(userInfoUrl));
		log.info("==========="+httpResponse.getEntity().toString());
		String srtResult = EntityUtils.toString(httpResponse.getEntity());
		log.info("---------"+srtResult);
		JSONObject jsonObject = new JSONObject(srtResult);
		if(jsonObject.getInt("errcode") == 40001){
			String refreshTokenUrl = WEIXIN_REFRESH_ACCESS_TOKEN + "?appid=" + WEIXIN_APP_ID + "&grant_type=refresh_token&refresh_token=" + refreshToken;
			httpResponse = httpCilent.execute(new HttpGet(refreshTokenUrl));
			srtResult = EntityUtils.toString(httpResponse.getEntity());
			jsonObject = new JSONObject(srtResult);
			return requestAccessToken(httpCilent, jsonObject.getString("access_token"), jsonObject.getString("openid"), jsonObject.getString("refresh_token"));
		}
		return httpResponse;
	}
}
