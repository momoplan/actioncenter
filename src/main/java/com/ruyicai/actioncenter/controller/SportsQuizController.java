package com.ruyicai.actioncenter.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.actioncenter.exception.RuyicaiException;
import com.ruyicai.actioncenter.service.SportsQuizService;
import com.ruyicai.actioncenter.util.ErrorCode;
import com.ruyicai.actioncenter.util.JsonMapper;

@RequestMapping("/sportsquiz")
@Controller
public class SportsQuizController {
	
	private Logger logger = LoggerFactory.getLogger(SportsQuizController.class);
	
	@Autowired
	private SportsQuizService sportsQuizService;
	
	@RequestMapping("/participate")
	public @ResponseBody String participate(@RequestParam(value = "callBackMethod", required = true) String callBackMethod,
			@RequestParam(value = "mobileid", required = false) String mobileid,
			@RequestParam(value = "answerid", required = false) Integer answerid) {
		logger.info("/sportsquiz/participate callBackMethod:{} mobileid:{} answerid:{}", new String[] {callBackMethod, mobileid, answerid+""});
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			sportsQuizService.participate(mobileid, answerid);
		} catch (IllegalArgumentException e) {
			logger.error("participate error" + e.getMessage());
			rd.setValue(e.getMessage());
			result = ErrorCode.PARAMTER_ERROR;
		} catch(RuyicaiException e) {
			logger.error("participate error" +e.getMessage());
			rd.setValue(e.getMessage());
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("participate error", new String[] { e.getMessage() }, e);
			logger.error("recruit error", e);
			rd.setValue(e.getMessage());
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return JsonMapper.toJsonP(callBackMethod, rd);
	}
	
	@RequestMapping("/draw")
	public @ResponseBody String draw(@RequestParam(value = "callBackMethod", required = true) String callBackMethod,
			@RequestParam(value = "answerid", required = false) Integer answerid) {
		logger.info("/sportsquiz/draw callBackMethod:{} answerid:{}", new String[] {callBackMethod, answerid+""});
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			sportsQuizService.draw(answerid);
		} catch (IllegalArgumentException e) {
			logger.error("draw error" + e.getMessage());
			rd.setValue(e.getMessage());
			result = ErrorCode.PARAMTER_ERROR;
		} catch(RuyicaiException e) {
			logger.error("draw error" + e.getMessage());
			rd.setValue(e.getMessage());
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("draw error", new String[] { e.getMessage() }, e);
			rd.setValue(e.getMessage());
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return JsonMapper.toJsonP(callBackMethod, rd);
	}
	
	@RequestMapping("/getAnswerid")
	public @ResponseBody String getAnswerid(@RequestParam(value = "callBackMethod", required = true) String callBackMethod) {
		logger.info("/sportsquiz/getAnswerid callBackMethod:{}", new String[] {callBackMethod});
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			rd.setValue(sportsQuizService.getAnswerid());
		} catch (IllegalArgumentException e) {
			logger.error("getAnswerid error" + e.getMessage());
			rd.setValue(e.getMessage());
			result = ErrorCode.PARAMTER_ERROR;
		} catch(RuyicaiException e) {
			logger.error("getAnswerid error" + e.getMessage());
			rd.setValue(e.getMessage());
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("getAnswerid error", new String[] { e.getMessage() }, e);
			rd.setValue(e.getMessage());
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return JsonMapper.toJsonP(callBackMethod, rd);
	}
	
	@RequestMapping("/pv")
	public @ResponseBody String pv(@RequestParam(value = "callBackMethod", required = true) String callBackMethod) {
		logger.info("/sportsquiz/pv callBackMethod:{}", new String[] {callBackMethod});
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			rd.setValue(sportsQuizService.pv());
		} catch (IllegalArgumentException e) {
			logger.error("pv error" + e.getMessage());
			rd.setValue(e.getMessage());
			result = ErrorCode.PARAMTER_ERROR;
		} catch(RuyicaiException e) {
			logger.error("pv error" + e.getMessage());
			rd.setValue(e.getMessage());
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("pv error", new String[] { e.getMessage() }, e);
			rd.setValue(e.getMessage());
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return JsonMapper.toJsonP(callBackMethod, rd);
	}
	
	@RequestMapping("/verify")
	public @ResponseBody String verify(@RequestParam(value = "callBackMethod", required = true) String callBackMethod,
			@RequestParam(value = "mobileid", required = false) String mobileid) {
		logger.info("/sportsquiz/verify callBackMethod:{} mobileid:{}", new String[] {callBackMethod, mobileid});
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			rd.setValue(sportsQuizService.verify(mobileid));
		} catch (IllegalArgumentException e) {
			logger.error("verify error" + e.getMessage());
			rd.setValue(e.getMessage());
			result = ErrorCode.PARAMTER_ERROR;
		} catch(RuyicaiException e) {
			logger.error("verify error" + e.getMessage());
			rd.setValue(e.getMessage());
			result = e.getErrorCode();
		} catch (Exception e) {
			logger.error("verify error", new String[] { e.getMessage() }, e);
			rd.setValue(e.getMessage());
			result = ErrorCode.ERROR;
		}
		rd.setErrorCode(result.value);
		return JsonMapper.toJsonP(callBackMethod, rd);
	}

}
