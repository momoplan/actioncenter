package com.ruyicai.actioncenter.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.consts.ActionJmsType;
import com.ruyicai.actioncenter.domain.TuserPrizeDetail;
import com.ruyicai.actioncenter.util.DateUtil;
import com.ruyicai.actioncenter.util.Page;
import com.ruyicai.actioncenter.util.PropertyFilter;
import com.ruyicai.actioncenter.util.Page.Sort;

@Component
public class TuserPrizeDetailDao {

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public void persist(TuserPrizeDetail tuserPrizeDetail) {
		this.entityManager.persist(tuserPrizeDetail);
	}

	@Transactional
	public void remove(TuserPrizeDetail tuserPrizeDetail) {
		if (this.entityManager.contains(tuserPrizeDetail)) {
			this.entityManager.remove(tuserPrizeDetail);
		} else {
			TuserPrizeDetail attached = this.findTuserPrizeDetail(tuserPrizeDetail.getId());
			this.entityManager.remove(attached);
		}
	}

	@Transactional
	public void flush() {
		this.entityManager.flush();
	}

	@Transactional
	public void clear() {
		this.entityManager.clear();
	}

	@Transactional
	public TuserPrizeDetail merge(TuserPrizeDetail tuserPrizeDetail) {
		TuserPrizeDetail merged = this.entityManager.merge(tuserPrizeDetail);
		this.entityManager.flush();
		return merged;
	}

	public TuserPrizeDetail findTuserPrizeDetail(Long id, boolean lock) {
		TuserPrizeDetail tuserPrizeDetail = this.entityManager.find(TuserPrizeDetail.class, id,
				lock ? LockModeType.PESSIMISTIC_WRITE : LockModeType.NONE);
		return tuserPrizeDetail;
	}

	public TuserPrizeDetail findTuserPrizeDetail(Long id) {
		return findTuserPrizeDetail(id, false);
	}

	public TuserPrizeDetail createTprizeUserBuyLog(String userno, BigDecimal amt, ActionJmsType actionJmsType) {
		return createTprizeUserBuyLog(userno, amt, actionJmsType, null);
	}

	@Transactional
	public TuserPrizeDetail createTprizeUserBuyLog(String userno, BigDecimal amt, ActionJmsType actionJmsType,
			String businessId) {
		if (StringUtils.isBlank(userno)) {
			throw new IllegalArgumentException("the argument userno is required");
		}
		if (amt == null) {
			throw new IllegalArgumentException("the argument amt is required");
		}
		if (actionJmsType == null) {
			throw new IllegalArgumentException("the argument actionDetailType is required");
		}
		TuserPrizeDetail log = new TuserPrizeDetail();
		log.setUserno(userno);
		log.setAmt(amt);
		log.setActivityType(actionJmsType.value);
		log.setState(0);
		log.setCreateTime(new Date());
		log.setBusinessId(businessId);
		this.persist(log);
		return log;
	}

	public TuserPrizeDetail findTuserPrizeDetailByUsernoAndActivityType(String userno, Integer activityType) {
		List<TuserPrizeDetail> resultList = this.entityManager
				.createQuery(
						"select o from TuserPrizeDetail o where o.userno = ? and o.activityType = ? order by o.createTime desc",
						TuserPrizeDetail.class).setParameter(1, userno).setParameter(2, activityType).getResultList();
		if (resultList != null && resultList.size() > 0) {
			return resultList.get(0);
		} else {
			return null;
		}
	}

	public BigDecimal statisticPrizeDetail(String userno, Integer activityType, Date date) {
		Date starttime = DateUtil.getDateTheZero(date);
		Date endtime = DateUtil.getDateTheZero(DateUtil.addDay(date, 1));
		TypedQuery<BigDecimal> query = this.entityManager
				.createQuery(
						"select sum(o.amt) from TuserPrizeDetail o where o.userno = ? and o.activityType = ? and o.createTime between ? and ? ",
						BigDecimal.class);
		query.setParameter(1, userno);
		query.setParameter(2, activityType);
		query.setParameter(3, starttime);
		query.setParameter(4, endtime);
		return query.getSingleResult()==null?new BigDecimal(0):query.getSingleResult();
	}

	public void findTuserPrizeDetailByPage(Map<String, Object> conditionMap, Page<TuserPrizeDetail> page) {
		String sql = "SELECT o FROM TuserPrizeDetail o ";
		String countSql = "SELECT count(*) FROM TuserPrizeDetail o ";
		StringBuilder whereSql = new StringBuilder(" WHERE 1=1 ");
		List<PropertyFilter> pfList = null;
		if (conditionMap != null && conditionMap.size() > 0) {
			pfList = PropertyFilter.buildFromMap(conditionMap);
			String buildSql = PropertyFilter.transfer2Sql(pfList, "o");
			whereSql.append(buildSql);
		}
		List<Sort> sortList = page.fetchSort();
		StringBuilder orderSql = new StringBuilder(" ORDER BY ");
		if (page.isOrderBySetted()) {
			for (Sort sort : sortList) {
				orderSql.append(" " + sort.getProperty() + " " + sort.getDir() + ",");
			}
			orderSql.delete(orderSql.length() - 1, orderSql.length());
		} else {
			orderSql.append(" o.createTime desc ");
		}
		String tsql = sql + whereSql.toString() + orderSql.toString();
		String tCountSql = countSql + whereSql.toString();
		TypedQuery<TuserPrizeDetail> q = entityManager.createQuery(tsql, TuserPrizeDetail.class);
		TypedQuery<Long> total = entityManager.createQuery(tCountSql, Long.class);
		if (conditionMap != null && conditionMap.size() > 0) {
			PropertyFilter.setMatchValue2Query(q, pfList);
			PropertyFilter.setMatchValue2Query(total, pfList);
		}
		q.setFirstResult(page.getPageIndex()).setMaxResults(page.getMaxResult());
		List<TuserPrizeDetail> resultList = q.getResultList();
		for (TuserPrizeDetail detail : resultList) {
			ActionJmsType type = ActionJmsType.get(detail.getActivityType());
			if (type != null) {
				detail.setActivityTypeMemo(type.memo);
			}
		}
		int count = total.getSingleResult().intValue();
		page.setList(resultList);
		page.setTotalResult(count);
	}

	public List<TuserPrizeDetail> findFailingAgencyPrizeDetails() {
		return entityManager.createQuery("SELECT o FROM TuserPrizeDetail o WHERE o.state != '1' ",
				TuserPrizeDetail.class).getResultList();
	}
}
