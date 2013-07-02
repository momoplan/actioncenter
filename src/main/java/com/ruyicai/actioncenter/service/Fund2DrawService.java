package com.ruyicai.actioncenter.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Header;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.consts.ActionJmsType;
import com.ruyicai.actioncenter.consts.Fund2DrawState;
import com.ruyicai.actioncenter.dao.Fund2DrawDao;
import com.ruyicai.actioncenter.domain.Fund2Draw;
import com.ruyicai.actioncenter.domain.FundAndJoinAction;
import com.ruyicai.actioncenter.domain.Tactivity;
import com.ruyicai.actioncenter.util.JsonUtil;

@Service
public class Fund2DrawService {

	private Logger logger = LoggerFactory.getLogger(Fund2DrawService.class);

	@Autowired
	private Fund2DrawDao fund2DrawDao;

	@Autowired
	private LotteryService lotteryService;

	@Produce(uri = "jms:topic:sendFund2Draw")
	private ProducerTemplate sendFund2Draw;

	public void quartzFindFund2Draw() {
		Tactivity tactivity = Tactivity.findTactivity(null, null, "00092493", null, ActionJmsType.Fund2Draw.value);
		if (tactivity != null) {
			String express = tactivity.getExpress();
			Map<String, Object> activity = JsonUtil.transferJson2Map(express);
			Integer state = (Integer) activity.get("state");
			if (state != null && state == 1) {
				logger.info("查询待增加可提现充值");
				List<Fund2Draw> list = fund2DrawDao.findCanFund2Draw(Fund2DrawState.waitToDraw.value(), new Date());
				for (Fund2Draw draw : list) {
					if (draw != null && draw.getTtransactionid() != null) {
						sendFund2DrawJMS(draw.getTtransactionid());
					}
				}
			}
		}
	}

	public void sendFund2DrawJMS(String ttransactionid) {
		logger.info("发送增加可提现充值ttransactionid:" + ttransactionid);
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("ttransactionid", ttransactionid);
		sendFund2Draw.sendBodyAndHeaders(null, headers);
	}

	@Transactional
	public void fund2Draw(@Header("ttransactionid") String ttransactionid) {
		Fund2Draw draw = fund2DrawDao.findFund2Draw(ttransactionid, true);
		if (draw.getState() != Fund2DrawState.waitToDraw.value()) {
			logger.info("该充值ttransactionid:" + ttransactionid + "状态非等待状态state:" + draw.getState());
			return;
		}
		FundAndJoinAction action = FundAndJoinAction.findFundAndJoinAction(ttransactionid);
		if (action != null) {
			logger.info("该充值已参加活动，不可提现ttransactionid:" + ttransactionid);
			draw.setState(Fund2DrawState.haveJoinAction.value());
			fund2DrawDao.merge(draw);
			return;
		}
		Boolean flag = lotteryService.fund2Draw(ttransactionid);
		if (flag) {
			draw.setState(Fund2DrawState.haveDraw.value());
			draw.setDrawTime(new Date());
			fund2DrawDao.merge(draw);
			logger.info("充值增加提现ttransactionid:" + ttransactionid + "已调用lottery完毕");
		} else {
			logger.info("充值增加提现ttransactionid:" + ttransactionid + "调用lottery失败");
		}
	}

}
