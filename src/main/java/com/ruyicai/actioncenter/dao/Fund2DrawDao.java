package com.ruyicai.actioncenter.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.consts.Const;
import com.ruyicai.actioncenter.domain.Fund2Draw;
import com.ruyicai.actioncenter.util.Page;
import com.ruyicai.actioncenter.util.PropertyFilter;
import com.ruyicai.actioncenter.util.Page.Sort;

@Component
public class Fund2DrawDao {

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public void persist(Fund2Draw draw) {
		this.entityManager.persist(draw);
	}

	@Transactional
	public void remove(Fund2Draw draw) {
		if (this.entityManager.contains(draw)) {
			this.entityManager.remove(draw);
		} else {
			Fund2Draw attached = this.findFund2Draw(draw.getTtransactionid());
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
	public Fund2Draw merge(Fund2Draw draw) {
		Fund2Draw merged = this.entityManager.merge(draw);
		this.entityManager.flush();
		return merged;
	}

	public Fund2Draw createFund2Draw(String ttransactionid, String userno, BigDecimal amt) {
		Date date = new Date();
		Fund2Draw draw = new Fund2Draw();
		draw.setTtransactionid(ttransactionid);
		draw.setUserno(userno);
		draw.setAmt(amt);
		draw.setCreateTime(date);
		draw.setDrawTime(new Date(date.getTime()+Const.delayDay));
		draw.setState(0);
		this.persist(draw);
		return draw;
	}

	public Fund2Draw findFund2Draw(String ttransactionid, boolean lock) {
		Fund2Draw draw = this.entityManager.find(Fund2Draw.class, ttransactionid, lock ? LockModeType.PESSIMISTIC_WRITE
				: LockModeType.NONE);
		return draw;
	}

	public Fund2Draw findFund2Draw(String ttransactionid) {
		return findFund2Draw(ttransactionid, false);
	}

	@Transactional
	public Fund2Draw updateState(String ttransactionid, int state) {
		Fund2Draw draw = findFund2Draw(ttransactionid, true);
		if (draw != null) {
			draw.setState(state);
			if (state == 1) {
				draw.setDrawTime(new Date());
			}
			this.merge(draw);
		}
		return draw;
	}

	public List<Fund2Draw> findCanFund2Draw(Integer state, Date date) {
		if (state == null || date == null) {
			throw new IllegalArgumentException("the argument state and date are both require");
		}
		TypedQuery<Fund2Draw> q = this.entityManager
				.createQuery(
						"SELECT o FROM Fund2Draw AS o WHERE o.state = :state AND o.drawTime <= :drawTime order by o.createTime desc",
						Fund2Draw.class);
		q.setParameter("state", state);
		q.setParameter("drawTime", date);
		q.setMaxResults(5000);
		return q.getResultList();
	}
	
	public void findFund2DrawByPage(Map<String, Object> conditionMap, Page<Fund2Draw> page) {
		String sql = "SELECT o FROM Fund2Draw o ";
		String countSql = "SELECT count(*) FROM Fund2Draw o ";
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
			orderSql.append(" o.createTime DESC ");
		}
		String tsql = sql + whereSql.toString() + orderSql.toString();
		String tCountSql = countSql + whereSql.toString();
		TypedQuery<Fund2Draw> q = entityManager.createQuery(tsql, Fund2Draw.class);
		TypedQuery<Long> total = entityManager.createQuery(tCountSql, Long.class);
		if (conditionMap != null && conditionMap.size() > 0) {
			PropertyFilter.setMatchValue2Query(q, pfList);
			PropertyFilter.setMatchValue2Query(total, pfList);
		}
		q.setFirstResult(page.getPageIndex() * page.getMaxResult()).setMaxResults(page.getMaxResult());
		List<Fund2Draw> resultList = q.getResultList();
		int count = total.getSingleResult().intValue();
		page.setList(resultList);
		page.setTotalResult(count);
	}
}
