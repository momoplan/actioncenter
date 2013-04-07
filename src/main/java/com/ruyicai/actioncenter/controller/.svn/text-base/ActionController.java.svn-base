package com.ruyicai.actioncenter.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.actioncenter.exception.RuyicaiException;
import com.ruyicai.actioncenter.service.TactionService;
import com.ruyicai.actioncenter.util.ErrorCode;

@RequestMapping("/action")
@Controller
public class ActionController {

	private Logger logger = LoggerFactory.getLogger(ActionController.class);

	@Autowired
	private TactionService tactionService;

	/**
	 * 追号包年套餐活动
	 * 
	 * @param body
	 *            OrderRequest的json串
	 * @return ResponseData
	 */
	@RequestMapping(value = "/addNumOneYear", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData registerAgent(@RequestParam("body") String body) {
		logger.info("/action/addNumOneYear");
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			String flowno = tactionService.addNumOneYear(body);
			rd.setValue(flowno);
		} catch (RuyicaiException e) {
			logger.error("追号一年活动出错,{}", new String[] { e.getMessage() }, e);
			rd.setValue(e.getMessage());
			result = ErrorCode.ERROR;
		} catch (Exception e) {
			logger.error("追号一年活动出错,{}", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}
}
