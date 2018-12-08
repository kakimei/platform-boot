package com.platform.sign.facade;

import com.platform.facade.Request;
import com.platform.facade.Response;
import com.platform.facade.ResponseType;
import com.platform.reserve.facade.ReserveResponse;
import com.platform.sign.controller.vo.SignReservationInfoVO;
import com.platform.sign.service.SignReservationInfoDtoTransferBuilder;
import com.platform.sign.service.SignReservationInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SignReservationInfoFacade {

	@Autowired
	private SignReservationInfoService signReservationInfoService;

	@Autowired
	private SignReservationInfoDtoTransferBuilder signReservationInfoDtoTransferBuilder;

	public Response<SignReservationInfoVO> sign(Request<SignReservationInfoVO> request) {
		SignReservationInfoVO signReservationInfoVO = request.getEntity();
		signReservationInfoService.save(signReservationInfoDtoTransferBuilder.toDTO(signReservationInfoVO));
		return ReserveResponse.<SignReservationInfoVO>builder().responseType(ResponseType.SUCCESS).entity(signReservationInfoVO).build();
	}
}
