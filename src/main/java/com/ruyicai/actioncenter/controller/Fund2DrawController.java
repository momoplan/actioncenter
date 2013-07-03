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
import com.ruyicai.actioncenter.service.Fund2DrawService;
import com.ruyicai.actioncenter.util.ErrorCode;

@RequestMapping("/fund2draw")
@Controller
public class Fund2DrawController {

	private Logger logger = LoggerFactory.getLogger(ActionController.class);

	@Autowired
	private Fund2DrawService fund2DrawService;

	/**
	 * 手工增加可提现金额
	 * 
	 * @param ttransactionid
	 * @return
	 */
	@RequestMapping(value = "/manualFund2Draw", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData manualFund2Draw(@RequestParam("ttransactionid") String ttransactionid) {
		logger.info("/fund2draw/manualFund2Draw ttransactionid:" + ttransactionid);
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			fund2DrawService.fund2Draw(ttransactionid);
		} catch (RuyicaiException e) {
			logger.error("manualFund2Draw error", e);
			rd.setValue(e.getMessage());
			result = ErrorCode.ERROR;
		} catch (Exception e) {
			logger.error("manualFund2Draw error", e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}
}
