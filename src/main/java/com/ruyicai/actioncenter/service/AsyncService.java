package com.ruyicai.actioncenter.service;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ruyicai.actioncenter.controller.ResponseData;
import com.ruyicai.actioncenter.exception.RuyicaiException;
import com.ruyicai.actioncenter.util.ErrorCode;
import com.ruyicai.actioncenter.util.HttpUtil;
import com.ruyicai.actioncenter.util.JsonUtil;
import com.ruyicai.lottery.domain.Tuserinfo;

@Service
public class AsyncService {

	private Logger logger = LoggerFactory.getLogger(AsyncService.class);

	@Autowired
	private LotteryService lotteryService;

	@Value("${msgcenterurl}")
	private String msgcenterurl;

	private void asyncSendSMS(String mobileid, String text) {
		String url = msgcenterurl + "/sms/send";
		try {
			String result = HttpUtil.post(url, "mobileIds=" + mobileid + "&text=" + text);
			ResponseData rd = JsonUtil.fromJsonToObject(result, ResponseData.class);
			if (rd.getErrorCode().equals(ErrorCode.OK.value)) {
				logger.info("发送短信 mobileid:" + mobileid + " 成功");
			} else {
				logger.info("发送短信 mobileid:" + mobileid + " 失败" + "ErrorCode:" + rd.getErrorCode());
			}
		} catch (Exception e) {
			logger.error("发送短信失败 mobileid:" + mobileid, e);
		}
	}

	/**
	 * @param mobileid
	 */
	@Async
	public void asyncSendMoneyAndSMS(String username, BigDecimal amount, String text) {
		logger.info("竞猜赠送奖金并且发短信 username:" + username + " amount:" + amount + " text:" + text);
		Tuserinfo tuserinfo = lotteryService.findByUsername(username);
		if (tuserinfo == null) {
			throw new RuyicaiException(ErrorCode.UserMod_UserNotExists);
		}
		lotteryService.directChargeProcess(tuserinfo.getUserno(), amount, tuserinfo.getSubChannel(),
				tuserinfo.getChannel(), "体育之窗竞猜奖金", null, null);
		this.asyncSendSMS(username, text);
	}

	/**
	 * 用户使用完兑换券以后，查询用户是否充值过，如果未充值过，则把用户的渠道号改为兑换券的渠道号。如果渠道号为空，不更改用户渠道号。
	 * 
	 * @param channel
	 *            渠道号
	 * @param userno
	 *            用户id
	 */
	@Async
	public void asyncUpdateUserChannel(String channel, String userno) {
		if (StringUtils.isBlank(channel) || StringUtils.isBlank(userno)) {
			return;
		}
		// 查找用户是否充值过
		Integer chargeCount = lotteryService.getChargeRecordCountByUserno(userno);
		if (chargeCount == null) {
			logger.error("充值查询出错");
		}
		// 如果没充值过修改为兑换券渠道
		if (chargeCount.equals(0)) {
			logger.info("未充值过 更改用户渠道 userno:" + userno + " channel:" + channel);
			lotteryService.modifyUserChannel(userno, channel);
		} else {
			logger.info("充值过 不更改用户渠道 userno:" + userno + " chargeCount:" + chargeCount);
		}

	}
}
