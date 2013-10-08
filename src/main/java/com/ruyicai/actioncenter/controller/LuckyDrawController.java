package com.ruyicai.actioncenter.controller;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.actioncenter.domain.PrizeInfo;
import com.ruyicai.actioncenter.domain.UserDrawDetails;
import com.ruyicai.actioncenter.service.DrawActivityService;
import com.ruyicai.actioncenter.util.ErrorCode;

@RequestMapping(value = "/luckyDraw")
@Controller
public class LuckyDrawController{

	private Logger logger = Logger.getLogger(LuckyDrawController.class);

	@Autowired
	DrawActivityService drawActivityService;

	/**
	 * 抽奖活动
	 * @return
	 */
	@RequestMapping(value = "/drawActivity", method = RequestMethod.GET)
	public @ResponseBody ResponseData drawActivity()
	{
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try{
			String activeTimes = "0";
			String userno = "00001119";
			String payObj = "100积分";
			String gainObj = "2元充值";

			logger.info("抽奖开始->用户编号：" + userno + ", 活动期次: " + activeTimes);
			// 根据概率随机获取奖品
			PrizeInfo pi = drawActivityService.getPrizeInfoByRandomProbability(activeTimes, userno, payObj, gainObj);
			// 返回中奖信息
			rd.setValue(pi);
		}catch(Exception e){
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
			logger.info("drawPrizeInfo error:" + e.getMessage());
		}
		rd.setErrorCode(result.value);
		logger.info("抽奖结束->respCode:" + result.value);
		return rd;
	}

	/**
	 * 获取用户中奖信息.
	 * @param userno
	 * @return
	 */
	@RequestMapping(value = "/userDrawDetails", method = RequestMethod.GET)
	public @ResponseBody ResponseData userDrawDetails(@RequestParam("userno") String userno){
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try{
			List<UserDrawDetails> userDrawDetails = drawActivityService.queryUserDrawList(userno);
			rd.setValue(userDrawDetails);
		}catch(Exception e){
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
			logger.info("queryUserDrawDetails error:" + e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}

}
