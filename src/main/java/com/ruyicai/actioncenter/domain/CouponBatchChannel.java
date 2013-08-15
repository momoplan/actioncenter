package com.ruyicai.actioncenter.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import com.ruyicai.actioncenter.util.Page;
import com.ruyicai.actioncenter.util.PropertyFilter;

/**
 * 兑换券批次渠道
 * @author 李晨星
 * @date 2013-8-7 下午5:16:22
 */
@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField = "", table = "COUPONBATCHCHANNEL", identifierField = "couponbatchchannelid")
public class CouponBatchChannel {
	
	/**
	 * id自增
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "COUPONBATCHCHANNELID")
	private long couponbatchchannelid;
	
	/**
	 * 兑换券批次
	 */
    @Column(name = "COUPONBATCHID", length = 50)
	private String couponbatchid;
    
    /**
     * 渠道名称
     */
    @Column(name = "CHANNELNAME", length = 50)
    private String channelname;
    
    /**
	 * 兑换券总数
	 */
	@Column(name = "COUPONQUANTITY")
	private int couponquantity;
	
	/**
	 * 兑换券已兑数量
	 */
	@Column(name = "COUPONUSAGE")
	private int couponusage;
	
	/**
	 * 总金额
	 */
	@Column(name = "TOTALAMOUNT", precision = 10, scale = 0, columnDefinition = "decimal")
	private BigDecimal totalamount;
	
	/**
	 * 渠道详细说明
	 */
	@Column(name = "MEMO", length = 255)
	private String memo;
	
	/**
	 * 创建时间
	 */
	@Column(name = "CREATETIME")
	private Date createTime;
	
	/**
	 * 根据渠道名称获取批次渠道
	 * @param channelName
	 * @return
	 */
	public static List<CouponBatchChannel> findByChannelName(String channelName) {
		List<CouponBatchChannel> result = entityManager().createQuery("SELECT o FROM CouponBatchChannel o WHERE o.channelname = ? ", CouponBatchChannel.class).setParameter(1, channelName).getResultList();
		return result;
	}
	
	/**
	 * 根据渠道id搜索
	 * @param couponbatchchannelid	渠道id
	 * @param lock								是否锁
	 * @return
	 */
	public static CouponBatchChannel find(long couponbatchchannelid, boolean lock) {
		CouponBatchChannel channel = entityManager().find(CouponBatchChannel.class, couponbatchchannelid, lock ? LockModeType.PESSIMISTIC_WRITE : LockModeType.NONE);
		return channel;
	}
	
	/**
	 * 创建批次渠道
	 * @param couponBatchId	批次id
	 * @param channelName	渠道名称
	 * @param memo				渠道备注
	 * @return
	 */
	public static CouponBatchChannel create(String couponBatchId, String channelName, String memo) {
		CouponBatchChannel channel = new CouponBatchChannel();
		channel.setChannelname(channelName);
		channel.setCouponbatchid(couponBatchId);
		channel.setCouponquantity(0);
		channel.setCouponusage(0);
		channel.setTotalamount(new BigDecimal(0));
		channel.setMemo(memo);
		channel.setCreateTime(new Date());
		channel.persist();
		return channel;
	}
	
	public static void findCouponBatchChannelsByPage(Map<String, Object> conditionMap,
			Page<CouponBatchChannel> page) {
		EntityManager em = Coupon.entityManager();
		String sql = "SELECT o FROM CouponBatchChannel o ";
		String countSql = "SELECT count(*) FROM CouponBatchChannel o ";
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
		TypedQuery<CouponBatchChannel> q = em.createQuery(tsql, CouponBatchChannel.class);
		TypedQuery<Long> total = em.createQuery(tCountSql, Long.class);
		if (conditionMap != null && conditionMap.size() > 0) {
			PropertyFilter.setMatchValue2Query(q, pfList);
			PropertyFilter.setMatchValue2Query(total, pfList);
		}
		q.setFirstResult(page.getPageIndex() * page.getMaxResult()).setMaxResults(page.getMaxResult());
		List<CouponBatchChannel> resultList = q.getResultList();
		int count = total.getSingleResult().intValue();
		page.setList(resultList);
		page.setTotalResult(count);
	}
}
