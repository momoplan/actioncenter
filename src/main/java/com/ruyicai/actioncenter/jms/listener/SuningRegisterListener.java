package com.ruyicai.actioncenter.jms.listener;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Body;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.consts.ActionJmsType;
import com.ruyicai.actioncenter.domain.SuningRegister;
import com.ruyicai.actioncenter.domain.Tactivity;
import com.ruyicai.actioncenter.domain.TuserPrizeDetail;
import com.ruyicai.actioncenter.util.DateUtil;
import com.ruyicai.actioncenter.util.JsonUtil;
import com.ruyicai.lottery.domain.Tuserinfo;

@Service
public class SuningRegisterListener {

	private Logger logger = LoggerFactory.getLogger(SuningRegisterListener.class);

	@Produce(uri = "jms:topic:sendActivityPrize")
	private ProducerTemplate sendActivityPrizeProducer;

	public void userCreatedCustomer(@Body String userInfoJson) {
		suningRegisterAction(userInfoJson);
	}

	public void userModifyCustomer(@Body String userInfoJson) {
		suningRegisterAction(userInfoJson);
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
		String userno = tuserinfo.getUserno();
		Tactivity suningtactivity = Tactivity.findTactivity(null, null, tuserinfo.getSubChannel(),
				tuserinfo.getChannel(), ActionJmsType.SuNing_REGISTER.value);
		if (suningtactivity != null) {
			if (StringUtils.isNotBlank(tuserinfo.getMobileid())) {
				Date regtime = tuserinfo.getRegtime();
				String regtimeStr = DateUtil.format("yyyyMMdd", regtime);
				String todayStr = DateUtil.format("yyyyMMdd", new Date());
				if (regtimeStr.equals(todayStr)) {
					SuningRegister register = SuningRegister.findSuningRegister(tuserinfo.getMobileid());
					if (register == null) {
						String express = suningtactivity.getExpress();
						Map<String, Object> activity = JsonUtil.transferJson2Map(express);
						Integer prizeamt = (Integer) activity.get("prizeamt");
						logger.info("苏宁完善用户信息赠送userno:{},mobileid:{},prizeamt:{}",
								new String[] { userno, tuserinfo.getMobileid(), prizeamt + "" });
						SuningRegister.createSuningRegister(tuserinfo.getMobileid(), userno);
						sendPrize2UserJMS(userno, new BigDecimal(prizeamt), ActionJmsType.SuNing_REGISTER, null,
								suningtactivity.getMemo());
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

	@Transactional
	public void sendPrize2UserJMS(String userno, BigDecimal amt, ActionJmsType actionJmsType, String ttransactionid,
			String memo) {
		TuserPrizeDetail userPrizeDetail = TuserPrizeDetail.createTprizeUserBuyLog(userno, amt, actionJmsType);
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("prizeDetailId", userPrizeDetail.getId());
		headers.put("actionJmsType", actionJmsType.value);
		headers.put("ttransactionid", ttransactionid);
		headers.put("memo", memo);
		sendActivityPrizeProducer.sendBodyAndHeaders(null, headers);
	}

}
