package com.platform.reserve.facade;

import com.platform.reserve.controller.vo.ReserveVO;
import com.platform.facade.Request;
import com.platform.facade.Response;
import com.platform.facade.ResponseType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReserveFacade {

	public Response<ReserveVO> reserve(Request<ReserveVO> request){
		// check resource can be reserve

		// reserve resource

		// update resource

		// send reserve success message

		// return vo
		ReserveVO result = request.getEntity();
		return ReserveResponse.builder().responseType(ResponseType.SUCCESS).entity(result).build();
	}

}
