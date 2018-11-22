package com.platform.reserve.facade;

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
	private MailService mailService;

	@Autowired
	private ReservationInfoService reservationInfoService;

	@Autowired
	private ReserveDtoTransferBuilder reserveDtoTransferBuilder;

	@Transactional(propagation = Propagation.REQUIRED)
	public Response<ReserveVO> reserve(Request<ReserveVO> request){
		ReserveVO reserveVO = request.getEntity();
		reservationInfoService.save(reserveDtoTransferBuilder.toDto(reserveVO));
		mailService.sendMail("yiming.he@coupang.com", "reserve success", "test!");
		return ReserveResponse.<ReserveVO>builder().responseType(ResponseType.SUCCESS).entity(reserveVO).build();
	}

}
