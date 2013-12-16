package com.ruyicai.actioncenter.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.actioncenter.dao.TactivityDao;
import com.ruyicai.actioncenter.dao.TuserPrizeDetailDao;
import com.ruyicai.actioncenter.domain.Tactivity;
import com.ruyicai.actioncenter.domain.TuserPrizeDetail;
import com.ruyicai.actioncenter.exception.RuyicaiException;
import com.ruyicai.actioncenter.jms.listener.SendActivityPrizeListener;
import com.ruyicai.actioncenter.util.ErrorCode;
import com.ruyicai.actioncenter.util.Page;

@RequestMapping("/tactivity")
@Controller
public class TactivityController {

	private Logger logger = LoggerFactory.getLogger(ActionController.class);

	@Autowired
	private SendActivityPrizeListener sendActivityPrizeListener;

	@Autowired
	private TactivityDao tactivityDao;

	@Autowired
	private TuserPrizeDetailDao tuserPrizeDetailDao;

	@RequestMapping(value = "/updateState", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updateState(@RequestParam("id") Long id, @RequestParam("state") Integer state) {
		logger.info("/tactivity/updateState");
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			rd.setValue(tactivityDao.updateState(id, state));
		} catch (RuyicaiException e) {
			logger.error("修改活动状态出错id:" + id + ",state:" + state + ",{}", new String[] { e.getMessage() }, e);
			rd.setValue(e.getMessage());
			result = ErrorCode.ERROR;
		} catch (Exception e) {
			logger.error("修改活动状态出错id:" + id + ",state:" + state + ",{}", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	/**
	 * @param lotno
	 *            彩种
	 * @param playtype
	 *            玩法
	 * @param subChannel
	 * @param channel
	 * @param actionJmsType
	 *            ActionJmsType的类型
	 * @param memo
	 * @param express
	 * @param state
	 *            1,有效，0失效
	 * @return
	 */
	@RequestMapping(value = "/saveorupdateActivity", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData saveorupdateActivity(@RequestParam(value = "lotno", required = false) String lotno,
			@RequestParam(value = "playtype", required = false) String playtype,
			@RequestParam(value = "subChannel", required = false) String subChannel,
			@RequestParam(value = "channel", required = false) String channel,
			@RequestParam(value = "actionJmsType") Integer actionJmsType,
			@RequestParam(value = "memo", required = false, defaultValue = " ") String memo,
			@RequestParam(value = "express") String express,
			@RequestParam(value = "state", required = false, defaultValue = "1") Integer state) {
		logger.info("/tactivity/saveorupdateActivity");
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			Tactivity tactivity = tactivityDao.saveOrUpdate(lotno, playtype, subChannel, channel, actionJmsType,
					express, state, memo);
			rd.setValue(tactivity);
		} catch (RuyicaiException e) {
			logger.error("saveorupdateActivity出错,{}", new String[] { e.getMessage() }, e);
			rd.setValue(e.getMessage());
			result = ErrorCode.ERROR;
		} catch (Exception e) {
			logger.error("saveorupdateActivity出错,{}", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	@RequestMapping(value = "/selectTactivity")
	public @ResponseBody
	ResponseData selectTactivity(
			@RequestParam(value = "startLine", required = false, defaultValue = "0") int startLine,
			@RequestParam(value = "endLine", required = false, defaultValue = "30") int endLine,
			@RequestParam(value = "orderBy", required = false) String orderBy,
			@RequestParam(value = "orderDir", required = false) String orderDir) {
		logger.info("/tactivity/selectTactivity");
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		Page<Tactivity> page = new Page<Tactivity>(startLine, endLine, orderBy, orderDir);
		try {
			tactivityDao.findTactivityByPage(page);
			rd.setValue(page);
		} catch (RuyicaiException e) {
			logger.error("findAllTactivity出错,{}", new String[] { e.getMessage() }, e);
			rd.setValue(e.getMessage());
			result = ErrorCode.ERROR;
		} catch (Exception e) {
			logger.error("findAllTactivity出错,{}", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}

	@RequestMapping(value = "/manualSendPrize", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData sendPrize() {
		logger.info("/manualSendPrize");
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			List<TuserPrizeDetail> list = tuserPrizeDetailDao.findFailingAgencyPrizeDetails();
			for (TuserPrizeDetail detail : list) {
				sendActivityPrizeListener.sendActivityPrizeCustomer(detail.getId(), detail.getActivityType(), null,
						null, detail.getBusinessId(), null);
			}
			rd.setValue(list.size() + "条数据执行成功");
		} catch (RuyicaiException e) {
			logger.error("手工派送派送失败的奖金异常,{}", new String[] { e.getMessage() }, e);
			rd.setValue(e.getMessage());
			result = ErrorCode.ERROR;
		} catch (Exception e) {
			logger.error("手工派送派送失败的奖金异常,{}", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}
}
