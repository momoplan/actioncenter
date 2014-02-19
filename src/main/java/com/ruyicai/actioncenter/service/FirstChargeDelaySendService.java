package com.ruyicai.actioncenter.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.actioncenter.consts.ActionJmsType;
import com.ruyicai.actioncenter.domain.FirstChargeDelaySend;

@Service
public class FirstChargeDelaySendService {

	private Logger logger = LoggerFactory.getLogger(FirstChargeDelaySendService.class);

	@Autowired
	private SendActivityPrizeJms sendActivityPrizeJms;

	public void sendFirstChargeDelay() {
		Date currentDate = new Date();
		List<FirstChargeDelaySend> list = FirstChargeDelaySend.findFirstChargeDelaySendBySendTime(currentDate);
		for (FirstChargeDelaySend fcds : list) {
			try {
				if (fcds != null && fcds.getSendState() == 0) {
					sendActivityPrizeJms.sendPrize2UserJMS(fcds.getUserno(), fcds.getAmt(),
							ActionJmsType.FIRST_CHONGZHI_ZENGSONG_20, fcds.getMemo(), fcds.getBusinessId(), null, null);
					fcds.setSendState(1);
					fcds.merge();
				}
			} catch (Exception e) {
				logger.error("赠送首次充值延迟金额异常:" + fcds, e);
			}
		}
	}

}
