package com.ruyicai.actioncenter.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.actioncenter.domain.Fund2Draw;
import com.ruyicai.actioncenter.exception.RuyicaiException;
import com.ruyicai.actioncenter.service.Fund2DrawService;
import com.ruyicai.actioncenter.util.ErrorCode;
import com.ruyicai.actioncenter.util.Page;

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
	
	/**
	 * 分页查询可提现记录
	 * @param condition
	 * @param pageIndex
	 * @param maxResult
	 * @param orderBy
	 * @param orderDir
	 * @return
	 */
	@RequestMapping(value = "/findFound2DrawByPage")
	public @ResponseBody ResponseData findFound2DrawByPage(@RequestParam(value = "condition", required = false) String condition,
			@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "maxResult", required = false, defaultValue = "30") int maxResult,
			@RequestParam(value = "orderBy", required = false) String orderBy,
			@RequestParam(value = "orderDir", required = false) String orderDir) {
		logger.info("/fund2draw/findFound2DrawByPage condition:{} pageIndex:{} maxResult:{} orderBy:{} orderDir:{}", new String[] {condition, pageIndex+"", maxResult+"", orderBy, orderDir});
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			Page<Fund2Draw> page = fund2DrawService.findFund2DrawByPage(condition, pageIndex, maxResult, orderBy, orderDir);
			rd.setValue(page);
		} catch (RuyicaiException e) {
			logger.error("findFound2DrawByPage error", e);
			rd.setValue(e.getMessage());
			result = ErrorCode.ERROR;
		} catch(IllegalArgumentException e) {
			logger.error("findFound2DrawByPage error", e);
			rd.setValue(e.getMessage());
			result = ErrorCode.ERROR;
		} catch (Exception e) {
			logger.error("findFound2DrawByPage error", e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}
}
