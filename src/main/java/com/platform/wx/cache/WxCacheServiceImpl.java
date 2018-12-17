package com.platform.wx.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class WxCacheServiceImpl {

	private String access_token = null;

	public static final String WEIXIN_APP_ID = "wx22d16b90c898f61d";

	public static final String WEIXIN_APP_SECRET = "ebf24940e789946ef53b05a70391c95b";

	private static final String COMMON_ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";

	private static final Integer RESPONSE_OK = 200;

	@Scheduled(initialDelay = 1L, fixedDelay = 7000000L)
	public void initAccessToken(){
		CloseableHttpClient httpCilent = HttpClients.createDefault();
		String commonAccessToken = COMMON_ACCESS_TOKEN_URL + "&appid=" + WEIXIN_APP_ID + "&secret=" + WEIXIN_APP_SECRET;

		HttpGet httpGet = new HttpGet(commonAccessToken);
		try {
			HttpResponse httpResponse = httpCilent.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() == RESPONSE_OK) {
				String srtResult = EntityUtils.toString(httpResponse.getEntity());
				JSONObject jsonObject = new JSONObject(srtResult);
				String accessToken = jsonObject.getString("access_token");
				synchronized (access_token){
					access_token = accessToken;
				}
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
	}

	public String getCommonAccessToken(){
		return access_token;
	}
}
