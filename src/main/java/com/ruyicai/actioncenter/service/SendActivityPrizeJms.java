package com.ruyicai.actioncenter.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.consts.ActionJmsType;
import com.ruyicai.actioncenter.dao.TuserPrizeDetailDao;
import com.ruyicai.actioncenter.domain.TuserPrizeDetail;

@Service
public class SendActivityPrizeJms {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private TuserPrizeDetailDao tuserPrizeDetailDao;

	@Produce(uri = "jms:topic:sendActivityPrize")
	private ProducerTemplate sendActivityPrizeProducer;

	/**
	 * 发送活动奖金到用户账号的JMS消息
	 * 
	 * @param userno
	 *            用户编号
	 * @param amt
	 *            奖金金额
	 * @param actionJmsType
	 *            活动类型
	 * @param memo
	 *            活动描述
	 * @param businessId
	 *            业务ID
	 * @param otherid
	 *            业务ID
	 * @param ttransactionid
	 *            充值交易号
	 */
	@Transactional
	public void sendPrize2UserJMS(String userno, BigDecimal amt, ActionJmsType actionJmsType, String memo,
			String businessId, String otherid, String ttransactionid) {
		TuserPrizeDetail userPrizeDetail = tuserPrizeDetailDao.createTprizeUserBuyLog(userno, amt, actionJmsType,
				businessId);
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("prizeDetailId", userPrizeDetail.getId());
		headers.put("actionJmsType", actionJmsType.value);
		headers.put("memo", memo);
		headers.put("flowno", businessId);
		headers.put("otherid", otherid);
		headers.put("ttransactionid", ttransactionid);
		logger.info("发送活动奖金JMS.prizeDetailId:{},actionJmsType:{},memo:{},flowno:{},otherid:{},ttransactionid:{}",
				new String[] { userPrizeDetail.getId() + "", actionJmsType.value + "", memo, businessId, otherid,
						ttransactionid });
		sendActivityPrizeProducer.sendBodyAndHeaders(null, headers);
	}
}
