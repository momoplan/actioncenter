package com.ruyicai.actioncenter.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.service.MemcachedService;
import com.ruyicai.actioncenter.util.Page;
import com.ruyicai.actioncenter.util.Page.Sort;

@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField = "", table = "TACTIVITY", identifierField = "id")
public class Tactivity implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 彩种 */
	@Column(name = "lotno")
	private String lotno;

	/** 玩法 */
	@Column(name = "playtype")
	private String playtype;

	/** 大渠道 */
	@Column(name = "subChannel")
	private String subChannel;

	/** 渠道 */
	@Column(name = "channel")
	private String channel;

	/** 活动类型 */
	@Column(name = "activityType")
	private Integer activityType;

	/** 活动描述 */
	@Column(name = "memo", length = 100)
	private String memo;

	/** 活动金额表达式 */
	@Column(name = "express", length = 500)
	private String express;

	/** 状态 0:失效,1:有效 */
	@Column(name = "state")
	private Integer state;

	/** 创建时间 */
	@Column(name = "createTime")
	private Date createTime;

	/** 最近修改时间 */
	@Column(name = "lastmodifyTime")
	private Date lastmodifyTime;

	@Autowired
	transient MemcachedService<Tactivity> memcachedService;

	public static Tactivity saveOrUpdate(String lotno, String playtype, String subChannel, String channel,
			Integer actionJmsType, String express, Integer state, String memo) {
		Tactivity tactivity = null;
		tactivity = findTactivityFromDB(lotno, playtype, subChannel, channel, actionJmsType);
		if (tactivity == null) {
			tactivity = new Tactivity();
			tactivity.setLotno(lotno);
			tactivity.setPlaytype(playtype);
			tactivity.setSubChannel(subChannel);
			tactivity.setChannel(channel);
			tactivity.setActivityType(actionJmsType);
			tactivity.setMemo(memo);
			tactivity.setExpress(express);
			tactivity.setState(state);
			tactivity.setCreateTime(new Date());
			tactivity.persist();
		} else {
			tactivity.setLotno(lotno);
			tactivity.setPlaytype(playtype);
			tactivity.setSubChannel(subChannel);
			tactivity.setChannel(channel);
			tactivity.setActivityType(actionJmsType);
			tactivity.setMemo(memo);
			tactivity.setExpress(express);
			tactivity.setState(state);
			tactivity.setLastmodifyTime(new Date());
			tactivity.merge();
		}
		String uniqueKey = uniqueKey(tactivity);
		new Tactivity().memcachedService.set(uniqueKey, tactivity);
		return tactivity;
	}

	@Transactional
	public static Tactivity updateState(Long id, Integer state) {
		if (id == null || state == null) {
			return null;
		}
		Tactivity tactivity = Tactivity.findTactivity(id);
		tactivity.setState(state);
		tactivity.setLastmodifyTime(new Date());
		tactivity.merge();
		String uniqueKey = uniqueKey(tactivity);
		new Tactivity().memcachedService.set(uniqueKey, tactivity);
		return tactivity;
	}

	/**
	 * 查询有效的活动
	 * 
	 * @param lotno
	 *            彩种,不需要的传Null
	 * @param playtype
	 *            玩法,不需要的传Null
	 * @param subChannel
	 *            用户大渠道subChannel(subChannel和channel必须有一个不为空)
	 * @param channel
	 *            用户渠道channel(subChannel和channel必须有一个不为空)
	 * @param activityType
	 *            活动类型
	 * @return
	 */
	public static Tactivity findTactivity(String lotno, String playtype, String subChannel, String channel,
			Integer actionJmsType) {
		if (actionJmsType == null)
			throw new IllegalArgumentException("The activityType argument is required");
		if (StringUtils.isBlank(subChannel) && StringUtils.isBlank(channel))
			throw new IllegalArgumentException("subChannel或channel至少有一个不为空");
		Tactivity tactivity = null;
		String uniqueKey = uniqueKey(lotno, playtype, subChannel, channel, actionJmsType);
		tactivity = new Tactivity().memcachedService.get(uniqueKey);
		if (tactivity == null) {
			tactivity = findTactivityFromDB(lotno, playtype, subChannel, channel, actionJmsType);
		}
		if (tactivity != null) {
			if (tactivity.getState() == 1) {
				return tactivity;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 查询有效的活动
	 * 
	 * @param lotno
	 *            彩种,不需要的传Null
	 * @param playtype
	 *            玩法
	 * @param subChannel
	 *            用户大渠道subChannel(subChannel和channel必须有一个不为空)
	 * @param channel
	 *            用户渠道channel(subChannel和channel必须有一个不为空)
	 * @param activityType
	 *            活动类型
	 * @return
	 */
	private static Tactivity findTactivityFromDB(String lotno, String playtype, String subChannel, String channel,
			Integer actionJmsType) {
		if (actionJmsType == null)
			throw new IllegalArgumentException("The argument actionDetailType is required");
		if (StringUtils.isBlank(subChannel) && StringUtils.isBlank(channel))
			throw new IllegalArgumentException("subChannel或channel至少有一个不为空");
		Tactivity tactivity = null;
		EntityManager em = Tactivity.entityManager();
		StringBuffer sql = new StringBuffer(
				"SELECT o FROM Tactivity AS o WHERE o.activityType = :activityType");
		if (StringUtils.isNotBlank(lotno)) {
			sql.append(" AND o.lotno = :lotno");
		}
		if (StringUtils.isNotBlank(playtype)) {
			sql.append(" AND o.playtype = :playtype");
		}
		if (StringUtils.isNotBlank(subChannel)) {
			sql.append(" AND o.subChannel = :subChannel");
		}
		if (StringUtils.isNotBlank(channel)) {
			sql.append(" AND o.channel = :channel");
		}
		TypedQuery<Tactivity> q = em.createQuery(sql.toString(), Tactivity.class);
		q.setParameter("activityType", actionJmsType);
		if (StringUtils.isNotBlank(lotno)) {
			q.setParameter("lotno", lotno);
		}
		if (StringUtils.isNotBlank(playtype)) {
			q.setParameter("playtype", playtype);
		}
		if (StringUtils.isNotBlank(subChannel)) {
			q.setParameter("subChannel", subChannel);
		}
		if (StringUtils.isNotBlank(channel)) {
			q.setParameter("channel", channel);
		}
		List<Tactivity> resultList = q.getResultList();
		if (resultList != null && resultList.size() > 0) {
			tactivity = resultList.get(0);
		}
		if (tactivity != null) {
			String uniqueKey = uniqueKey(tactivity);
			new Tactivity().memcachedService.set(uniqueKey, tactivity);
		}
		return tactivity;
	}

	public static String uniqueKey(Tactivity tactivity) {
		return uniqueKey(tactivity.getLotno(), tactivity.getPlaytype(), tactivity.getSubChannel(),
				tactivity.getChannel(), tactivity.getActivityType());
	}

	public static String uniqueKey(String lotno, String playtype, String subChannel, String channel,
			Integer actionJmsType) {
		StringBuffer sb = new StringBuffer("Tactivity");
		if (StringUtils.isNotBlank(lotno)) {
			sb.append(lotno);
		}
		if (StringUtils.isNotBlank(playtype)) {
			sb.append(playtype);
		}
		if (StringUtils.isNotBlank(subChannel)) {
			sb.append(subChannel);
		}
		if (StringUtils.isNotBlank(channel)) {
			sb.append(channel);
		}
		sb.append(actionJmsType);
		return sb.toString();
	}

	public static void findTactivityByPage(Page<Tactivity> page) {
		EntityManager em = Tactivity.entityManager();
		String sql = "SELECT o FROM Tactivity o ";
		String countSql = "SELECT count(*) FROM Tactivity o ";
		StringBuilder whereSql = new StringBuilder(" WHERE 1=1 ");
		List<Sort> sortList = page.fetchSort();
		StringBuilder orderSql = new StringBuilder(" ORDER BY ");
		if (page.isOrderBySetted()) {
			for (Sort sort : sortList) {
				orderSql.append(" " + sort.getProperty() + " " + sort.getDir() + ",");
			}
			orderSql.delete(orderSql.length() - 1, orderSql.length());
		} else {
			orderSql.append(" o.activityType asc ");
		}
		String tsql = sql + whereSql.toString() + orderSql.toString();
		String tCountSql = countSql + whereSql.toString();
		TypedQuery<Tactivity> q = em.createQuery(tsql, Tactivity.class);
		TypedQuery<Long> total = em.createQuery(tCountSql, Long.class);
		q.setFirstResult(page.getPageIndex()).setMaxResults(page.getMaxResult());
		List<Tactivity> resultList = q.getResultList();
		int count = total.getSingleResult().intValue();
		page.setList(resultList);
		page.setTotalResult(count);
	}
}
