package com.ruyicai.actioncenter.jms.listener;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.camel.Body;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.consts.ActionJmsType;
import com.ruyicai.actioncenter.domain.Tactivity;
import com.ruyicai.actioncenter.domain.Tjmsservice;
import com.ruyicai.actioncenter.service.LotteryService;
import com.ruyicai.actioncenter.util.JsonUtil;
import com.ruyicai.lottery.domain.CaseLot;
import com.ruyicai.lottery.domain.Torder;
import com.ruyicai.lottery.domain.Tuserinfo;

@Service
public class DispatchCaseLotFinishListener {

	private Logger logger = LoggerFactory.getLogger(DispatchCaseLotFinishListener.class);

	@Autowired
	private LotteryService lotteryService;

	@Autowired
	private OrderEncashListener orderEncashListener;

	public void dispatchCaseLotFinishCustomer(@Body String caseLotJson) {
		logger.info("合买派奖完成caseLotJson:" + caseLotJson);
		if (StringUtils.isBlank(caseLotJson)) {
			throw new IllegalArgumentException("the arguments caseLotJson  is require");
		}
		CaseLot caseLot = CaseLot.fromJsonToCaseLot(caseLotJson);
		if (caseLot == null) {
			return;
		}
		String starter = caseLot.getStarter();
		if (StringUtils.isBlank(starter)) {
			return;
		}
		Tuserinfo userInfo = lotteryService.findTuserinfoByUserno(starter);
		if (userInfo == null) {
			return;
		}
		Torder torder = lotteryService.findTorderById(caseLot.getOrderid());
		if (torder == null) {
			return;
		}
		addPrize2Chuan1(caseLot, torder, userInfo);
		addPrizeLanQiu(caseLot, torder, userInfo);
	}

	@Transactional
	public void addPrize2Chuan1(CaseLot caseLot, Torder torder, Tuserinfo userinfo) {
		Tactivity tactivity = Tactivity.findTactivity(torder.getLotno(), torder.getPlaytype(),
				userinfo.getSubChannel(), null, ActionJmsType.Encash_2chuan1_AddPrize.value);
		if (tactivity != null) {
			String caselotId = caseLot.getId();
			Integer caselotprize = lotteryService.findCaseLotBuyAllPrizeamtById(caselotId, userinfo.getUserno());
			if (caselotprize > 0) {
				BigDecimal prize = BigDecimal.ZERO;
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer step = (Integer) activity.get("step");
				Integer prizeamt = (Integer) activity.get("prizeamt");
				int multiple = caselotprize / step;
				if (multiple > 0) {
					prize = new BigDecimal(multiple * prizeamt);
				}
				if (prize.compareTo(BigDecimal.ZERO) > 0) {
					if (Tjmsservice.createTjmsservice(caselotId, ActionJmsType.Encash_2chuan1_AddPrize)) {
						logger.info(tactivity.getMemo() + "合买中奖加奖prize:" + prize.longValue());
						orderEncashListener.sendPrize2UserJMS(userinfo.getUserno(), prize,
								ActionJmsType.Encash_2chuan1_AddPrize, caseLot.getId(), tactivity.getMemo());
					}
				}
			}
		}
	}

	@Transactional
	public void addPrizeLanQiu(CaseLot caseLot, Torder torder, Tuserinfo userinfo) {
		Tactivity tactivity = Tactivity.findTactivity(torder.getLotno(), null, userinfo.getSubChannel(), null,
				ActionJmsType.Encash_LanQiu_AddPrize.value);
		if (tactivity != null) {
			String caselotId = caseLot.getId();
			Integer caselotprize = lotteryService.findCaseLotBuyAllPrizeamtById(caselotId, userinfo.getUserno());
			if (caselotprize > 0) {
				BigDecimal prize = BigDecimal.ZERO;
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer step1min = (Integer) activity.get("step1min");
				Integer step1max = (Integer) activity.get("step1max");
				Integer step1prizeamt = (Integer) activity.get("step1prizeamt");
				if (caselotprize >= step1min && caselotprize <= step1max) {
					prize = new BigDecimal(step1prizeamt);
				} else {
					Integer step = (Integer) activity.get("step");
					Integer prizeamt = (Integer) activity.get("prizeamt");
					int multiple = caselotprize.intValue() / step;
					if (multiple > 0) {
						prize = new BigDecimal(multiple * prizeamt);
					}
				}
				if (prize.compareTo(BigDecimal.ZERO) > 0) {
					if (Tjmsservice.createTjmsservice(caselotId, ActionJmsType.Encash_LanQiu_AddPrize)) {
						logger.info(tactivity.getMemo() + "合买中奖加奖prize:" + prize.longValue());
						orderEncashListener.sendPrize2UserJMS(userinfo.getUserno(), prize,
								ActionJmsType.Encash_LanQiu_AddPrize, caseLot.getId(), tactivity.getMemo());
					}
				}
			}
		}
	}
}
