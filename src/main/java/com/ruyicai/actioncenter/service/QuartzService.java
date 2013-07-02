package com.ruyicai.actioncenter.service;

import java.io.Serializable;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.ruyicai.actioncenter.util.DateUtil;
import com.ruyicai.actioncenter.util.SpringUtils;

@Service
public class QuartzService implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(QuartzService.class);

	/**
	 * 定时调度执行增加充值可提现金额
	 */
	public void scheduleFindFund2Draw() {
		logger.info("定时循环执行增加充值可提现金额任务" + DateUtil.format(new Date()));
		Fund2DrawService fund2DrawService = SpringUtils.getBean(Fund2DrawService.class);
		fund2DrawService.quartzFindFund2Draw();
	}

}
