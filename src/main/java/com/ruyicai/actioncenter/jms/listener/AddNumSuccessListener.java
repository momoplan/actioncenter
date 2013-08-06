package com.ruyicai.actioncenter.jms.listener;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Body;
import org.apache.camel.Header;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.consts.ActionJmsType;
import com.ruyicai.actioncenter.dao.TuserPrizeDetailDao;
import com.ruyicai.actioncenter.domain.Tactivity;
import com.ruyicai.actioncenter.domain.TaddNumActivity;
import com.ruyicai.actioncenter.domain.TuserPrizeDetail;
import com.ruyicai.actioncenter.service.LotteryService;
import com.ruyicai.actioncenter.util.DateUtil;
import com.ruyicai.actioncenter.util.JsonUtil;
import com.ruyicai.lottery.domain.Tsubscribe;
import com.ruyicai.lottery.domain.Tuserinfo;

@Service
public class AddNumSuccessListener {

	private Logger logger = LoggerFactory.getLogger(AddNumSuccessListener.class);

	@Autowired
	private LotteryService lotteryService;

	@Autowired
	private TuserPrizeDetailDao tuserPrizeDetailDao;

	@Produce(uri = "jms:topic:sendActivityPrize")
	private ProducerTemplate sendActivityPrizeProducer;

	@Transactional
	public void addNumSuccessCustomer(@Header("flowno") String flowno, @Header("userno") String userno) {
		logger.info("追号成功消息flowno:{},userno:{}", new String[] { flowno, userno });
		if (StringUtils.isBlank(flowno) || StringUtils.isBlank(userno)) {
			return;
		}
		TaddNumActivity addNumActivity = TaddNumActivity.findTaddNumActivityByFlownoAndUserno(flowno, userno);
		if (addNumActivity == null) {
			return;
		}
		Tuserinfo tuserinfo = lotteryService.findTuserinfoByUserno(userno);
		if (tuserinfo == null) {
			return;
		}
		if (tuserinfo != null && tuserinfo.getChannel() != null) {
			if (tuserinfo.getChannel().equals("991")) {
				logger.info("如意彩大户渠道不参加活动userno:" + userno);
				return;
			}
		}
		Tactivity tactivity = Tactivity.findTactivity(null, null, tuserinfo.getSubChannel(), null,
				ActionJmsType.AddNumOneYear.value);
		if (tactivity == null) {
			return;
		}
		TuserPrizeDetail prizeDetail = tuserPrizeDetailDao.findTuserPrizeDetailByUsernoAndActivityType(userno,
				ActionJmsType.AddNumOneYear.value);
		String express = tactivity.getExpress();
		Map<String, Object> activity = JsonUtil.transferJson2Map(express);
		Integer prize = (Integer) activity.get("prize");
		Date date = null;
		if (prizeDetail == null) {
			logger.info("第一次赠送追号包年套餐奖金");
			sendPrize2UserJMS(addNumActivity.getUserno(), new BigDecimal(prize), ActionJmsType.AddNumOneYear,
					tactivity.getMemo(), flowno);
		} else {
			date = prizeDetail.getCreateTime();
			String dateStr = DateUtil.format(date);
			BigDecimal count = lotteryService.findTtransactionSum(userno, dateStr);
			if (count != null) {
				if (count.compareTo(new BigDecimal(200)) < 0) {
					logger.info("取消用户追号包年套餐userno:" + userno);
					addNumActivity.setAddNumState(BigDecimal.ZERO);
					addNumActivity.setModifyTime(new Date());
					addNumActivity.merge();
				} else {
					logger.info("赠送用户追号包年套餐userno:" + userno + ",prize:" + prize);
					sendPrize2UserJMS(addNumActivity.getUserno(), new BigDecimal(prize), ActionJmsType.AddNumOneYear,
							tactivity.getMemo(), flowno);
				}
			}
		}
	}

	private void sendPrize2UserJMS(String userno, BigDecimal amt, ActionJmsType actionJmsType, String memo,
			String businessId) {
		TuserPrizeDetail userPrizeDetail = tuserPrizeDetailDao.createTprizeUserBuyLog(userno, amt, actionJmsType,
				businessId);
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("prizeDetailId", userPrizeDetail.getId());
		headers.put("actionJmsType", actionJmsType.value);
		headers.put("memo", memo);
		sendActivityPrizeProducer.sendBodyAndHeaders(null, headers);
	}

	@Transactional
	public void addNumSuccessJsonCustomer(@Body String body) {
		if (StringUtils.isBlank(body)) {
			logger.info("null return");
			return;
		}
		logger.info("addnumsuccessjson:" + body);
		Tsubscribe tsubscribe = Tsubscribe.fromJsonToTsubscribe(body);
		if (tsubscribe == null) {
			logger.info("null return");
			return;
		}
		if (tsubscribe.getBatchnum() == null || tsubscribe.getLastnum() == null) {
			logger.info("Batchnum or Lastnum is null");
			return;
		}
		String userno = tsubscribe.getUserno();
		Tuserinfo tuserinfo = lotteryService.findTuserinfoByUserno(userno);
		if (tuserinfo == null) {
			logger.info("null return");
			return;
		}
		Tactivity addNum15 = Tactivity.findTactivity(null, null, tuserinfo.getSubChannel(), null,
				ActionJmsType.AddNum15.value);
		if (addNum15 != null) {
			String express = addNum15.getExpress();
			Map<String, Object> activity = JsonUtil.transferJson2Map(express);
			Integer addnum = (Integer) activity.get("addnum");
			Integer maxprizeamt = (Integer) activity.get("maxprizeamt");
			BigDecimal subtract = tsubscribe.getBatchnum().subtract(tsubscribe.getLastnum());
			if (subtract.compareTo(new BigDecimal(addnum)) == 0) {
				BigDecimal minAmt = lotteryService.selectMinAmtBySubscribeno(tsubscribe.getFlowno());
				if (minAmt == null || minAmt.compareTo(BigDecimal.ZERO) <= 0) {
					logger.info("最小金额为空或小于等于0");
					return;
				}
				if (minAmt.compareTo(new BigDecimal(maxprizeamt)) > 0) {
					minAmt = new BigDecimal(maxprizeamt);
				}
				logger.info("追号15期赠送" + minAmt);
				sendPrize2UserJMS(userno, minAmt, ActionJmsType.AddNum15, addNum15.getMemo(), tsubscribe.getFlowno());
			}
		}
	}
}
