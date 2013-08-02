package com.ruyicai.actioncenter.domain;

import javax.persistence.Column;

import org.springframework.roo.addon.entity.RooIdentifier;
import org.springframework.roo.addon.tostring.RooToString;

@RooIdentifier
@RooToString
public class CouponBatchUsageDetailPK {
	
	private static final long serialVersionUID = 1L;
	
	/** 
	 * 用户编号 
	 **/
	@Column(name = "USERNO", length = 50)
	private String userno;
	
	/**
	 * 批次ID
	 */
	@Column(name = "COUPONBATCHID", length = 50)
	private String couponbatchid;
}
