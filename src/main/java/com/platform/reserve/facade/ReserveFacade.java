package com.platform.reserve.facade;

import com.platform.activity.service.ActivityService;
import com.platform.common.util.MailService;
import com.platform.reserve.controller.vo.ReserveVO;
import com.platform.facade.Request;
import com.platform.facade.Response;
import com.platform.facade.ResponseType;
import com.platform.reserve.service.ReservationInfoService;
import com.platform.reserve.service.ReserveDtoTransferBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ReserveFacade {

	@Autowired
	private ActivityService activityService;

	@Autowired
	private MailService mailService;

	@Autowired
	private ReservationInfoService reservationInfoService;

	@Autowired
	private ReserveDtoTransferBuilder reserveDtoTransferBuilder;

	@Transactional(propagation = Propagation.REQUIRED)
	public Response<ReserveVO> reserve(Request<ReserveVO> request){
		// check resource can be reserve

		// reserve resource

		// update resource

		// send reserve success message

		// return vo
		ReserveVO reserveVO = request.getEntity();
		reservationInfoService.save(reserveDtoTransferBuilder.toDto(reserveVO));
		return ReserveResponse.<ReserveVO>builder().responseType(ResponseType.SUCCESS).entity(reserveVO).build();
	}

}
