package com.ruyicai.actioncenter.jms.listener;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Body;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.consts.ActionJmsType;
import com.ruyicai.actioncenter.dao.TuserPrizeDetailDao;
import com.ruyicai.actioncenter.domain.Tactivity;
import com.ruyicai.actioncenter.domain.Tjmsservice;
import com.ruyicai.actioncenter.domain.TuserPrizeDetail;
import com.ruyicai.actioncenter.service.LotteryService;
import com.ruyicai.actioncenter.util.JsonUtil;
import com.ruyicai.lottery.domain.Torder;
import com.ruyicai.lottery.domain.Tuserinfo;

@Service
public class OrderAfterBetListener {

	private Logger logger = LoggerFactory.getLogger(OrderAfterBetListener.class);

	@Autowired
	private LotteryService lotteryService;
	
	@Autowired
	private TuserPrizeDetailDao tuserPrizeDetailDao;

	@Produce(uri = "jms:topic:sendActivityPrize")
	private ProducerTemplate sendActivityPrizeProducer;

	@Value("${ruyicaiUserno}")
	private String ruyicaiUserno;

	public void orderAfterBetCustomer(@Body String orderJson) {
		if (StringUtils.isBlank(orderJson)) {
			return;
		}
		Torder order = JsonUtil.fromJsonToObject(orderJson, Torder.class);
		if (order == null) {
			return;
		}
		String userno = order.getUserno();
		if (userno.equals(ruyicaiUserno)) {
			logger.info("如意彩账户购买,不赠送userno:" + userno);
			return;
		}
		Tuserinfo tuserinfo = lotteryService.findTuserinfoByUserno(userno);
		if (tuserinfo == null) {
			return;
		}
		Tactivity tactivity = Tactivity.findTactivity(order.getLotno(), null, tuserinfo.getSubChannel(), null,
				ActionJmsType.Friday_SSQ_ZENGSONG.value);
		if (tactivity != null) {
			String express = tactivity.getExpress();
			Map<String, Object> activity = JsonUtil.transferJson2Map(express);
			String week = (String) activity.get("week");
			Integer step = (Integer) activity.get("step");
			Integer prizeamt = (Integer) activity.get("prizeamt");
			if (week == null || step == null || prizeamt == null) {
				logger.error("参数不正确week:{},step:{},prizeamt:{}", new String[] { week + "", step + "", prizeamt + "" });
				return;
			}
			Calendar cal = Calendar.getInstance();
			String cw = cal.get(Calendar.DAY_OF_WEEK) - 1 + "";
			if (week.indexOf(cw) != -1) {
				Long amt = order.getAmt().longValue();
				if (amt >= step) {
					BigDecimal prize = new BigDecimal(prizeamt);
					if (prize.compareTo(BigDecimal.ZERO) > 0) {
						if (Tjmsservice.createTjmsservice(order.getId(), ActionJmsType.Friday_SSQ_ZENGSONG)) {
							logger.info(ActionJmsType.Friday_SSQ_ZENGSONG.memo + "prize:" + prize.longValue());
							sendPrize2UserJMS(userno, prize, ActionJmsType.Friday_SSQ_ZENGSONG, order.getId(),
									tactivity.getMemo());
						}
					}
				}
			}
		}
	}

	@Transactional
	public void sendPrize2UserJMS(String userno, BigDecimal amt, ActionJmsType actionJmsType, String businessId,
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
