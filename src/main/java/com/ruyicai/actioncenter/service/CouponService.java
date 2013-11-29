package com.ruyicai.actioncenter.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.consts.ActionJmsType;
import com.ruyicai.actioncenter.domain.Coupon;
import com.ruyicai.actioncenter.domain.CouponBatch;
import com.ruyicai.actioncenter.domain.CouponBatchChannel;
import com.ruyicai.actioncenter.domain.CouponBatchUsageDetail;
import com.ruyicai.actioncenter.domain.CouponBatchUsageDetailPK;
import com.ruyicai.actioncenter.exception.RuyicaiException;
import com.ruyicai.actioncenter.util.DateUtil;
import com.ruyicai.actioncenter.util.ErrorCode;
import com.ruyicai.lottery.domain.Tuserinfo;

@Service
public class CouponService {
	private Logger logger = LoggerFactory.getLogger(CouponService.class);
	
	@Autowired
	private TactionService tactionService;
	
	@Autowired
	private LotteryService lotteryService;
	
	@Autowired
	private AsyncService asyncService;
	
	@Produce(uri = "jms:topic:updateCouponBatchAndChannel")
	private ProducerTemplate updateCouponBatchAndChannelProducer;
	
	/**
	 * 创建兑换券批次
	 * @param couponBatchName
	 * @param reusable
	 * @return CouponBatch
	 */
	@Transactional
	public CouponBatch createCouponBatch(String couponBatchName, Boolean reusable) {
		if(StringUtils.isBlank(couponBatchName)) {
			throw new IllegalArgumentException("the argument couponBatchName is required");
		}
		if(reusable ==  null) {
			throw new IllegalArgumentException("the argument reusable is required");
		}
		logger.info("createCouponBatch couponBachName:" + couponBatchName + "  reusable:" + reusable);
		CouponBatch couponBatch = CouponBatch.createCouponBatch(couponBatchName, reusable);
		return couponBatch;
	}
	
	/**
	 * 创建兑换券批次渠道
	 * @param couponBatchId	批次id
	 * @param channelName	渠道名称
	 * @param memo				渠道说明
	 * @param channelNo				渠道号
	 * @return CouponBatchChannel
	 */
	@Transactional
	public CouponBatchChannel createCouponBatchChannel(String couponBatchId, String channelName, String memo, String channelNo) {
		if(StringUtils.isBlank(couponBatchId)) {
			throw new IllegalArgumentException("the argument couponBatchId is required");
		}
		if(StringUtils.isBlank(channelName)) {
			throw new IllegalArgumentException("the argument channelName is required");
		}
		if(StringUtils.isBlank(memo)) {
			throw new IllegalArgumentException("the argument memo is required");
		}
		if(StringUtils.isBlank(channelNo)) {
			throw new IllegalArgumentException("The argument channelNo is required");
		}
		logger.info("createCouponBatchChannel couponBatchId:" + couponBatchId + " channelName:" + channelName + " memo:" + memo + " channelNo:" + channelNo);
		CouponBatchChannel channel = CouponBatchChannel.create(couponBatchId, channelName, memo, channelNo);
		return channel;
	}
	
	/**
	 * 创建兑换券，并且更新批次、渠道信息<br />
	 * 同渠道只可以创建一次兑换券，不可添加新兑换券。
	 * @param couponBatchId					批次id
	 * @param couponBatchChannelId		渠道id
	 * @param couponQuantity				创建兑换券数量
	 * @param validity								有效期
	 * @param couponAmount					兑换券金额
	 */
	@Transactional
	public void createCoupons(String couponBatchId, Long couponBatchChannelId, Integer couponQuantity, String validity, BigDecimal couponAmount) {
		if(StringUtils.isBlank(couponBatchId)) {
			throw new IllegalArgumentException("the argument couponBatchId is required");
		}
		if(couponBatchChannelId == null) {
			throw new IllegalArgumentException("the argument couponBatchChannelId is required");
		}
		if(couponQuantity == null) {
			throw new IllegalArgumentException("the argument couponQuantity is required");
		}
		if(StringUtils.isBlank(validity)) {
			throw new IllegalArgumentException("the argument validity is required");
		}
		if(couponAmount == null) {
			throw new IllegalArgumentException("the argument couponAmount is required");
		}
		logger.info("createCoupons couponBatchId:" + couponBatchId + " couponBatchChannelId:" + couponBatchChannelId + " couponQuantity:" + couponQuantity + " validity:" + validity + " couponAmount:" + couponAmount);
		//更新批次数量、总金额
		CouponBatch couponBatch = CouponBatch.findCouponBatch(couponBatchId);
		couponBatch.setCouponbatchquantity(couponBatch.getCouponbatchquantity() + couponQuantity);
		couponBatch.setTotalamount(couponBatch.getTotalamount().add(couponAmount.multiply(new BigDecimal(couponQuantity))));
		couponBatch.merge();
		//更新渠道数量、总金额
		CouponBatchChannel channel = CouponBatchChannel.findCouponBatchChannel(couponBatchChannelId);
		channel.setCouponquantity(couponQuantity);
		channel.setTotalamount(couponAmount.multiply(new BigDecimal(couponQuantity)));
		channel.merge();
		//创建兑换券
		doCreateCoupons(couponBatchId, couponBatchChannelId, couponQuantity, couponBatch.getReusable(), validity, couponAmount);
	}
	
