package com.ruyicai.actioncenter.jms.listener;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.camel.Body;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.consts.ActionJmsType;
import com.ruyicai.actioncenter.dao.TactivityDao;
import com.ruyicai.actioncenter.domain.Chong20Mobile;
import com.ruyicai.actioncenter.domain.FirstChargeDelaySend;
import com.ruyicai.actioncenter.domain.FirstChargeUser;
import com.ruyicai.actioncenter.domain.SuningRegister;
import com.ruyicai.actioncenter.domain.Tactivity;
import com.ruyicai.actioncenter.service.SendActivityPrizeJms;
import com.ruyicai.actioncenter.util.DateUtil;
import com.ruyicai.actioncenter.util.JsonUtil;
import com.ruyicai.lottery.domain.Tuserinfo;

@Service
public class SuningRegisterListener {

	private Logger logger = LoggerFactory.getLogger(SuningRegisterListener.class);

	@Autowired
	private TactivityDao tactivityDao;

	@Autowired
	private SendActivityPrizeJms sendActivityPrizeJms;

	public void userCreatedCustomer(@Body String userInfoJson) {
		logger.info("用户信息创建:" + userInfoJson);
		suningRegisterAction(userInfoJson);
	}

	public void userModifyCustomer(@Body String userInfoJson) {
		logger.info("用户信息修改:" + userInfoJson);
		suningRegisterAction(userInfoJson);
		try {
			firstCharge(userInfoJson);
			firstCharge20(userInfoJson);
		} catch (Exception e) {
			logger.error("首次充值后完善信息赠送异常", e);
		}
	}

	@Transactional
	public void firstCharge(String userInfoJson) {
		if (StringUtils.isBlank(userInfoJson)) {
			logger.error("the arguments userInfoJson is blank");
			return;
		}
		Tuserinfo tuserinfo = Tuserinfo.fromJsonToTuserinfo(userInfoJson);
		if (tuserinfo == null || StringUtils.isBlank(tuserinfo.getUserno())
				|| StringUtils.isBlank(tuserinfo.getChannel())) {
			logger.error("用户为空或用户编号为空或channel为空");
			return;
		}
		if (tuserinfo != null && tuserinfo.getChannel() != null && tuserinfo.getChannel().equals("991")) {
			logger.info("如意彩大户渠道不参加活动");
			return;
		}
		String userno = tuserinfo.getUserno();
		Tactivity tactivity = tactivityDao.findTactivity(null, null, tuserinfo.getSubChannel(), null,
				ActionJmsType.FIRST_CHONGZHI_ZENGSONG.value);
		if (tactivity != null) {
			logger.info("首次充值用户信息修改userno:" + userno);
			if (StringUtils.isNotBlank(tuserinfo.getMobileid())) {
				Chong20Mobile chong20Mobile = Chong20Mobile.findChong20Mobile(tuserinfo.getMobileid());
				if (chong20Mobile != null) {
					logger.info("第一次充值活动,用户手机号已赠送过.mobileid:{},userno:{}", new String[] { tuserinfo.getMobileid(),
							userno });
					return;
				}
				FirstChargeUser fcu = FirstChargeUser.findFirstChargeUser(userno, true);
				if (fcu == null || fcu.getState() == 1) {
					logger.info("非首次充值用户或已赠送userno:" + userno);
					return;
				}
				String todayStr = DateUtil.format("yyyyMMdd", new Date());
				String createTimeStr = DateUtil.format("yyyyMMdd", fcu.getCreateTime());
				if (!todayStr.equals(createTimeStr)) {
					logger.info("首次充值用户不是在同一天完善的信息today:" + todayStr + ",createTime:" + createTimeStr);
					return;
				}
				logger.info("首次充值后完善信息,userno:" + userno);
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer prizeamt = (Integer) activity.get("prizeamt");
				Chong20Mobile.createChong20Mobile(tuserinfo.getMobileid(), userno);
				FirstChargeUser.updateFirstChargeUser(fcu, 1);
				sendActivityPrizeJms.sendPrize2UserJMS(tuserinfo.getUserno(), new BigDecimal(prizeamt),
						ActionJmsType.FIRST_CHONGZHI_ZENGSONG, tactivity.getMemo(), fcu.getTtransactionid(), "",
						fcu.getTtransactionid());
			} else {
				logger.info("首次充值用户信息修改手机号未完善userno:" + userno);
			}
		}
	}

