package com.ruyicai.actioncenter.service;

import java.math.BigDecimal;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.ruyicai.actioncenter.controller.ResponseData;
import com.ruyicai.actioncenter.domain.SportsQuizUserLog;
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

	/**
	 * 异步注册 并且发送短信
	 */
	@Async
	public void sportsQuizRegister(String mobileid) {
		logger.info("异步注册用户:" + mobileid);
		StringBuilder text = new StringBuilder();
		Tuserinfo tuserinfo = lotteryService.findByUsername(mobileid);
		String password = this.getRandomString(6);
		if (tuserinfo == null) {
			tuserinfo = lotteryService.registerUserByMobileid(mobileid, password, "1047");
			logger.info("注册用户：" + mobileid + " 密码：" + password);
			if (tuserinfo == null) {
				logger.error("注册失败 mobileid:" + mobileid);
			} else {
				text.append("成功参与竞猜，账户名：").append(tuserinfo.getUserName()).append(" 密码：").append(password)
						.append(" 比赛结束后使用此账户登录如意彩http://t.cn/8DDt8sY? 领取赠送彩金");
			}
			SportsQuizUserLog.saveSportsQuizUserLog(mobileid, password);
		} else { // 已注册用户
			logger.info("已注册用户 mobileid:" + mobileid);
			text.append("成功参与竞猜，比赛结束后使用您的如意彩账户登录http://t.cn/8DDt8sY? 领取赠送彩金，详询4006651000");
			SportsQuizUserLog.saveSportsQuizUserLog(mobileid, "");
		}
		// 发送短信
		String url = msgcenterurl + "/sms/send";
		try {
			String result = HttpUtil.post(url, "mobileIds=" + mobileid + "&text=" + text.toString());
			ResponseData rd = JsonUtil.fromJsonToObject(result, ResponseData.class);
			if (rd != null) {
				if (rd.getErrorCode().equals(ErrorCode.OK.value)) {
					logger.info("参与体育竞猜发送短信成功 mobileid:" + mobileid + " text:" + text);
				} else {
					logger.error("参与体育竞猜发短信失败 mobileid:" + mobileid);
				}
			}
		} catch (Exception e) {
			logger.error("请求" + url + "失败" + e.getMessage(), e);
			throw new RuyicaiException(ErrorCode.ERROR);
		}
	}

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

	private String getRandomString(int length) {
		String base = "0123456789";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
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
