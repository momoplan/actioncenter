package com.ruyicai.actioncenter.jms.listener;

import org.apache.camel.Header;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.consts.ActionJmsType;
import com.ruyicai.actioncenter.dao.TactivityDao;
import com.ruyicai.actioncenter.dao.TuserPrizeDetailDao;
import com.ruyicai.actioncenter.domain.FundAndJoinAction;
import com.ruyicai.actioncenter.domain.Tactivity;
import com.ruyicai.actioncenter.domain.Tjmsservice;
import com.ruyicai.actioncenter.domain.TuserPrizeDetail;
import com.ruyicai.actioncenter.service.LotteryService;
import com.ruyicai.lottery.domain.Tuserinfo;

@Service
public class SendActivityPrizeListener {

	private Logger logger = LoggerFactory.getLogger(SendActivityPrizeListener.class);

	@Autowired
	private LotteryService lotteryService;

	@Autowired
	private TactivityDao tactivityDao;

	@Autowired
	private TuserPrizeDetailDao tuserPrizeDetailDao;

	@Transactional
	public void sendActivityPrizeCustomer(@Header("prizeDetailId") Long prizeDetailId,
			@Header("actionJmsType") Integer actionJmsType, @Header("ttransactionid") String ttransactionid,
			@Header("memo") String memo, @Header("flowno") String flowno, @Header("otherid") String otherid) {
		logger.info("发送奖励prizeDetailId:{},actionJmsType:{},ttransactionid:{},memo:{},flowno:{},otherid:{}",
				new String[] { prizeDetailId + "", actionJmsType + "", ttransactionid, memo, flowno, otherid });
		if (StringUtils.isBlank(memo)) {
			ActionJmsType type = ActionJmsType.get(actionJmsType);
			if (type != null && StringUtils.isNotBlank(type.memo)) {
				memo = type.memo;
			}
		}
		TuserPrizeDetail detail = tuserPrizeDetailDao.findTuserPrizeDetail(prizeDetailId, true);
		if (detail != null && detail.getState() == 0) {
			Tuserinfo tuserinfo = lotteryService.findTuserinfoByUserno(detail.getUserno());
			Boolean flag = lotteryService.directChargeProcess(tuserinfo.getUserno(), detail.getAmt(),
					tuserinfo.getSubChannel(), tuserinfo.getChannel(), memo, flowno, otherid);
			if (flag) {
				detail.setState(1);
				logger.info("TuserPrizeDetail id:{},prizeAmt:{},userno:{},actionJmsType:{}.奖励成功", new String[] {
						prizeDetailId + "", detail.getAmt() + "", detail.getUserno(), actionJmsType + "" });
				if (StringUtils.isNotBlank(ttransactionid)) {
					if (Tjmsservice.createTjmsservice(ttransactionid + actionJmsType,
							ActionJmsType.SEND_ACTION_PRIZE_SUCCESS)) {
						try {
							Tactivity tactivity = tactivityDao.findTactivity(null, null, "00092493", null,
									ActionJmsType.Fund2Draw.value);
							if (tactivity != null) {
								FundAndJoinAction.createFundAndJoinAction(ttransactionid, detail.getUserno(),
										actionJmsType, detail.getId());
							} else {
								lotteryService.deductDrawBalance(tuserinfo.getUserno(), ttransactionid);
							}
						} catch (Exception e) {
							// logger.error("减少可提现金额失败", e);
							logger.error("保存参与活动的充值记录失败", e);
						}
					}
				}
			} else {
				detail.setState(2);
				logger.error("TuserPrizeDetail id:{},prizeAmt:{},userno:{},actionJmsType:{}.奖励失败", new String[] {
						prizeDetailId + "", detail.getAmt() + "", detail.getUserno(), actionJmsType + "" });
			}
			tuserPrizeDetailDao.merge(detail);
		}
	}
}
