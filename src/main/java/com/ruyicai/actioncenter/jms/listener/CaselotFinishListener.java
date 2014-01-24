package com.ruyicai.actioncenter.jms.listener;

import java.math.BigDecimal;
import java.util.List;

import org.apache.camel.Body;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.service.LotteryService;
import com.ruyicai.actioncenter.service.UserExperienceService;
import com.ruyicai.lottery.domain.CaseLot;
import com.ruyicai.lottery.domain.CaseLotBuy;
import com.ruyicai.lottery.domain.Tuserinfo;

@Service
public class CaselotFinishListener {
	
	private Logger logger = LoggerFactory.getLogger(CaselotFinishListener.class);
	
	@Autowired
	private LotteryService lotteryService;

	@Autowired
	private UserExperienceService userExperienceService;
	
	@Autowired
	private OrderAfterBetListener orderAfterBetListener;
	
	@Transactional
	public void caselotFinishCustomer(@Body String caseLotJson) {
		logger.info("合买结期 caseLotJson:" + caseLotJson);
		CaseLot caseLot = CaseLot.fromJsonToCaseLot(caseLotJson);
//		addUserExperienceVoteTime(caseLot.getId());	//增加用户体验官用户投票次数
		
		//合买期截 增加用户购彩金额、返点
		this.addVipCase(caseLot);
	}
	
	@Transactional
	public void addUserExperienceVoteTime(String caselotid) {
		List<CaseLotBuy> list = lotteryService.selectCaseLotBuysWithOutPage(caselotid);
		logger.info("增加合买的用户的投票次数 size：" + list.size());
		for (CaseLotBuy buy : list) {
			userExperienceService.addAvailableVoteTimesByBuyAMT(buy.getUserno(), buy.getNum().divide(new BigDecimal(200)).intValue());
		}
	}
	
	public void addVipCase(CaseLot caselot) {
		List<CaseLotBuy> list = lotteryService.selectCaseLotBuysWithOutPage(caselot.getId());
		for (CaseLotBuy caselotBuy : list) {
			Tuserinfo tuserinfo = lotteryService.findTuserinfoByUserno(caselotBuy.getUserno());
			if (tuserinfo == null) {
				return;
			}
			orderAfterBetListener.vipCase(tuserinfo, caselotBuy.getNum(), caselotBuy.getId().toString());
		}
	}
	
	
}
