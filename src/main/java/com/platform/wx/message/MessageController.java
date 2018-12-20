package com.platform.wx.message;

import com.platform.reserve.controller.vo.ActivityType;
import com.platform.reserve.service.ReservationInfoService;
import com.platform.reserve.service.dto.ReservationInfoDto;
import com.platform.resource.service.TimeResourceService;
import com.platform.wx.cache.WxCacheServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;

@RestController
@RequestMapping(path = "/weixin/message")
@Slf4j
public class MessageController {

	private static final String CONFIRM = "confirm";
	private static final String CANCEL = "cancel";
	private static final Integer RESPONSE_OK = 200;
	private static final String SEND_WX_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/template/subscribe?access_token=";

	private static final String RESERVATION_DETAIL = "http://yp.compass-trip.com/reservationModify.html?reservationInfoId=";

	private static final String MESSAGE_TITLE = "恭喜您，预定成功！";

	private static final String MESSAGE_CONTENT = "预定信息：\n联系人:%s\n预定类型:%s\n预定人数:%s\n联系人电话:%s\n预定日:%s\n预定时间段:%s";

	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
	@Autowired
	private WxCacheServiceImpl wxCacheService;

	@Autowired
	private ReservationInfoService reservationInfoService;
	@Autowired
	private TimeResourceService timeResourceService;

	@RequestMapping(path = "/send", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public void sendWxOneTimeTemplateMessage(
		@RequestParam(name = "openid") String openId,
		@RequestParam(name = "template_id") String templateId,
		@RequestParam(name = "action") String action,
		@RequestParam(name = "scene") String scene,
		@RequestParam(name = "reservationInfoId") String reservationInfoId) {
		if(CONFIRM.equals(action)){

			String sendWxMessageUrl = SEND_WX_MESSAGE_URL + wxCacheService.getCommonAccessToken();
			HttpPost httpPost = new HttpPost(sendWxMessageUrl);
			httpPost.setHeader("Content-type", "application/json");
			String clickUrl = RESERVATION_DETAIL + reservationInfoId;
			try(CloseableHttpClient httpCilent = HttpClients.createDefault()) {
				HttpEntity httpEntity = buildEntity(openId, templateId, clickUrl, scene, MESSAGE_TITLE, buildContent(reservationInfoId), "BLUE");
				httpPost.setEntity(httpEntity);
				HttpResponse httpResponse = httpCilent.execute(httpPost);
				if (httpResponse.getStatusLine().getStatusCode() == RESPONSE_OK) {
					String srtResult = EntityUtils.toString(httpResponse.getEntity());
					JSONObject jsonObject = new JSONObject(srtResult);
					int errcode = jsonObject.getInt("errcode");
					if(errcode == 0){
						log.info("send wx message success.");
					}else{
						log.warn("send wx message failed!");
					}
				}
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}

	}

	private String buildContent(String reservationInfoId) throws Exception {
		if(StringUtils.isBlank(reservationInfoId)){
			throw new Exception("reservation id is null.");
		}
		ReservationInfoDto reservationInfoDto = reservationInfoService.findReservationInfoById(Long.valueOf(reservationInfoId));
		if(reservationInfoDto == null){
			throw new Exception("reservation does not exist. reservation id: " + reservationInfoId);
		}
		return String.format(MESSAGE_CONTENT,
			reservationInfoDto.getLinkManName(),
			ActivityType.valueOf(reservationInfoDto.getActivityType().name()).getDisplayName(),
			String.valueOf(reservationInfoDto.getPeopleCount()),
			reservationInfoDto.getPhoneNumber(),
			SDF.format(reservationInfoDto.getReserveDate()),timeResourceService.getFormatTimeString(
				reservationInfoDto.getReserveBeginHH(),
				reservationInfoDto.getReserveBeginMM(),
				reservationInfoDto.getReserveEndHH(),
				reservationInfoDto.getReserveEndMM()));
	}

	private HttpEntity buildEntity(String openId, String templateId, String url, String scene, String title, String value, String color) {
		JSONObject json = new JSONObject();
		json.put("touser", openId);
		json.put("template_id", templateId);
		json.put("url", url);
		json.put("scene", scene);
		json.put("title", title);
		json.put("data", buildData(value, color));
		StringEntity s = new StringEntity(json.toString(),"UTF-8");
		return s;
	}

	private String buildData(String value, String color){
		JSONObject json = new JSONObject();
		json.put("content", buildDataDetail(value, color));
		return json.toString();
	}

	private String buildDataDetail(String value, String color){
		JSONObject json = new JSONObject();
		json.put("value", value);
		json.put("color", color);
		return json.toString();
	}
}
