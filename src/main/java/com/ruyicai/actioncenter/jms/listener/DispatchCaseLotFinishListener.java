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
import com.ruyicai.actioncenter.dao.TactivityDao;
import com.ruyicai.actioncenter.domain.Tactivity;
import com.ruyicai.actioncenter.domain.Tjmsservice;
import com.ruyicai.actioncenter.service.LotteryService;
import com.ruyicai.actioncenter.service.SendActivityPrizeJms;
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
	private TactivityDao tactivityDao;

	@Autowired
	private SendActivityPrizeJms sendActivityPrizeJms;

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
		if (userInfo != null && userInfo.getChannel() != null) {
			if (userInfo.getChannel().equals("991")) {
				logger.info("如意彩大户渠道不参加活动userno:" + starter);
				return;
			}
		}
		Torder torder = lotteryService.findTorderById(caseLot.getOrderid());
		if (torder == null) {
			return;
		}
		addPrize2Chuan1(caseLot, torder, userInfo);
		addPrizeLanQiu(caseLot, torder, userInfo);
		addPrizeJingcai2chuan1(caseLot, torder, userInfo);
		addPrize3D(caseLot, torder, userInfo);
	}
	
	@Transactional
	public void addPrize3D(CaseLot caseLot, Torder order, Tuserinfo tuserinfo) {
		Tactivity tactivity = tactivityDao.findTactivity(order.getLotno(), order.getPlaytype(),
				tuserinfo.getSubChannel(), null, ActionJmsType.FuCai3D_JiaJiang.value);
		if (tactivity != null) {
			String caselotId = caseLot.getId();
			Integer caselotprize = lotteryService.findCaseLotBuyAllPrizeamtById(caselotId, tuserinfo.getUserno());
			if (caselotprize > 0) {
				BigDecimal prize = BigDecimal.ZERO;
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer step = (Integer) activity.get("step");
				Integer prizeamt = (Integer) activity.get("prizeamt");
				Integer topprize = (Integer) activity.get("topprize");
				int multiple = caselotprize / step;
				if (multiple > 0) {
					prize = new BigDecimal(multiple * prizeamt);
				}
				if (prize.compareTo(new BigDecimal(topprize)) > 0) {
					prize = new BigDecimal(topprize);
				}
				if (prize.compareTo(BigDecimal.ZERO) > 0) {
					if (Tjmsservice.createTjmsservice(order.getId(), ActionJmsType.FuCai3D_JiaJiang)) {
						logger.info(tactivity.getMemo() + "prize:" + prize.longValue());
						sendActivityPrizeJms.sendPrize2UserJMS(tuserinfo.getUserno(), prize,
								ActionJmsType.FuCai3D_JiaJiang, tactivity.getMemo(), order.getId(), "", "");
					}
				}
			}
		}
	}

	@Transactional
	public void addPrizeJingcai2chuan1(CaseLot caseLot, Torder torder, Tuserinfo userinfo) {
		String userno = userinfo.getUserno();
		String lotno = torder.getLotno();
		if (!lotno.startsWith("J")) {
			return;
		}
		Tactivity tactivity = tactivityDao.findTactivity(null, torder.getPlaytype(), userinfo.getSubChannel(), null,
				ActionJmsType.Encash_Jingcai_2Chan1.value);
		if (tactivity != null) {
			String caselotId = caseLot.getId();
			Integer caselotprize = lotteryService.findCaseLotBuyAllPrizeamtById(caselotId, userinfo.getUserno());
			if (caselotprize > 0) {
				BigDecimal prize = BigDecimal.ZERO;
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer step1 = (Integer) activity.get("step1");
				Integer step1prize = (Integer) activity.get("step1prize");
				Integer step2 = (Integer) activity.get("step2");
				Integer step2prize = (Integer) activity.get("step2prize");
				Integer step3 = (Integer) activity.get("step3");
				Integer step3prize = (Integer) activity.get("step3prize");
				Integer step4 = (Integer) activity.get("step4");
				Integer step4prize = (Integer) activity.get("step4prize");
				Integer step5 = (Integer) activity.get("step5");
				Integer step5prize = (Integer) activity.get("step5prize");
				Integer step6 = (Integer) activity.get("step6");
				Integer step6prize = (Integer) activity.get("step6prize");
				Integer step7 = (Integer) activity.get("step7");
				Integer step7prize = (Integer) activity.get("step7prize");
				if (caselotprize >= step1 && caselotprize < step2) {
					prize = new BigDecimal(step1prize);
				} else if (caselotprize >= step2 && caselotprize < step3) {
					prize = new BigDecimal(step2prize);
				} else if (caselotprize >= step3 && caselotprize < step4) {
					prize = new BigDecimal(step3prize);
				} else if (caselotprize >= step4 && caselotprize < step5) {
					prize = new BigDecimal(step4prize);
				} else if (caselotprize >= step5 && caselotprize < step6) {
					prize = new BigDecimal(step5prize);
				} else if (caselotprize >= step6 && caselotprize < step7) {
					prize = new BigDecimal(step6prize);
				} else if (caselotprize >= step7) {
					prize = new BigDecimal(step7prize);
				}
				if (prize.compareTo(BigDecimal.ZERO) > 0) {
					if (Tjmsservice.createTjmsservice(caselotId, ActionJmsType.Encash_Jingcai_2Chan1)) {
						logger.info(tactivity.getMemo() + prize);
						sendActivityPrizeJms.sendPrize2UserJMS(userno, prize, ActionJmsType.Encash_Jingcai_2Chan1,
								tactivity.getMemo(), caselotId, "", "");
					}
				}
			}
		}
	}

	@Transactional
	public void addPrize2Chuan1(CaseLot caseLot, Torder torder, Tuserinfo userinfo) {
		Tactivity tactivity = tactivityDao.findTactivity(torder.getLotno(), torder.getPlaytype(),
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
						sendActivityPrizeJms.sendPrize2UserJMS(userinfo.getUserno(), prize,
								ActionJmsType.Encash_2chuan1_AddPrize, tactivity.getMemo(), caselotId, "", "");
					}
				}
			}
		}
	}

	@Transactional
	public void addPrizeLanQiu(CaseLot caseLot, Torder torder, Tuserinfo userinfo) {
		Tactivity tactivity = tactivityDao.findTactivity(torder.getLotno(), null, userinfo.getSubChannel(), null,
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
						sendActivityPrizeJms.sendPrize2UserJMS(userinfo.getUserno(), prize,
								ActionJmsType.Encash_LanQiu_AddPrize, tactivity.getMemo(), caselotId, "", "");
					}
				}
			}
		}
	}
}
