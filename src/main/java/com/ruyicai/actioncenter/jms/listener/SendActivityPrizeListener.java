package com.ruyicai.actioncenter.jms.listener;

import java.util.Map;

import org.apache.camel.Header;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.consts.ActionJmsType;
import com.ruyicai.actioncenter.dao.TuserPrizeDetailDao;
import com.ruyicai.actioncenter.domain.FundAndJoinAction;
import com.ruyicai.actioncenter.domain.Tactivity;
import com.ruyicai.actioncenter.domain.Tjmsservice;
import com.ruyicai.actioncenter.domain.TuserPrizeDetail;
import com.ruyicai.actioncenter.service.LotteryService;
import com.ruyicai.actioncenter.util.JsonUtil;
import com.ruyicai.lottery.domain.Tuserinfo;

@Service
public class SendActivityPrizeListener {

	private Logger logger = LoggerFactory.getLogger(SendActivityPrizeListener.class);

	@Autowired
	private LotteryService lotteryService;

	@Autowired
	private TuserPrizeDetailDao tuserPrizeDetailDao;

	@Transactional
	public void sendActivityPrizeCustomer(@Header("prizeDetailId") Long prizeDetailId,
			@Header("actionJmsType") Integer actionJmsType, @Header("ttransactionid") String ttransactionid,
			@Header("memo") String memo) {
		logger.info("发送奖励prizeDetailId:{},actionJmsType:{}", new String[] { prizeDetailId + "", actionJmsType + "" });
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
					tuserinfo.getSubChannel(), tuserinfo.getChannel(), memo);
			if (flag) {
				detail.setState(1);
				logger.info("TuserPrizeDetail id:{},prizeAmt:{},userno:{},actionJmsType:{}.奖励成功", new String[] {
						prizeDetailId + "", detail.getAmt() + "", detail.getUserno(), actionJmsType + "" });
				if (StringUtils.isNotBlank(ttransactionid)) {
					if (Tjmsservice.createTjmsservice(ttransactionid + actionJmsType,
							ActionJmsType.SEND_ACTION_PRIZE_SUCCESS)) {
						try {
							Tactivity tactivity = Tactivity.findTactivity(null, null, "00092493", null,
									ActionJmsType.Fund2Draw.value);
							if (tactivity != null) {
								String express = tactivity.getExpress();
								Map<String, Object> activity = JsonUtil.transferJson2Map(express);
								Integer state = (Integer) activity.get("state");
								if (state != null && state == 1) {
									FundAndJoinAction.createFundAndJoinAction(ttransactionid, detail.getUserno(),
											actionJmsType, detail.getId());
								} else {
									lotteryService.deductDrawBalance(tuserinfo.getUserno(), ttransactionid);
								}
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
