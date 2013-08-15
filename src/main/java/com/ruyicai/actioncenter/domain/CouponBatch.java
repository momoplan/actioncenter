package com.ruyicai.actioncenter.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import com.ruyicai.actioncenter.util.Page;
import com.ruyicai.actioncenter.util.PropertyFilter;

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
	@Column(name = "TOTALAMOUNT", precision = 10, scale = 0, columnDefinition = "decimal")
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
	 * @param reusable						是否可以重复使用
	 * @return
	 */
	public static CouponBatch createCouponBatch(String couponBatchName,  Boolean reusable) {
		CouponBatch couponBatch = new CouponBatch();
		couponBatch.setCouponbatchname(couponBatchName);
		couponBatch.setCouponbatchquantity(0);
		couponBatch.setCouponbatchusage(0);
		couponBatch.setReusable(reusable);
		couponBatch.setTotalamount(new BigDecimal(0));
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
	
	/**
	 * 根据条件查询到page中
	 * @param conditionMap
	 * @param page
	 */
	public static void findCouponBatchesByPage(Map<String, Object> conditionMap,
			Page<CouponBatch> page) {
		EntityManager em = Coupon.entityManager();
		String sql = "SELECT o FROM CouponBatch o ";
		String countSql = "SELECT count(*) FROM CouponBatch o ";
		StringBuilder whereSql = new StringBuilder(" WHERE 1=1 ");
		List<PropertyFilter> pfList = null;
		if (conditionMap != null && conditionMap.size() > 0) {
			pfList = PropertyFilter.buildFromMap(conditionMap);
			String buildSql = PropertyFilter.transfer2Sql(pfList, "o");
			whereSql.append(buildSql);
		}
		StringBuilder orderSql = new StringBuilder(" ORDER BY o.createTime DESC");
		String tsql = sql + whereSql.toString() + orderSql.toString();
		String tCountSql = countSql + whereSql.toString();
		TypedQuery<CouponBatch> q = em.createQuery(tsql, CouponBatch.class);
		TypedQuery<Long> total = em.createQuery(tCountSql, Long.class);
		if (conditionMap != null && conditionMap.size() > 0) {
			PropertyFilter.setMatchValue2Query(q, pfList);
			PropertyFilter.setMatchValue2Query(total, pfList);
		}
		q.setFirstResult(page.getPageIndex() * page.getMaxResult()).setMaxResults(page.getMaxResult());
		List<CouponBatch> resultList = q.getResultList();
		int count = total.getSingleResult().intValue();
		page.setList(resultList);
		page.setTotalResult(count);
	}
	
	
	
}