	@Transactional
	public void firstCharge20(String userInfoJson) {
		if (StringUtils.isBlank(userInfoJson)) {
			logger.error("the arguments userInfoJson is blank");
			return;
		}
		Tuserinfo tuserinfo = Tuserinfo.fromJsonToTuserinfo(userInfoJson);
		if (tuserinfo == null || StringUtils.isBlank(tuserinfo.getUserno())
				|| StringUtils.isBlank(tuserinfo.getChannel())) {
			logger.error("用户为空或用户编号为空或channel为空");
			return;
		}
		if (tuserinfo != null && tuserinfo.getChannel() != null && tuserinfo.getChannel().equals("991")) {
			logger.info("如意彩大户渠道不参加活动");
			return;
		}
		String userno = tuserinfo.getUserno();
		Tactivity tactivity = tactivityDao.findTactivity(null, null, tuserinfo.getSubChannel(), null,
				ActionJmsType.FIRST_CHONGZHI_ZENGSONG_20.value);
		if (tactivity != null) {
			logger.info("首次充值用户信息修改userno:" + userno);
			if (StringUtils.isNotBlank(tuserinfo.getMobileid())) {
				Chong20Mobile chong20Mobile = Chong20Mobile.findChong20Mobile(tuserinfo.getMobileid());
				if (chong20Mobile != null) {
					logger.info("第一次充值活动,用户手机号已赠送过.mobileid:{},userno:{}", new String[] { tuserinfo.getMobileid(),
							userno });
					return;
				}
				FirstChargeUser fcu = FirstChargeUser.findFirstChargeUser(userno, true);
				if (fcu == null || fcu.getState() == 1) {
					logger.info("非首次充值用户或已赠送userno:" + userno);
					return;
				}
				String todayStr = DateUtil.format("yyyyMMdd", new Date());
				String createTimeStr = DateUtil.format("yyyyMMdd", fcu.getCreateTime());
				if (!todayStr.equals(createTimeStr)) {
					logger.info("首次充值用户不是在同一天完善的信息today:" + todayStr + ",createTime:" + createTimeStr);
					return;
				}
				logger.info("首次充值后完善信息,userno:" + userno);
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer prizeamt = (Integer) activity.get("prizeamt");
				Chong20Mobile.createChong20Mobile(tuserinfo.getMobileid(), userno);
				FirstChargeUser.updateFirstChargeUser(fcu, 1);
				sendActivityPrizeJms.sendPrize2UserJMS(tuserinfo.getUserno(), new BigDecimal(prizeamt),
						ActionJmsType.FIRST_CHONGZHI_ZENGSONG_20, tactivity.getMemo(), fcu.getTtransactionid(), "",
						fcu.getTtransactionid());
				// 增加延迟赠送记录
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.MONTH, 1);
				calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 15, 8, 0, 0);
				FirstChargeDelaySend.createFirstChargeDelaySend(tuserinfo.getUserno(), new BigDecimal(500),
						tactivity.getMemo(), calendar.getTime(), fcu.getTtransactionid());
				calendar.add(Calendar.MONTH, 1);
				FirstChargeDelaySend.createFirstChargeDelaySend(tuserinfo.getUserno(), new BigDecimal(500),
						tactivity.getMemo(), calendar.getTime(), fcu.getTtransactionid());
			} else {
				logger.info("首次充值用户信息修改手机号未完善userno:" + userno);
			}
		}
	}

	@Transactional
	public void suningRegisterAction(String userInfoJson) {
		if (StringUtils.isBlank(userInfoJson)) {
			logger.error("the arguments userInfoJson is blank");
			return;
		}
		Tuserinfo tuserinfo = Tuserinfo.fromJsonToTuserinfo(userInfoJson);
		if (tuserinfo == null || StringUtils.isBlank(tuserinfo.getUserno())
				|| StringUtils.isBlank(tuserinfo.getChannel())) {
			logger.error("用户为空或用户编号为空或channel为空");
			return;
		}
		if (tuserinfo != null && tuserinfo.getChannel() != null && tuserinfo.getChannel().equals("991")) {
			logger.info("如意彩大户渠道不参加活动");
			return;
		}
		String userno = tuserinfo.getUserno();
		Tactivity suningtactivity = tactivityDao.findTactivity(null, null, tuserinfo.getSubChannel(),
				tuserinfo.getChannel(), ActionJmsType.SuNing_REGISTER.value);
		if (suningtactivity != null) {
			if (StringUtils.isNotBlank(tuserinfo.getMobileid())) {
				Date regtime = tuserinfo.getRegtime();
				String regtimeStr = DateUtil.format("yyyyMMdd", regtime);
				String todayStr = DateUtil.format("yyyyMMdd", new Date());
				if (regtimeStr.equals(todayStr)) {
					SuningRegister byUserno = SuningRegister.findSuningRegisterByUserno(userno);
					if (byUserno != null) {
						logger.info("苏宁渠道完善信息赠送3元,用户编号已赠送过.mobileid:{},userno:{}",
								new String[] { tuserinfo.getMobileid(), userno });
						return;
					}
					SuningRegister register = SuningRegister.findSuningRegister(tuserinfo.getMobileid());
					if (register == null) {
						String express = suningtactivity.getExpress();
						Map<String, Object> activity = JsonUtil.transferJson2Map(express);
						Integer prizeamt = (Integer) activity.get("prizeamt");
						logger.info("苏宁完善用户信息赠送userno:{},mobileid:{},prizeamt:{}",
								new String[] { userno, tuserinfo.getMobileid(), prizeamt + "" });
						SuningRegister.createSuningRegister(tuserinfo.getMobileid(), userno);
						sendActivityPrizeJms.sendPrize2UserJMS(userno, new BigDecimal(prizeamt),
								ActionJmsType.SuNing_REGISTER, suningtactivity.getMemo(), "", "", "");
					} else {
						logger.info("苏宁渠道完善信息赠送3元,用户手机号已赠送过.mobileid:{},user:{}",
								new String[] { tuserinfo.getMobileid(), tuserinfo.toString() });
					}
				} else {
					logger.info("苏宁渠道完善信息不是当天userno:{},regtimeStr:{},mobileid:{}", new String[] { userno, regtimeStr,
							tuserinfo.getMobileid() });
				}
			} else {
				logger.info("用户手机号为空userno:{}", new String[] { userno });
			}
		} else {
			logger.info("苏宁渠道注册送彩金未开启");
		}
	}
}
