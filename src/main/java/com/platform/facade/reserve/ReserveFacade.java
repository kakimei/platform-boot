package com.platform.facade.reserve;

import com.platform.controller.vo.ReserveVO;
import com.platform.facade.Response;
import com.platform.facade.ResponseType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReserveFacade {

	public Response<ReserveVO> reserve(ReserveVO reserveVO){
		// check resource can be reserve

		// reserve resource

		// update resource

		// send reserve success message

		// return vo
		ReserveVO result = ReserveVO.builder().build();
		return ReserveResponse.builder().responseType(ResponseType.SUCCESS).entity(result).build();
	}
}
