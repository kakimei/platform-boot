package com.platform.reserve.controller;

import com.platform.reserve.controller.vo.ReserveVO;
import com.platform.facade.Request;
import com.platform.facade.Response;
import com.platform.reserve.facade.ReserveFacade;
import com.platform.resource.service.dto.TimeResourceDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping(path = {"/reserve", "/bo/reserve"})
public class ReserveController {

	private static final String BO_URI = "/platform/bo";
	@Autowired
	private ReserveFacade reserveFacade;

	@Value("#{environment['people.number.threshold']}")
	protected int PEOPLE_NUMBER_THRESHOLD;

	@RequestMapping(method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public @ResponseBody
	Boolean reserve(@RequestBody ReserveVO reserveVO, HttpServletRequest httpServletRequest) {
		if (reserveVO == null) {
			return false;
		}
		reserveVO.setPeopleNumberThreshold(PEOPLE_NUMBER_THRESHOLD);
		if (!reserveVO.canReserve()) {
			return false;
		}
		reserveVO.setUserName((String) httpServletRequest.getAttribute("user"));
		Request<ReserveVO> request = Request.<ReserveVO>builder().entity(reserveVO).build();
		Response<ReserveVO> response = reserveFacade.reserve(request);
		return response.getResponseType().isSuccess();
	}

	@RequestMapping(path = "/update", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public @ResponseBody
	Boolean update(@RequestBody ReserveVO reserveVO, HttpServletRequest httpServletRequest) {
		if (reserveVO == null) {
			return false;
		}
		reserveVO.setPeopleNumberThreshold(PEOPLE_NUMBER_THRESHOLD);
		if (!reserveVO.canReserve()) {
			return false;
		}
		Request<ReserveVO> request = Request.<ReserveVO>builder().entity(reserveVO).build();
		Response<ReserveVO> response = reserveFacade.update(request);
		return response.getResponseType().isSuccess();
	}

	@RequestMapping(path = "/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public @ResponseBody
	List<ReserveVO> list(HttpServletRequest httpServletRequest) {
		String user = (String) httpServletRequest.getAttribute("user");
		if (StringUtils.isBlank(user)) {
			return new ArrayList<>();
		}
		ReserveVO entity = new ReserveVO();
		entity.setUserName(user);
		Request<ReserveVO> request = Request.<ReserveVO>builder().entity(entity).build();
		Response<List<ReserveVO>> result = reserveFacade.getReservationListByUserName(request);
		if (result.getResponseType().isSuccess()) {
			log.info("get reservation list success. username : {}", user);
			return result.getEntity();
		}
		log.error("get reservation list failed. username : {}, cause : {}", user, result.getErrMsg());
		return new ArrayList<>();
	}

	@RequestMapping(path = "/mySignInlist", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public @ResponseBody
	List<ReserveVO> mySignInlist(HttpServletRequest httpServletRequest) {
		String user = (String) httpServletRequest.getAttribute("user");
		if (StringUtils.isBlank(user)) {
			return new ArrayList<>();
		}
		ReserveVO entity = new ReserveVO();
		entity.setUserName(user);
		Request<ReserveVO> request = Request.<ReserveVO>builder().entity(entity).build();
		Response<List<ReserveVO>> result = reserveFacade.getReservationListBySignIn(request);
		if (result.getResponseType().isSuccess()) {
			log.info("get reservation list success. username : {}", user);
			return result.getEntity();
		}
		log.error("get reservation list failed. username : {}, cause : {}", user, result.getErrMsg());
		return new ArrayList<>();
	}

	@RequestMapping(path = "/alllist", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public @ResponseBody
	List<ReserveVO> allList() {
		Response<List<ReserveVO>> result = reserveFacade.getActiveReservationList();
		if (result.getResponseType().isSuccess()) {
			log.info("get reservation list success.");
			return result.getEntity();
		}
		log.error("get reservation list failed. cause : {}", result.getErrMsg());
		return new ArrayList<>();
	}

	@RequestMapping(path = "/{reservationDate}/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public @ResponseBody
	List<ReserveVO> listForReservationDate(@PathVariable("reservationDate") @DateTimeFormat(pattern="yyyy-MM-dd") Date reservationDate) {
		if(reservationDate == null){
			return new ArrayList<>();
		}
		ReserveVO reserveVO = new ReserveVO();
		reserveVO.setReserveDay(reservationDate);
		Request<ReserveVO> request = Request.<ReserveVO>builder().entity(reserveVO).build();
		Response<List<ReserveVO>> result = reserveFacade.getReservationListByReserveDate(request);
		if (result.getResponseType().isSuccess()) {
			log.info("get reservation list success.");
			return result.getEntity();
		}
		log.error("get reservation list failed. cause : {}", result.getErrMsg());
		return new ArrayList<>();
	}

	@RequestMapping(path = "/modify", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public @ResponseBody
	ReserveVO modify(HttpServletRequest httpServletRequest) {
		if(isFromBoRequest(httpServletRequest)){
			return getReservationInfoByBOUser(httpServletRequest);
		}
		String user = (String) httpServletRequest.getAttribute("user");
		Long reservationInfoId = Long.valueOf(httpServletRequest.getParameter("reservationInfoId"));
		if (StringUtils.isBlank(user) || reservationInfoId == null || reservationInfoId == 0) {
			return null;
		}
		ReserveVO reserveVO = new ReserveVO();
		reserveVO.setUserName(user);
		reserveVO.setReservationInfoId(reservationInfoId);
		Request<ReserveVO> request = Request.<ReserveVO>builder().entity(reserveVO).build();
		Response<ReserveVO> reserveVOResponse = reserveFacade.findByReservationInfoIdAndUser(request);
		if (reserveVOResponse.getResponseType().isSuccess()) {
			return reserveVOResponse.getEntity();
		}
		return null;
	}

	private ReserveVO getReservationInfoByBOUser(HttpServletRequest httpServletRequest){
		Long reservationInfoId = Long.valueOf(httpServletRequest.getParameter("reservationInfoId"));
		if (reservationInfoId == null || reservationInfoId == 0) {
			return null;
		}
		ReserveVO reserveVO = new ReserveVO();
		reserveVO.setReservationInfoId(reservationInfoId);
		Request<ReserveVO> request = Request.<ReserveVO>builder().entity(reserveVO).build();
		Response<ReserveVO> reserveVOResponse = reserveFacade.findByReservationInfoId(request);
		if (reserveVOResponse.getResponseType().isSuccess()) {
			return reserveVOResponse.getEntity();
		}
		return null;
	}

	@RequestMapping(path = "/cancel", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public @ResponseBody
	ReserveVO cancel(@RequestBody ReserveVO reserveVO, HttpServletRequest httpServletRequest) {
		if(isFromBoRequest(httpServletRequest)){
			return cancelByBOUser(reserveVO);
		}
		String user = (String) httpServletRequest.getAttribute("user");
		Long reservationInfoId = reserveVO.getReservationInfoId();
		if (StringUtils.isBlank(user) || reservationInfoId == null || reservationInfoId == 0) {
			return null;
		}
		reserveVO.setUserName(user);
		Request<ReserveVO> request = Request.<ReserveVO>builder().entity(reserveVO).build();
		Response<ReserveVO> reserveVOResponse = reserveFacade.cancel(request);
		if (reserveVOResponse.getResponseType().isSuccess()) {
			return reserveVOResponse.getEntity();
		}
		return null;
	}

	private ReserveVO cancelByBOUser(ReserveVO reserveVO){
		Long reservationInfoId = reserveVO.getReservationInfoId();
		if (reservationInfoId == null || reservationInfoId == 0) {
			return null;
		}
		Request<ReserveVO> request = Request.<ReserveVO>builder().entity(reserveVO).build();
		Response<ReserveVO> reserveVOResponse = reserveFacade.cancelByBOUser(request);
		if (reserveVOResponse.getResponseType().isSuccess()) {
			return reserveVOResponse.getEntity();
		}
		return null;
	}

	@RequestMapping(path = "/team/validDateTime", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public @ResponseBody
	List<Map.Entry<String, List<TimeResourceDto.TimeDTO>>> getTeamValidDateTime() {
		Response<ReserveVO> reserveVOResponse = reserveFacade.getTeamValidDateTime();
		if (reserveVOResponse.getResponseType().isSuccess()) {
			return reserveVOResponse.getEntity().getResourceList();
		}
		return null;
	}

	@RequestMapping(path = "/single/validDateTime", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	public @ResponseBody
	List<Map.Entry<String, List<TimeResourceDto.TimeDTO>>> getSingleValidDateTime() {
		Response<ReserveVO> reserveVOResponse = reserveFacade.getSingleValidDateTime();
		if (reserveVOResponse.getResponseType().isSuccess()) {
			return reserveVOResponse.getEntity().getResourceList();
		}
		return null;
	}

	private boolean isFromBoRequest(HttpServletRequest httpServletRequest){
		return httpServletRequest.getRequestURI().startsWith(BO_URI);
	}
}
