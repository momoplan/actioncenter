package com.ruyicai.actioncenter.domain;

import javax.persistence.Column;

import org.springframework.roo.addon.entity.RooIdentifier;
import org.springframework.roo.addon.tostring.RooToString;

@RooIdentifier
@RooToString
public class CouponBatchUsageDetailPK {
	
	private static final long serialVersionUID = 1L;
	
	/** 
	 * 用户手机号
	 **/
	@Column(name = "MOBILE", length = 50)
	private String mobile;
	
	/**
	 * 批次ID
	 */
	@Column(name = "COUPONBATCHID", length = 50)
	private String couponbatchid;
}
