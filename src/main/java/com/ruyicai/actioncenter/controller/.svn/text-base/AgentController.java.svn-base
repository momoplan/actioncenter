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

@RequestMapping(value = "/agent")
@Controller
public class AgentController {

	private Logger logger = LoggerFactory.getLogger(AgentController.class);

	@Autowired
	private TactionService tactionService;

	@RequestMapping(value = "/registerAgent", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData registerAgent(@RequestParam("userno") String userno, @RequestParam("mobileid") String mobileid) {
		logger.info("agent/registerAgent");
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			tactionService.registerAgent(userno, mobileid);
		} catch (RuyicaiException e) {
			logger.error("注册代理出错,{}", new String[] { e.getMessage() }, e);
			rd.setValue(e.getMessage());
			result = ErrorCode.ERROR;
		} catch (Exception e) {
			logger.error("注册代理出错,{}", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}
}
