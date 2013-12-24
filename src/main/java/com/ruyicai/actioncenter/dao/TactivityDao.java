package com.ruyicai.actioncenter.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.domain.Tactivity;
import com.ruyicai.actioncenter.service.MemcachedService;
import com.ruyicai.actioncenter.util.Page;
import com.ruyicai.actioncenter.util.Page.Sort;

@Component
public class TactivityDao {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private MemcachedService<Tactivity> memcachedService;

	public Tactivity findTactivity(Long id) {
		return this.entityManager.find(Tactivity.class, id);
	}

	@Transactional
	public void persist(Tactivity tactivity) {
		this.entityManager.persist(tactivity);
	}

	@Transactional
	public Tactivity merge(Tactivity tactivity) {
		Tactivity merge = this.entityManager.merge(tactivity);
		this.entityManager.flush();
		return merge;
	}

	@Transactional
	public Tactivity saveOrUpdate(String lotno, String playtype, String subChannel, String channel,
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
			this.persist(tactivity);
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
			this.merge(tactivity);
		}
		String uniqueKey = uniqueKey(tactivity);
		this.memcachedService.set(uniqueKey, tactivity);
		return tactivity;
	}

	@Transactional
	public Tactivity updateState(Long id, Integer state) {
		if (id == null || state == null) {
			return null;
		}
		Tactivity tactivity = this.findTactivity(id);
		tactivity.setState(state);
		tactivity.setLastmodifyTime(new Date());
		this.merge(tactivity);
		String uniqueKey = uniqueKey(tactivity);
		this.memcachedService.set(uniqueKey, tactivity);
		return tactivity;
	}

	/**
	 * 查询有效的活动,不满足活动条件的返回NULL
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
	public Tactivity findTactivity(String lotno, String playtype, String subChannel, String channel,
			Integer actionJmsType) {
		if (actionJmsType == null)
			throw new IllegalArgumentException("the argument actionJmsType is required");
		if (StringUtils.isBlank(subChannel) && StringUtils.isBlank(channel))
			throw new IllegalArgumentException("subChannel或channel至少有一个不为空");
		Tactivity tactivity = null;
		String uniqueKey = uniqueKey(lotno, playtype, subChannel, channel, actionJmsType);
		tactivity = this.memcachedService.get(uniqueKey);
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
	private Tactivity findTactivityFromDB(String lotno, String playtype, String subChannel, String channel,
			Integer actionJmsType) {
		if (actionJmsType == null)
			throw new IllegalArgumentException("The argument actionDetailType is required");
		if (StringUtils.isBlank(subChannel) && StringUtils.isBlank(channel))
			throw new IllegalArgumentException("subChannel或channel至少有一个不为空");
		Tactivity tactivity = null;
		StringBuffer sql = new StringBuffer("SELECT o FROM Tactivity AS o WHERE o.activityType = :activityType");
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
		TypedQuery<Tactivity> q = this.entityManager.createQuery(sql.toString(), Tactivity.class);
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
			this.memcachedService.set(uniqueKey, tactivity);
		}
		return tactivity;
	}

	public String uniqueKey(Tactivity tactivity) {
		return uniqueKey(tactivity.getLotno(), tactivity.getPlaytype(), tactivity.getSubChannel(),
				tactivity.getChannel(), tactivity.getActivityType());
	}

	public String uniqueKey(String lotno, String playtype, String subChannel, String channel, Integer actionJmsType) {
		StringBuffer sb = new StringBuffer("Tactivity");
		sb.append(actionJmsType);
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
		return sb.toString();
	}

	public void findTactivityByPage(Page<Tactivity> page) {
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
		TypedQuery<Tactivity> q = this.entityManager.createQuery(tsql, Tactivity.class);
		TypedQuery<Long> total = this.entityManager.createQuery(tCountSql, Long.class);
		q.setFirstResult(page.getPageIndex()).setMaxResults(page.getMaxResult());
		List<Tactivity> resultList = q.getResultList();
		int count = total.getSingleResult().intValue();
		page.setList(resultList);
		page.setTotalResult(count);
	}

}
