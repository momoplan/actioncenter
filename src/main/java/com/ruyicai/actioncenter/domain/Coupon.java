package com.ruyicai.actioncenter.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.TypedQuery;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import com.ruyicai.actioncenter.util.Page;
import com.ruyicai.actioncenter.util.PropertyFilter;

/**
 * 兑换券
 * @author 李晨星
 * @date 2013-7-25 下午12:02:56
 */
@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField = "", table = "COUPON", identifierField = "couponcode")
public class Coupon {
	
	/**
	 * 兑换券号
	 */
	@Id
	@Column(name = "COUPONCODE", length = 50)
	private String couponcode;
	
	/**
	 * 兑换券批次
	 */
    @Column(name = "COUPONBATCHID", length = 50)
	private String couponbatchid;
	
	/**
	 * 是否可以重复使用
	 */
	@Column(name = "REUABLE")
	private Boolean reusable;
	
	/**
	 * 金额
	 */
	@Column(name = "AMOUNT")
	private BigDecimal amount;
	
	/**
	 * 有效期
	 */
	@Column(name = "VALIDITY")
	private Date validity;
	
	/**
	 * 使用状态0使用，1未使用
	 */
	@Column(name = "STATE")
	private int state;
	
	/**
	 * 使用者用户ID
	 */
	@Column(name = "USERNO", length = 20)
	private String userno;
	
	/**
	 * 使用时间
	 */
	@Column(name = "USETIME")
	private Date usetime;
	
	public static Coupon create(String couponCode, Boolean reusable, String couponBatchId, BigDecimal couponAmount, Date validity) {
		Coupon coupon = new Coupon();
		coupon.setCouponcode(couponCode);
		coupon.setCouponbatchid(couponBatchId);
		coupon.setAmount(couponAmount);
		coupon.setReusable(reusable);
		coupon.setState(1);
		coupon.setValidity(validity);
		coupon.persist();
		return coupon;
	}
	
	/**
	 * 使用兑换券
	 * @param couponCode	兑换券号码
	 * @param userno			使用者
	 * @return
	 */
	public static Coupon useCoupon(String couponCode, String userno) {
		Coupon coupon = Coupon.findCoupon(couponCode);
		coupon.setUserno(userno);
		coupon.setState(0);
		coupon.setUsetime(new Date());
		coupon.merge();
		return coupon;
	}
	
	public static void findCouponsByPage(Map<String, Object> conditionMap,
			Page<Coupon> page) {
		EntityManager em = SendMoneyDetails.entityManager();
		String sql = "SELECT o FROM Coupon o ";
		String countSql = "SELECT count(*) FROM Coupon o ";
		StringBuilder whereSql = new StringBuilder(" WHERE 1=1 ");
		List<PropertyFilter> pfList = null;
		if (conditionMap != null && conditionMap.size() > 0) {
			pfList = PropertyFilter.buildFromMap(conditionMap);
			String buildSql = PropertyFilter.transfer2Sql(pfList, "o");
			whereSql.append(buildSql);
		}
		StringBuilder orderSql = new StringBuilder(" ORDER BY o.validity DESC");
		String tsql = sql + whereSql.toString() + orderSql.toString();
		String tCountSql = countSql + whereSql.toString();
		TypedQuery<Coupon> q = em.createQuery(tsql, Coupon.class);
		TypedQuery<Long> total = em.createQuery(tCountSql, Long.class);
		if (conditionMap != null && conditionMap.size() > 0) {
			PropertyFilter.setMatchValue2Query(q, pfList);
			PropertyFilter.setMatchValue2Query(total, pfList);
		}
		q.setFirstResult(page.getPageIndex()).setMaxResults(page.getMaxResult());
		List<Coupon> resultList = q.getResultList();
		int count = total.getSingleResult().intValue();
		page.setList(resultList);
		page.setTotalResult(count);
	}
}
