package com.ruyicai.actioncenter.jms.listener;

import org.apache.camel.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.domain.CouponBatch;
import com.ruyicai.actioncenter.domain.CouponBatchChannel;

@Service
public class UseCouponListener {
	
	private Logger logger = LoggerFactory.getLogger(UseCouponListener.class);
	
	@Transactional
	public void updateCouponBatchAndChannel(@Header("couponBatchId") String couponBatchId,
			@Header("couponBatchChannelId") Long couponBatchChannelId) {
		logger.info("更新使用兑换券以后的批次和渠道信息 couponBatchId:{},couponBatchChannelId:{}", new String[] {couponBatchId, couponBatchChannelId + ""});
		try {
			CouponBatch couponBatch = CouponBatch.find(couponBatchId, true);
			couponBatch.setCouponbatchusage(couponBatch.getCouponbatchusage() + 1);
			couponBatch.merge();
			CouponBatchChannel channel = CouponBatchChannel.find(couponBatchChannelId, true);
			channel.setCouponusage(channel.getCouponusage() + 1);
			channel.merge();
		} catch (Exception e) {
			logger.error("更新批次和渠道失败", new String[] { e.getMessage() }, e);
		}
	}
}
