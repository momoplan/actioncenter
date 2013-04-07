package com.ruyicai.actioncenter.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.actioncenter.domain.SendMoneyDetails;
import com.ruyicai.actioncenter.exception.RuyicaiException;
import com.ruyicai.actioncenter.service.TactionService;
import com.ruyicai.actioncenter.util.ErrorCode;
import com.ruyicai.actioncenter.util.JsonUtil;
import com.ruyicai.actioncenter.util.Page;

@Controller
@RequestMapping("/sendMoney")
public class SendMoneyController {
	private Logger logger = LoggerFactory.getLogger(SendMoneyController.class);

	/**
	 * 保存送钱。
	 * 
	 * @return SendMoneyDetails
	 */
	@RequestMapping(value = "/savesendMoney", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData savesendMoney(@RequestParam("sendusername") String sendusername,
			@RequestParam("reciverUserno") String reciverUserno, @RequestParam("content") String content,
			@RequestParam("amt") BigDecimal amt) {
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			logger.info("赠送彩票 请求参数为：" + sendusername);
			rd.setValue(SendMoneyDetails.createSendMoneyDetails(sendusername, reciverUserno, amt, content));
		} catch (Exception e) {
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * 查询送钱列表
	 * 
	 * @param userno
	 *            被赠送用户userno
	 * @param condition
	 *            查询条件JSON
	 * @param startLine
	 *            开始记录数
	 * @param endLine
	 *            总记录数
	 * @param orderBy
	 *            排序字段
	 * @param orderDir
	 *            排序类型
	 * @return Page<PresentDetailsDTO>
	 */
	@RequestMapping(value = "/selectReciverSendMoneyDetails")
	public @ResponseBody
	ResponseData selectReciverSendMoneyDetails(@RequestParam(value = "userno") String userno,
			@RequestParam(value = "condition", required = false) String condition,
			@RequestParam(value = "startLine", required = false, defaultValue = "0") int startLine,
			@RequestParam(value = "endLine", required = false, defaultValue = "30") int endLine,
			@RequestParam(value = "orderBy", required = false) String orderBy,
			@RequestParam(value = "orderDir", required = false) String orderDir) {
		ResponseData rd = new ResponseData();
		Page<SendMoneyDetails> page = new Page<SendMoneyDetails>(startLine, endLine, orderBy, orderDir);
		try {
			Map<String, Object> conditionMap = JsonUtil.transferJson2Map(condition);
			SendMoneyDetails.findReciverSendMoneyDetailsByPage(userno, conditionMap, page);
			rd.setValue(page);
			rd.setErrorCode(ErrorCode.OK.value);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("被送查询分页列表出错", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}

	/**
	 * mgr查询赠送列表
	 * 
	 * @param condition
	 *            查询条件JSON
	 * @param startLine
	 *            开始记录数
	 * @param endLine
	 *            总记录数
	 * @param orderBy
	 *            排序字段
	 * @param orderDir
	 *            排序类型
	 * @return Page<PresentDetails>
	 */
	@RequestMapping(value = "/selectSendMoneyDetails")
	public @ResponseBody
	ResponseData selectSendMoneyDetails(@RequestParam(value = "condition", required = false) String condition,
			@RequestParam(value = "startLine", required = false, defaultValue = "0") int startLine,
			@RequestParam(value = "endLine", required = false, defaultValue = "30") int endLine,
			@RequestParam(value = "orderBy", required = false) String orderBy,
			@RequestParam(value = "orderDir", required = false) String orderDir) {
		ResponseData rd = new ResponseData();
		Page<SendMoneyDetails> page = new Page<SendMoneyDetails>(startLine, endLine, orderBy, orderDir);
		try {
			Map<String, Object> conditionMap = JsonUtil.transferJson2Map(condition);
			SendMoneyDetails.findSenderSendMoneyDetailsByPage(conditionMap, page);
			rd.setValue(page);
			rd.setErrorCode(ErrorCode.OK.value);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("赠送人查询分页列表出错", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}

	@Autowired
	private TactionService tactionService;

	/**
	 * 领取赠送彩金
	 * 
	 * @param presentid
	 *            赠送方案ID
	 * @param randomCode
	 */
	@RequestMapping(value = "/reciveSendMoney", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData reciveSendMoney(@RequestParam(value = "sendMoneyid") String sendMoneyid) {
		logger.info("recivePresent sendMoneyid:{}", new String[] { sendMoneyid });
		ResponseData rd = new ResponseData();
		try {
			rd.setValue(tactionService.reciveSendMoney(sendMoneyid));
			rd.setErrorCode(ErrorCode.OK.value);
		} catch (RuyicaiException e) {
			e.printStackTrace();
			logger.error("领取赠送彩金出错, errorcode:" + e.getErrorCode().value, e);
			rd.setErrorCode(e.getErrorCode().value);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("领取赠送彩金出错", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
}
