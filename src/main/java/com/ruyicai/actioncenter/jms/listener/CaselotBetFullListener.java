package com.ruyicai.actioncenter.jms.listener;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Body;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.consts.ActionJmsType;
import com.ruyicai.actioncenter.dao.TuserPrizeDetailDao;
import com.ruyicai.actioncenter.domain.Tactivity;
import com.ruyicai.actioncenter.domain.Tjmsservice;
import com.ruyicai.actioncenter.domain.TuserPrizeDetail;
import com.ruyicai.actioncenter.service.LotteryService;
import com.ruyicai.actioncenter.util.JsonUtil;
import com.ruyicai.lottery.domain.CaseLot;
import com.ruyicai.lottery.domain.Tuserinfo;

@Service
public class CaselotBetFullListener {

	private Logger logger = LoggerFactory.getLogger(CaselotBetFullListener.class);

	@Autowired
	private LotteryService lotteryService;
	
	@Autowired
	private TuserPrizeDetailDao tuserPrizeDetailDao;

	@Produce(uri = "jms:topic:sendActivityPrize")
	private ProducerTemplate sendActivityPrizeProducer;

	@Transactional
	public void encashCustomer(@Body String body) {
		logger.info("合买满员:{}", new String[] { body });
		CaseLot caseLot = CaseLot.fromJsonToCaseLot(body);
		if (caseLot == null || caseLot.getStarter() == null || caseLot.getTotalAmt() == null) {
			logger.error("参数异常");
			return;
		}
		String starter = caseLot.getStarter();
		Tuserinfo tuserinfo = lotteryService.findTuserinfoByUserno(starter);
		if (tuserinfo == null) {
			logger.error("合买发起人从lottery中没有查询到");
			return;
		}
		Tactivity tactivity = Tactivity.findTactivity(null, null, tuserinfo.getSubChannel(), null,
				ActionJmsType.CASELOT_SUCCESS.value);
		BigDecimal prize = BigDecimal.ZERO;
		if (tactivity != null) {
			String express = tactivity.getExpress();
			Map<String, Object> activity = JsonUtil.transferJson2Map(express);
			Integer step1min = (Integer) activity.get("step1min");
			Integer step1max = (Integer) activity.get("step1max");
			Integer step1present = (Integer) activity.get("step1present");
			Integer step2 = (Integer) activity.get("step2");
			Integer step2present = (Integer) activity.get("step2present");
			if (caseLot.getTotalAmt() >= step1min && caseLot.getTotalAmt() <= step1max) {
				prize = new BigDecimal(caseLot.getTotalAmt()).multiply(new BigDecimal(step1present)).divide(
						new BigDecimal(100));
			} else if (caseLot.getTotalAmt() >= step2) {
				prize = new BigDecimal(caseLot.getTotalAmt()).multiply(new BigDecimal(step2present)).divide(
						new BigDecimal(100));
			}
			if (prize.compareTo(BigDecimal.ZERO) > 0) {
				if (Tjmsservice.createTjmsservice(caseLot.getId(), ActionJmsType.CASELOT_SUCCESS)) {
					logger.info("合买满员返奖{},caselotid:{},starter:{}", new String[] { prize.toString(), caseLot.getId(),
							starter });
					sendPrize2UserJMS(tuserinfo.getUserno(), prize, ActionJmsType.CASELOT_SUCCESS, caseLot.getId(),
							tactivity.getMemo());
				}
			}
		}
	}

	private void sendPrize2UserJMS(String userno, BigDecimal amt, ActionJmsType actionJmsType, String businessId,
			String memo) {
		TuserPrizeDetail userPrizeDetail = tuserPrizeDetailDao.createTprizeUserBuyLog(userno, amt, actionJmsType,
				businessId);
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("prizeDetailId", userPrizeDetail.getId());
		headers.put("actionJmsType", actionJmsType.value);
		headers.put("memo", memo);
		sendActivityPrizeProducer.sendBodyAndHeaders(null, headers);
	}
}
