package com.ruyicai.actioncenter.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * 用户-兑换券批次使用情况
 * @author 李晨星
 * @date 2013-7-25 下午12:03:06
 */
@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField = "", table = "COUPONBATCHUSAGEDETAIL", identifierField = "id")
public class CouponBatchUsageDetail {
	
	@EmbeddedId
	private CouponBatchUsageDetailPK id;
	
	@Column(name = "USETIME")
	private Date usetime;
	
	public static CouponBatchUsageDetail create(String userno, String couponBatchId) {
		CouponBatchUsageDetail couponBatchUsageDetail = new CouponBatchUsageDetail();
		couponBatchUsageDetail.setId(new CouponBatchUsageDetailPK(userno, couponBatchId));
		couponBatchUsageDetail.setUsetime(new Date());
		couponBatchUsageDetail.persist();
		return couponBatchUsageDetail;
	}
}
