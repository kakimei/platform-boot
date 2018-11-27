package com.platform.feedback.controller;

import com.platform.facade.Request;
import com.platform.feedback.controller.vo.FeedBackVO;
import com.platform.feedback.facade.FeedBackFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/feedback")
public class FeedBackController {

	@Autowired
	private FeedBackFacade feedBackFacade;

	@RequestMapping(value = "/thumbsUp", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	public @ResponseBody Boolean thumbsUp(@RequestBody FeedBackVO feedBackVO){
		Request<Long> request = Request.<Long>builder().entity(feedBackVO.getReservationInfoId()).build();
		feedBackFacade.thumbsUp(request);
		return true;
	}
}
