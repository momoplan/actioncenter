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
	 * 根据ttransactionid查询增加可提现记录
	 * @param ttransactionid
	 * @return
	 */
	@RequestMapping(value = "/findFund2Draw")
	public @ResponseBody ResponseData findFund2Draw(@RequestParam("ttransactionid") String ttransactionid) {
		logger.info("/fund2draw/findFund2Draw ttransactionid:" + ttransactionid);
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			rd.setValue(fund2DrawService.findFund2Draw(ttransactionid));
		} catch (IllegalArgumentException e) {
			logger.error("findFund2Draw error", e);
			rd.setValue(e.getMessage());
			result = ErrorCode.PARAMTER_ERROR;
		} catch (Exception e) {
			logger.error("findFund2Draw error", e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}
	
	/**
	 * 分页查询增加可提现记录
	 * @param condition
	 * @param pageIndex
	 * @param maxResult
	 * @param orderBy
	 * @param orderDir
	 * @return
	 */
	@RequestMapping(value = "/findFund2DrawByPage")
	public @ResponseBody ResponseData findFound2DrawByPage(@RequestParam(value = "condition", required = false) String condition,
			@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "maxResult", required = false, defaultValue = "30") int maxResult,
			@RequestParam(value = "orderBy", required = false) String orderBy,
			@RequestParam(value = "orderDir", required = false) String orderDir) {
		logger.info("/fund2draw/findFund2DrawByPage condition:{} pageIndex:{} maxResult:{} orderBy:{} orderDir:{}", new String[] {condition, pageIndex+"", maxResult+"", orderBy, orderDir});
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			Page<Fund2Draw> page = fund2DrawService.findFund2DrawByPage(condition, pageIndex, maxResult, orderBy, orderDir);
			rd.setValue(page);
		} catch (RuyicaiException e) {
			logger.error("findFund2DrawByPage error", e);
			rd.setValue(e.getMessage());
			result = ErrorCode.ERROR;
		} catch(IllegalArgumentException e) {
			logger.error("findFund2DrawByPage error", e);
			rd.setValue(e.getMessage());
			result = ErrorCode.ERROR;
		} catch (Exception e) {
			logger.error("findFund2DrawByPage error", e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}
	
	@RequestMapping(value = "/findFirstChargeUser", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData findFirstChargeUserExists(@RequestParam("userno") String userno,@RequestParam("state") Integer state) {
		logger.info("/fund2draw/findFirstChargeUser condition:{} userno:{} state:{}", new String[] {userno, state + " "});
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try{
			rd.setValue(fund2DrawService.findFirstChargeUser(userno, state));
		} catch (Exception e) {
			logger.error("findFirstChargeUser error", e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}
	
	@RequestMapping(value = "/findChong20Mobile", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData findChong20MobileExists(@RequestParam("mobileid") String mobileid) {
		logger.info("/fund2draw/findChong20Mobile condition:{} userno:{}", new String[] {mobileid});
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try{
			rd.setValue(fund2DrawService.findChong20Mobile(mobileid));
		} catch (Exception e) {
			logger.error("findChong20Mobile error", e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}
	
}
