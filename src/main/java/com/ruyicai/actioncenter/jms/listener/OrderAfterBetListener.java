package com.ruyicai.actioncenter.jms.listener;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
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
import com.ruyicai.actioncenter.domain.FirstOrder;
import com.ruyicai.actioncenter.domain.Tactivity;
import com.ruyicai.actioncenter.domain.Tjmsservice;
import com.ruyicai.actioncenter.domain.TuserPrizeDetail;
import com.ruyicai.actioncenter.service.LotteryService;
import com.ruyicai.actioncenter.service.UserExperienceService;
import com.ruyicai.actioncenter.util.DateUtil;
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
	
	@Autowired
	private UserExperienceService userExperienceService;

	public void orderAfterBetCustomer(@Body String orderJson) {
		if (StringUtils.isBlank(orderJson)) {
			return;
		}
		logger.info("订单出票成功orderJson:" + orderJson);
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
		if (tuserinfo.getChannel() != null && tuserinfo.getChannel().equals("991")) {
			logger.info("如意彩大户渠道不参加活动userno:" + userno);
			return;
		}
		ssqzengsong(order, tuserinfo);
		try {
			firstorder(order, tuserinfo);
		} catch (Exception e) {
			logger.error("广东快乐十分首单活动异常", e);
		}
		
//		try {
//			logger.info("→→→→→→→→→→→→→→→→→增加用户体验官投票次数");
//			addUserExperienceVoteTime(order.getUserno(), order.getAmt());
//		} catch (Exception e) {
//			logger.error("增加用户体验官投票次数出错", e);
//		}
	}

	@Transactional
	public void firstorder(Torder order, Tuserinfo tuserinfo) {
		Tactivity tactivity = Tactivity.findTactivity(order.getLotno(), null, tuserinfo.getSubChannel(), null,
				ActionJmsType.First_Order.value);
		if (tactivity != null) {
			Date regtime = tuserinfo.getRegtime();
			String regtimeStr = DateUtil.format("yyyyMMdd", regtime);
			String todayStr = DateUtil.format("yyyyMMdd", new Date());
			if (!regtimeStr.equals(todayStr)) {
				logger.info("非当天注册用户userno:" + tuserinfo.getUserno());
				return;
			}
			if (StringUtils.isBlank(tuserinfo.getMobileid())) {
				logger.info("未完善手机信息用户userno:" + tuserinfo.getUserno());
				return;
			}
			String express = tactivity.getExpress();
			Map<String, Object> activity = JsonUtil.transferJson2Map(express);
			Integer amt = (Integer) activity.get("amt");
			Integer prize = (Integer) activity.get("prize");
			if (amt == null || prize == null) {
				logger.error("参数不正确amt:{},prize:{}", new String[] { amt + "", prize + "" });
				return;
			}
			BigDecimal orderAmt = order.getAmt();
			if (orderAmt.compareTo(new BigDecimal(amt)) >= 0) {
				FirstOrder firstOrder = FirstOrder.findFirstOrder(tuserinfo.getUserno());
				if (firstOrder != null) {
					logger.info(tactivity.getMemo() + "活动已参加过userno:" + tuserinfo.getUserno());
					return;
				} else {
					FirstOrder fo = new FirstOrder();
					fo.setUserno(tuserinfo.getUserno());
					fo.setOrderid(order.getId());
					fo.setCreateTime(new Date());
					fo.persist();
					if (Tjmsservice.createTjmsservice(order.getId(), ActionJmsType.First_Order)) {
						logger.info(ActionJmsType.First_Order.memo + "prize:" + prize.longValue());
						sendPrize2UserJMS(tuserinfo.getUserno(), new BigDecimal(prize), ActionJmsType.First_Order,
								order.getId(), tactivity.getMemo());
					}
				}
			}
		}
	}

	public void ssqzengsong(Torder order, Tuserinfo tuserinfo) {
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
							sendPrize2UserJMS(tuserinfo.getUserno(), prize, ActionJmsType.Friday_SSQ_ZENGSONG,
									order.getId(), tactivity.getMemo());
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
	
	@Transactional
	public void addUserExperienceVoteTime(String userno, BigDecimal amt) {
		userExperienceService.addAvailableVoteTimesByBuyAMT(userno, amt.divide(new BigDecimal(200)).intValue());
	}
}
