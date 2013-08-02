package com.ruyicai.actioncenter.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.consts.ActionJmsType;
import com.ruyicai.actioncenter.domain.Coupon;
import com.ruyicai.actioncenter.domain.CouponBatch;
import com.ruyicai.actioncenter.domain.CouponBatchUsageDetail;
import com.ruyicai.actioncenter.domain.CouponBatchUsageDetailPK;
import com.ruyicai.actioncenter.util.DateUtil;

@Service
public class CouponService {
	private Logger logger = LoggerFactory.getLogger(CouponService.class);
	
	@Autowired
	private TactionService tactionService;
	
	/**
	 * 创建批次信息，并且创建兑换券
	 * @param couponBatchName	批次名称
	 * @param couponQuantity		批次数量
	 * @param reusable						是否可以重复使用
	 * @param validity						有效期
	 * @param couponAmount			兑换券金额
	 */
	@Transactional
	public void createCoupon(String couponBatchName, int couponQuantity, Boolean reusable, String validity, BigDecimal couponAmount) {
		logger.info("创建兑换券 couponBatchName:" + couponBatchName + " couponQuantity:" + couponQuantity + " reusable:" + reusable + " validity:" + validity + " couponAmount:" + couponAmount);
		try {
			CouponBatch couponBatch = CouponBatch.createCouponBatch(couponBatchName,
					couponQuantity, reusable,
					couponAmount.multiply(new BigDecimal(couponQuantity)));  
			doCreateCoupons(couponBatch.getCouponbatchid(), couponQuantity, reusable, validity, couponAmount);
		} catch (Exception e) {
			logger.error("创建批次兑换券错误", e);
		}		
	}
	
	/**
	 * 创建兑换券
	 * @param couponBatchId		兑换券批号
	 * @param couponQuantity	兑换券数量
	 * @param reusable					是否可以重复使用
	 * @param validity					有效期
	 * @param couponAmount		兑换券金额
	 */
	@Transactional
	private void doCreateCoupons(String couponBatchId, int couponQuantity, Boolean reusable, String validity, BigDecimal couponAmount) {
		for(int i = 0; i < couponQuantity; i++) {	
			Coupon.create(UUID.randomUUID().toString().replaceAll("-", ""), reusable, couponBatchId, couponAmount, DateUtil.parse(validity));
		}		
	}
	
	/**
	 * 兑换兑换券
	 * @param userno
	 * @param couponCode
	 * @return -1 错误 <br/> 0 兑换券不存在 <br/> 1 兑换成功 <br/>  2 兑换券已经兑换 <br/>  3 兑换券已经使用 <br/> 4 兑换券已过期
	 */
	@Transactional
	public int exchangeCoupon(String userno, String couponCode) {
		int result = -1;
		try {
			Coupon coupon = Coupon.findCoupon(couponCode);
			if(coupon == null) {	//兑换券不存在
				result = 0;
			} else {	//兑换券存在
				if(new Date().before(coupon.getValidity())) {	//兑换券未过期
					if(coupon.getState() == 1) {	//兑换券未使用
						if(coupon.getReusable() == true) {	//批次可以重复使用
							Coupon.useCoupon(couponCode, userno);
							CouponBatch.useACoupon(coupon.getCouponbatchid());
							//发送彩金
							tactionService.sendPrize2UserJMS(userno, coupon.getAmount(), ActionJmsType.Coupon, coupon.getCouponcode(), coupon.getCouponcode(), "");
							result = 1;
						} else {	//批次不可以重复使用
							String couponBatchId = coupon.getCouponbatchid();
							CouponBatchUsageDetail couponBatchUsageDetail = CouponBatchUsageDetail.findCouponBatchUsageDetail(new CouponBatchUsageDetailPK(userno, couponBatchId));
							if(couponBatchUsageDetail == null) {	//未兑换过该批次
								Coupon.useCoupon(couponCode, userno);
								couponBatchUsageDetail =CouponBatchUsageDetail.create(userno, couponBatchId);
								CouponBatch.useACoupon(coupon.getCouponbatchid());
								//发送彩金
								tactionService.sendPrize2UserJMS(userno, coupon.getAmount(), ActionJmsType.Coupon, coupon.getCouponcode(), coupon.getCouponcode(), "");
								result = 1;
							} else {	//已经兑换过该批次，不可重复兑换
								result = 2;
							}
						}
					} else {	//兑换券已使用
						result = 3;
					}		
				} else {
					result = 4;
				}					
			}		
		} catch (Exception e) {
			logger.error("兑换券兑换错误", e);
		}
		return result;
	}
}
