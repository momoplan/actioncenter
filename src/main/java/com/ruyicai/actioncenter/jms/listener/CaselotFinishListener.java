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

@Service
public class CaselotFinishListener {
	
	private Logger logger = LoggerFactory.getLogger(CaselotFinishListener.class);
	
	@Autowired
	private LotteryService lotteryService;

	@Autowired
	private UserExperienceService userExperienceService;
	
	@Transactional
	public void caselotFinishCustomer(@Body String caseLotJson) {
		logger.info("合买结期 caseLotJson:" + caseLotJson);
		CaseLot caseLot = CaseLot.fromJsonToCaseLot(caseLotJson);
		try {
			addUserExperienceVoteTime(caseLot.getId());	//增加用户体验官用户投票次数
		} catch (Exception e) {
			logger.error("增加用户体验官投票次数出错", e);
		}
	}
	
	@Transactional
	public void addUserExperienceVoteTime(String caselotid) {
		List<CaseLotBuy> list = lotteryService.selectCaseLotBuysWithOutPage(caselotid);
		logger.info("增加合买的用户的投票次数 size：" + list.size());
		for (CaseLotBuy buy : list) {
			userExperienceService.addAvailableVoteTimesByBuyAMT(buy.getUserno(), buy.getNum().divide(new BigDecimal(200)).intValue());
		}
	}
}
