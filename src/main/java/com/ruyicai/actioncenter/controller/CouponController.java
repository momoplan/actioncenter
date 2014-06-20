package com.ruyicai.actioncenter.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.actioncenter.domain.Coupon;
import com.ruyicai.actioncenter.domain.CouponBatch;
import com.ruyicai.actioncenter.domain.CouponBatchChannel;
import com.ruyicai.actioncenter.exception.RuyicaiException;
import com.ruyicai.actioncenter.service.CouponService;
import com.ruyicai.actioncenter.util.ErrorCode;
import com.ruyicai.actioncenter.util.JsonUtil;
import com.ruyicai.actioncenter.util.Page;

@RequestMapping("/coupon")
@Controller
public class CouponController {
	private Logger logger = LoggerFactory.getLogger(CouponController.class);
	
	@Autowired
	private CouponService couponService;
	
	/**
	 * 生成兑换券批次
	 * @param couponBatchName	批次名称
	 * @param reusable						是否可以重复使用
	 * @return
	 */
	@RequestMapping(value = "/createCouponBatch")
	public @ResponseBody ResponseData createCouponBatch(@RequestParam(value = "couponBatchName", required = true) String couponBatchName,
			@RequestParam(value = "reusable", required = true) Boolean reusable) {
		logger.info("/coupon/createCouponBatch");
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			CouponBatch couponBatch = couponService.createCouponBatch(couponBatchName, reusable);
			rd.setValue(couponBatch);
		} catch (Exception e) {
			logger.error("createCouponBatch error", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		logger.info("/coupon/createCouponBatch end");
		return rd;
	}
	
	/**
	 * 创建兑换券批次渠道
	 * @param couponBatchId	批次id
	 * @param channelName	渠道名称
	 * @param memo				渠道说明
	 * @param channelNo			渠道号
	 * @return
	 */
	@RequestMapping(value = "/createCouponBatchChannel")
	public @ResponseBody ResponseData createCouponBatchChannel(@RequestParam(value = "couponBatchId", required = false) String couponBatchId,
			@RequestParam(value = "channelName", required = false) String channelName,
			@RequestParam(value = "memo", required = false) String memo,
			@RequestParam(value = "channelNo", required = false) String channelNo) {
		logger.info("/coupon/createCouponBatchChannel");
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			CouponBatchChannel channel = couponService.createCouponBatchChannel(couponBatchId, channelName, memo, channelNo);
			rd.setValue(channel);
		} catch (Exception e) {
			logger.error("createCouponBatchChannel error", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		logger.info("/coupon/createCouponBatchChannel end");
		return rd;
	}
	
	/**
	 * 创建兑换券
	 * @param couponBatchId					兑换券批次Id
	 * @param couponBatchChannelId		兑换券批次渠道Id
	 * @param couponQuantity				创建的兑换券数量
	 * @param validity								兑换券有效期
	 * @param couponAmount					兑换券金额（分）
	 * @return
	 */
	@RequestMapping(value = "/createCoupon")
	public @ResponseBody ResponseData createCoupon(@RequestParam(value = "couponBatchId", required = true) String couponBatchId,
			@RequestParam(value = "couponBatchChannelId", required = true) Long couponBatchChannelId,
			@RequestParam(value = "couponQuantity", required = true) Integer couponQuantity,
			@RequestParam(value = "validity", required = true) String validity,
			@RequestParam(value = "couponAmount", required = true) BigDecimal couponAmount,
			@RequestParam(value = "belonguserno", required = false) String belonguserno) {
		logger.info("/coupon/createCoupon");
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			couponService.createCoupons(couponBatchId, couponBatchChannelId, couponQuantity, validity, couponAmount,belonguserno);
		} catch(IllegalArgumentException e) {
			logger.error("createCoupon error", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		} catch (Exception e) {
			logger.error("createCoupon error", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		logger.info("/coupon/createCoupon end");
		return rd;
	}
	
	/**
	 * 使用兑换券
	 * @param couponCode	兑换券号
	 * @param userno			用户ID
	 * @return Coupon
	 */
	@RequestMapping(value = "/useCoupon")
	public @ResponseBody ResponseData useCoupon(@RequestParam("couponCode") String couponCode,
			@RequestParam("userno") String userno) {
		logger.info("/coupon/useCoupon");
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			Coupon coupon = couponService.exchangeCoupon(userno, couponCode);
			rd.setValue(coupon);
		} catch(RuyicaiException e) {
			logger.error("使用兑换券出错,{}", new String[] { e.getMessage() }, e);
			result = e.getErrorCode();
			rd.setValue(e.getMessage());
		} catch (Exception e) {
			logger.error("使用兑换券出错,{}", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}
	
	/**
	 * 根据condition搜索兑换券批次
	 * @param condition
	 * @param pageIndex
	 * @param maxResult
	 * @return List<CouponBatch>
	 */
	@RequestMapping(value = "/listCouponBatchesByPage")
	public @ResponseBody ResponseData listCouponBatchesByPage(@RequestParam(value = "condition", required = false) String condition,
			@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "maxResult", required = false, defaultValue = "30") int maxResult) {
		logger.info("/listCouponsByPage" + condition);
		ResponseData rd = new ResponseData();
		Page<CouponBatch> page = new Page<CouponBatch>(pageIndex, maxResult);
		try {
			Map<String, Object> conditionMap = JsonUtil.transferJson2Map(condition);
			CouponBatch.findCouponBatchesByPage(conditionMap, page);
			rd.setValue(page);
			rd.setErrorCode(ErrorCode.OK.value);
		} catch (Exception e) {
			logger.error("兑换券批次分页查询出错", new String[] { e.getMessage() }, e);
			rd.setErrorCode(ErrorCode.ERROR.value);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	/**
	 * 根据condition搜索兑换券批次渠道
	 * @param condition
	 * @param pageIndex
	 * @param maxResult
	 * @return	List<CouponBatchChannel>
	 */
	@RequestMapping(value = "/listCouponBatchChannelsByPage")
	public @ResponseBody ResponseData listCouponBatchChannelsByPage(@RequestParam(value = "condition", required = false) String condition,
			@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "maxResult", required = false, defaultValue = "30") int maxResult) {
		logger.info("/listCouponBatchChannelsByPage" + condition);
		ResponseData rd = new ResponseData();
		Page<CouponBatchChannel> page = new Page<CouponBatchChannel>(pageIndex, maxResult);
		try {
			Map<String, Object> conditionMap = JsonUtil.transferJson2Map(condition);
			CouponBatchChannel.findCouponBatchChannelsByPage(conditionMap, page);
			rd.setValue(page);
			rd.setErrorCode(ErrorCode.OK.value);
		} catch (Exception e) {
			logger.error("兑换券批次渠道分页查询出错", new String[] { e.getMessage() }, e);
			rd.setErrorCode(ErrorCode.ERROR.value);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	/**
	 * 根据条件查询兑换券
	 * @param condition	条件
	 * @param pageIndex	当前页号
	 * @param maxResult	返回结果数
	 * @return List<Coupon>
	 */
	@RequestMapping(value = "/listCouponsByPage")
	public @ResponseBody
	ResponseData listCouponsByPage(@RequestParam(value = "condition", required = false) String condition,
			@RequestParam(value = "pageIndex", required = false, defaultValue = "0") int pageIndex,
			@RequestParam(value = "maxResult", required = false, defaultValue = "30") int maxResult) {
		logger.info("/listCouponsByPage" + condition);
		ResponseData rd = new ResponseData();
		Page<Coupon> page = new Page<Coupon>(pageIndex, maxResult);
		try {
			Map<String, Object> conditionMap = JsonUtil.transferJson2Map(condition);
			Coupon.findCouponsByPage(conditionMap, page);
			rd.setValue(page);
			rd.setErrorCode(ErrorCode.OK.value);
		} catch (Exception e) {
			logger.error("兑换券分页查询出错", new String[] { e.getMessage() }, e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
	/**
	 * 根据批次渠道ID查询用户集
	 * @param channelId
	 * @return
	 */
	@RequestMapping(value = "/queryUsersByChannelId")
	public @ResponseBody
	ResponseData listCouponsByPage(@RequestParam(value = "channelId", required = true) long channelId) {
		logger.info("/queryUsersByChannelId:" + channelId);
		ResponseData rd = new ResponseData();
		try{
			rd.setValue(Coupon.queryUsersBychannelId(channelId));
			rd.setErrorCode(ErrorCode.OK.value);
		} catch (Exception e) {
			logger.error("根据批次渠道ID查询用户集", new String[] { e.getMessage() }, e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
}