	/**
	 * 创建兑换券
	 * @param couponBatchId					兑换券批号
	 * @param couponBatchChannelId 		兑换券批次Id
	 * @param couponQuantity				兑换券数量
	 * @param reusable								是否可以重复使用
	 * @param validity								有效期
	 * @param couponAmount					兑换券金额
	 */
	@Transactional
	public void doCreateCoupons(String couponBatchId, long couponBatchChannelId, int couponQuantity, Boolean reusable, String validity, BigDecimal couponAmount) {
		logger.info("doCreateCoupons couponBatchId:" + couponBatchId + " couponBatchChannelId:" + couponBatchChannelId + " couponQuantity:" + couponQuantity + " reusable:" + reusable + " validity:" + validity + " couponAmount:" + couponAmount);
		Date date = new Date();
		for(int i = 0; i < couponQuantity; i++) {	
			try {
				Coupon.create(getRandomString(12), reusable, couponBatchChannelId, couponBatchId, couponAmount, DateUtil.parse(validity), date);
			} catch (Exception e) {
				logger.error("doCreateCoupons error", e.getMessage(), e);
			}
		}	
	}
	
	/**
	 * 生成随机码
	 * @param length	生成的长度
	 * @return
	 */
	private String getRandomString(int length) {
		String base = "ABCDEFGHIJKLMNPQRSTUVWXYZ0123456789";   
		Random random = new Random();   
		StringBuffer sb = new StringBuffer();   
		for (int i = 0; i < length; i++) {   
		    int number = random.nextInt(base.length());   
		    sb.append(base.charAt(number));   
		}   
		return sb.toString();  
	}
	
	/**
	 * 兑换兑换券
	 * @param userno				要兑换的用户
	 * @param couponCode		兑换券号
	 * @return Coupon
	 */
	@Transactional
	public Coupon exchangeCoupon(String userno, String couponCode) {
		if(StringUtils.isBlank(couponCode)) {
			throw new IllegalArgumentException("the argument couponCode is required");
		}
		if(StringUtils.isBlank(userno)) {
			throw new IllegalArgumentException("the argument userno is required");
		}
		logger.info("exchangeCoupon userno:" + userno + " couponCode:" + couponCode);
		Tuserinfo tuserinfo = lotteryService.findTuserinfoByUserno(userno);
		if(tuserinfo == null) {
			throw new RuyicaiException(ErrorCode.UserMod_UserNotExists);
		}
		String mobile = tuserinfo.getMobileid();
		if(StringUtils.isBlank(mobile)) {
			throw new RuyicaiException(ErrorCode.UserMod_MobileidNotBind);
		}
		Coupon coupon = Coupon.findCoupon(couponCode, true);
		if(coupon == null) {	//兑换券不存在
			throw new RuyicaiException(ErrorCode.CouponNotExist);
		} else {	//兑换券存在
			if(new Date().before(coupon.getValidity())) {	//兑换券未过期
				if(coupon.getState() == 0) {	//兑换券未使用
					if(coupon.getReusable() == true) {	//批次可以重复使用
						doExchangeCoupon(coupon, mobile, userno);
						return coupon;
					} else {	//批次不可以重复使用
						String couponBatchId = coupon.getCouponbatchid();
						CouponBatchUsageDetail couponBatchUsageDetail = CouponBatchUsageDetail.findCouponBatchUsageDetail(new CouponBatchUsageDetailPK(mobile, couponBatchId));
						if(couponBatchUsageDetail == null) {	//未兑换过该批次
							doExchangeCoupon(coupon, mobile, userno);
							couponBatchUsageDetail =CouponBatchUsageDetail.create(mobile, couponBatchId);
							return coupon;
						} else {	//已经兑换过该批次，不可重复兑换
							throw new RuyicaiException(ErrorCode.CouponBatchAlreadyUsed);
						}
					}
				} else {	//兑换券已使用
					throw new RuyicaiException(ErrorCode.CouponAlreadyUsed);
				}		
			} else {
				throw new RuyicaiException(ErrorCode.CouponOutOfDate);
			}					
		}		
	}
	
	/**
	 * 兑换操作
	 * @param coupon
	 * @param mobile
	 * @param userno
	 */
	private void doExchangeCoupon(Coupon coupon, String mobile, String userno) {
		CouponBatchChannel batchChannel = CouponBatchChannel.find(coupon.getCouponbatchchannelid(), false);
		if(batchChannel == null) {
			throw new RuyicaiException(ErrorCode.ERROR);
		}
		coupon.setMobile(mobile);
		coupon.setUserno(userno);
		coupon.setState(1);
		coupon.setUsetime(new Date());
		coupon.merge();
		//更新CouponBatch和CouponBatchChannel
		updateCouponBatchAndChannelJMS(coupon.getCouponbatchid(),coupon.getCouponbatchchannelid());
		//异步尝试更新用户渠道号
		asyncService.asyncUpdateUserChannel(batchChannel.getChannel(), userno);
		//发送彩金
		tactionService.sendPrize2UserJMS(userno, coupon.getAmount(), ActionJmsType.Coupon, coupon.getCouponcode(), coupon.getCouponcode(), "");
	}
	
	/**
	 * 发送使用兑换券后，更新兑换券批次和兑换券批次渠道的JMS
	 * @param couponBatchId					批次Id
	 * @param couponBatchChannelId		批次渠道Id
	 */
	@Transactional
	public void updateCouponBatchAndChannelJMS(String couponBatchId, Long couponBatchChannelId) {
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("couponBatchId", couponBatchId);
		headers.put("couponBatchChannelId", couponBatchChannelId);
		updateCouponBatchAndChannelProducer.sendBodyAndHeaders(null, headers);
	}
}
