package com.ruyicai.actioncenter.jms.listener;

import java.util.List;

import org.apache.camel.Body;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.actioncenter.service.LotteryService;
import com.ruyicai.lottery.domain.CaseLot;
import com.ruyicai.lottery.domain.CaseLotBuy;
import com.ruyicai.lottery.domain.Tuserinfo;

@Service
public class CaselotFinishListener {

	private Logger logger = LoggerFactory.getLogger(CaselotFinishListener.class);

	@Autowired
	private LotteryService lotteryService;

	@Autowired
	private OrderAfterBetListener orderAfterBetListener;

	public void caselotFinishCustomer(@Body String caseLotJson) {
		logger.info("合买结期 caseLotJson:" + caseLotJson);
		CaseLot caseLot = CaseLot.fromJsonToCaseLot(caseLotJson);
		// 合买期截 增加用户购彩金额、返点
		this.addVipCase(caseLot);
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
