package com.ruyicai.actioncenter.service;

import java.io.Serializable;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ruyicai.actioncenter.util.DateUtil;
import com.ruyicai.actioncenter.util.SpringUtils;

@Service
public class QuartzService implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(QuartzService.class);

	/**
	 * 定时调度执行增加充值可提现金额
	 */
	public void scheduleFindFund2Draw() {
		logger.info("定时循环执行增加充值可提现金额任务" + DateUtil.format(new Date()));
		Fund2DrawService fund2DrawService = SpringUtils.getBean(Fund2DrawService.class);
		fund2DrawService.quartzFindFund2Draw();
	}

	/**
	 * 定时执行增加延迟赠送金额
	 */
	public void scheduleFirstChargeDelaySend() {
		logger.info("定时执行首次充值赠送延迟赠送金额" + DateUtil.format(new Date()));
		FirstChargeDelaySendService fcdsService = SpringUtils.getBean(FirstChargeDelaySendService.class);
		fcdsService.sendFirstChargeDelay();
	}

}
