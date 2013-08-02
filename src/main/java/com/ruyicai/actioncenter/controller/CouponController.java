package com.ruyicai.actioncenter.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ruyicai.actioncenter.domain.Coupon;
import com.ruyicai.actioncenter.domain.CouponBatch;
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
	 * 生成兑换券
	 * @param couponBatchName	批次名称
	 * @param couponAmount			每个兑换券的金额（单位是分）
	 * @param couponQuantity		生产数量
	 * @param reusable						是否可以重复使用
	 * @param validity						有效期
	 * @return
	 */
	@RequestMapping(value = "/createCoupon")
	public @ResponseBody ResponseData createCoupon(@RequestParam(value = "couponBatchName", required = true) String couponBatchName,
			@RequestParam(value = "couponAmount", required = true) BigDecimal couponAmount, 
			@RequestParam(value = "couponQuantity", required = true) Integer couponQuantity,
			@RequestParam(value = "reusable", required = true) Boolean reusable,
			@RequestParam(value = "validity", required = true) String validity) {
		logger.info("/coupon/createCoupon");
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			couponService.createCoupon(couponBatchName, couponQuantity, reusable, validity, couponAmount);
		} catch (Exception e) {
			logger.error("创建兑换券出错,{}", new String[] { e.getMessage() }, e);
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
	 * @return
	 */
	@RequestMapping(value = "/useCoupon")
	public @ResponseBody ResponseData useCoupon(@RequestParam("couponCode") String couponCode,
			@RequestParam("userno") String userno) {
		logger.info("/coupon/useCoupon");
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		if(StringUtils.isBlank(couponCode)) {
			rd.setValue("couponCode不能为空");
			rd.setErrorCode(ErrorCode.PARAMTER_ERROR.value);
			return rd;
		}
		if(StringUtils.isBlank(userno)) {
			rd.setValue("userno不能为空");
			rd.setErrorCode(ErrorCode.PARAMTER_ERROR.value);
			return rd;
		}
		try {
			int intresult = couponService.exchangeCoupon(userno, couponCode);
			if(intresult != 1) {
				result = ErrorCode.PARAMTER_ERROR;
				rd.setValue(intresult);
			}
		} catch (Exception e) {
			logger.error("使用兑换券出错,{}", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}
	
	/**
	 * 根据兑换券号，返回兑换券信息
	 * @param couponCode	兑换券号
	 * @return
	 */
	@RequestMapping(value = "/listCoupon")
	public @ResponseBody ResponseData listCoupon(@RequestParam("couponCode") String couponCode) {
		logger.info("/coupon/listCoupon");		
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;		
		if(StringUtils.isBlank(couponCode)) {
			rd.setValue("couponCode不能为空");
			rd.setErrorCode(ErrorCode.PARAMTER_ERROR.value);
			return rd;
		}
		try {		
			Coupon coupon = Coupon.findCoupon(couponCode);
			rd.setValue(coupon);
			if(coupon == null) {
				result = ErrorCode.PARAMTER_ERROR;
			}
		} catch (Exception e) {
			logger.error("根据兑换券号查找兑换券信息出错,{}", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}
	
	/**
	 * 根据兑换券批次名称，返回兑换券批次信息
	 * @param couponBatchName	兑换券批次名称
	 * @return
	 */
	@RequestMapping(value = "/listCouponBatch")
	public @ResponseBody ResponseData listCouponBatch(@RequestParam("couponBatchName") String couponBatchName) {
		logger.info("/coupon/listCouponBatch");		
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;		
		if(StringUtils.isBlank(couponBatchName)) {
			rd.setValue("couponBatchName不能为空");
			rd.setErrorCode(ErrorCode.PARAMTER_ERROR.value);
			return rd;
		}
		try {		
			List<CouponBatch> couponBatchList = CouponBatch.findByName(couponBatchName);
			rd.setValue(couponBatchList);
			if(couponBatchList.size() == 0) {
				result = ErrorCode.PARAMTER_ERROR;
			}
		} catch (Exception e) {
			logger.error("根据批次名称查找兑换券批次失败,{}", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}
	
	@RequestMapping(value = "/listCouponBatchById")
	public @ResponseBody ResponseData listCouponBatchById(@RequestParam("couponBatchId") String couponBatchId) {
		logger.info("/coupon/listCouponBatchById");		
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;		
		if(StringUtils.isBlank(couponBatchId)) {
			rd.setValue("couponBatchId不能为空");
			rd.setErrorCode(ErrorCode.PARAMTER_ERROR.value);
			return rd;
		}
		try {		
			CouponBatch couponBatch = CouponBatch.findCouponBatch(couponBatchId);
			rd.setValue(couponBatch);
			if(couponBatch == null) {
				result = ErrorCode.PARAMTER_ERROR;
			}
		} catch (Exception e) {
			logger.error("根据批次查找兑换券批次信息失败,{}", new String[] { e.getMessage() }, e);
			result = ErrorCode.ERROR;
			rd.setValue(e.getMessage());
		}
		rd.setErrorCode(result.value);
		return rd;
	}
	
	/**
	 * 根据条件查询兑换券
	 * @param condition	条件
	 * @param startLine	开始行
	 * @param endLine		结束行
	 * @return
	 */
	@RequestMapping(value = "/listCouponsByPage")
	public @ResponseBody
	ResponseData selectReciverSendMoneyDetails(@RequestParam(value = "condition", required = false) String condition,
			@RequestParam(value = "startLine", required = false, defaultValue = "0") int startLine,
			@RequestParam(value = "endLine", required = false, defaultValue = "30") int endLine) {
		logger.info("/listCouponsByPage" + condition);
		ResponseData rd = new ResponseData();
		Page<Coupon> page = new Page<Coupon>(startLine, endLine);
		try {
			Map<String, Object> conditionMap = JsonUtil.transferJson2Map(condition);
			Coupon.findCouponsByPage(conditionMap, page);
			rd.setValue(page);
			rd.setErrorCode(ErrorCode.OK.value);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("兑换券分页查询出错", e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
}
