package com.ruyicai.actioncenter.jms.listener;

import org.apache.camel.Header;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.consts.ActionJmsType;
import com.ruyicai.actioncenter.domain.Tjmsservice;
import com.ruyicai.actioncenter.domain.TuserPrizeDetail;
import com.ruyicai.actioncenter.service.LotteryService;
import com.ruyicai.lottery.domain.Tuserinfo;

@Service
public class SendActivityPrizeListener {

	private Logger logger = LoggerFactory.getLogger(SendActivityPrizeListener.class);

	@Autowired
	private LotteryService lotteryService;

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
		TuserPrizeDetail detail = TuserPrizeDetail.findTuserPrizeDetail(prizeDetailId, true);
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
							lotteryService.deductDrawBalance(tuserinfo.getUserno(), ttransactionid);
						} catch (Exception e) {
							logger.error("减少可提现金额失败", e);
						}
					}
				}
				/*
				 * if (actionJmsType ==
				 * ActionJmsType.OLD_USER_CHONGZHI_ZENGSONG.value) { if
				 * (StringUtils.isNotBlank(detail.getUserno())) {
				 * logger.info("赠送成功更新updateStateDisable,userno:" +
				 * detail.getUserno()); try {
				 * OldUser.updateStateDisable(detail.getUserno(), true); } catch
				 * (Exception e) {
				 * logger.error("赠送成功更新updateStateDisable error", e); } } } if
				 * (actionJmsType ==
				 * ActionJmsType.Encash_DuoLeCai_AddPrize.value) { String day =
				 * DateUtil.format("yyyy-MM-dd", new Date()); try {
				 * SSCPrizedDetail.addPrize(tuserinfo.getUserno(), day,
				 * detail.getAmt()); } catch (Exception e) {
				 * logger.error("更新多乐彩11选5用户中奖累计金额异常", e); } }
				 */
			} else {
				detail.setState(2);
				logger.error("TuserPrizeDetail id:{},prizeAmt:{},userno:{},actionJmsType:{}.奖励失败", new String[] {
						prizeDetailId + "", detail.getAmt() + "", detail.getUserno(), actionJmsType + "" });
			}
			detail.merge();
		}
	}
}
