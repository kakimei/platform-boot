package com.platform.facade.reserve;

import com.platform.controller.vo.ReserveVO;
import com.platform.controller.vo.ResourceType;
import com.platform.facade.Request;
import com.platform.facade.Response;
import com.platform.facade.ResponseType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ReserveFacade {

	public Response<ReserveVO> reserve(Request<ReserveVO> reserveVO){
		// check resource can be reserve

		// reserve resource

		// update resource

		// send reserve success message

		// return vo
		ReserveVO result = ReserveVO.builder().build();
		return ReserveResponse.builder().responseType(ResponseType.SUCCESS).entity(result).build();
	}

	public Response<List<ReserveVO>> list(Request<ResourceType> request){
		List<ReserveVO> result = new ArrayList<ReserveVO>();
		return ReserveResponse.builder().responseType(ResponseType.SUCCESS).entity(result).build();
	}
}
