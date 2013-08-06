package com.ruyicai.actioncenter.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.LockModeType;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * 兑换券批号
 * @author 李晨星
 * @date 2013-7-25 上午11:02:10
 */
@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField = "", table = "COUPONBATCH", identifierField = "couponbatchid")
public class CouponBatch {
	
	@Id
	@GenericGenerator(name = "generator", strategy = "org.hibernate.id.UUIDHexGenerator")
	@GeneratedValue(generator = "generator")
	@Column(name = "COUPONBATCHID", length = 50)
	private String couponbatchid;
	
	/**
	 * 兑换券批次名称
	 */
	@Column(name = "COUPONBATCHNAME", length = 50)
	private String couponbatchname;
	
	/**
	 * 兑换券总数
	 */
	@Column(name = "COUPONBATCHQUANTITY")
	private int couponbatchquantity;
	
	/**
	 * 兑换券已兑数量
	 */
	@Column(name = "COUPONBATCHUSAGE")
	private int couponbatchusage;
	
	/**
	 * 总金额
	 */
	@Column(name = "TOTALAMOUNT")
	private BigDecimal totalamount;
	
	/**
	 * 是否何以重复使用
	 */
	@Column(name = "REUABLE")
	private Boolean reusable;
	
	/**
	 * 创建时间
	 */
	@Column(name = "CREATETIME")
	private Date createTime;
	
	public static CouponBatch find(String couponbatchid, boolean lock) {
		CouponBatch couponBatch = entityManager().find(CouponBatch.class, couponbatchid, lock ? LockModeType.PESSIMISTIC_WRITE : LockModeType.NONE);
		return couponBatch;
	}
	
	/**
	 * 创建兑换券批次
	 * @param couponBatchName	批次名称
	 * @param couponQuantity		批次数量
	 * @param reusable						是否可以重复使用
	 * @param totalAmount				总金额
	 * @return
	 */
	public static CouponBatch createCouponBatch(String couponBatchName, int couponQuantity, Boolean reusable, BigDecimal totalAmount) {
		CouponBatch couponBatch = new CouponBatch();
		couponBatch.setCouponbatchname(couponBatchName);
		couponBatch.setCouponbatchquantity(couponQuantity);
		couponBatch.setCouponbatchusage(0);
		couponBatch.setReusable(reusable);
		couponBatch.setTotalamount(totalAmount);
		couponBatch.setCreateTime(new Date());
		couponBatch.persist();
		return couponBatch;
	}
	
	/**
	 * 根据批次名称查找兑换券批次
	 * @param couponbatchname
	 * @return
	 */
	public static List<CouponBatch> findByName(String couponbatchname) {
		List<CouponBatch> result = entityManager().createQuery("SELECT o FROM CouponBatch o WHERE o.couponbatchname = ? ", CouponBatch.class).setParameter(1, couponbatchname).getResultList();
		return result;
	}
	
	public static CouponBatch useACoupon(String couponbatchid) {
		CouponBatch couponBatch = CouponBatch.find(couponbatchid, true);
		couponBatch.setCouponbatchusage(couponBatch.getCouponbatchusage() + 1);
		couponBatch.merge();
		return couponBatch;
	}
	
	
	
}
