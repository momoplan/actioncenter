package com.ruyicai.actioncenter.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;

import org.apache.commons.lang.StringUtils;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import com.ruyicai.actioncenter.consts.ActionJmsType;
import com.ruyicai.actioncenter.util.Page;
import com.ruyicai.actioncenter.util.Page.Sort;
import com.ruyicai.actioncenter.util.PropertyFilter;

/**
 * 用户奖金派发记录
 */
@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField = "", table = "TUSERPRIZEDETAIL", identifierField = "id")
public class TuserPrizeDetail {

	/** 用户编号 */
	@Column(name = "userno")
	private String userno;

	/** 金额 */
	@Column(name = "amt")
	private BigDecimal amt;

	/** 活动类型 */
	@Column(name = "activityType")
	private Integer activityType;

	/** 创建时间 */
	@Column(name = "createTime")
	private Date createTime;

	/** 奖金是否派发成功。0：等待派发，1：派发成功 ，2：派发失败 */
	@Column(name = "state")
	private Integer state = 0;

	@Column(name = "businessId")
	private String businessId;

	/** 活动描述 */
	private transient String activityTypeMemo;

	public static TuserPrizeDetail createTprizeUserBuyLog(String userno, BigDecimal amt, ActionJmsType actionJmsType) {
		return createTprizeUserBuyLog(userno, amt, actionJmsType, null);
	}

	public static TuserPrizeDetail createTprizeUserBuyLog(String userno, BigDecimal amt, ActionJmsType actionJmsType,
			String businessId) {
		if (StringUtils.isBlank(userno)) {
			throw new IllegalArgumentException("the argument parentAgentUserno is required");
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
		log.persist();
		return log;
	}

	public static TuserPrizeDetail findTuserPrizeDetail(Long id, boolean lock) {
		EntityManager entityManager = entityManager();
		TuserPrizeDetail detail = entityManager.find(TuserPrizeDetail.class, id, lock ? LockModeType.PESSIMISTIC_WRITE
				: LockModeType.NONE);
		return detail;
	}

	public static TuserPrizeDetail findTuserPrizeDetailByUsernoAndActivityType(String userno, Integer activityType) {
		EntityManager entityManager = entityManager();
		List<TuserPrizeDetail> resultList = entityManager
				.createQuery(
						"select o from TuserPrizeDetail o where o.userno = ? and o.activityType = ? order by o.createTime desc",
						TuserPrizeDetail.class).setParameter(1, userno).setParameter(2, activityType).getResultList();
		if (resultList != null && resultList.size() > 0) {
			return resultList.get(0);
		} else {
			return null;
		}
	}

	public static void findTuserPrizeDetailByPage(Map<String, Object> conditionMap, Page<TuserPrizeDetail> page) {
		EntityManager entityManager = entityManager();
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

	public static List<TuserPrizeDetail> findFailingAgencyPrizeDetails() {
		EntityManager em = entityManager();
		return em.createQuery("SELECT o FROM TuserPrizeDetail o WHERE o.state != '1' ", TuserPrizeDetail.class)
				.getResultList();
	}

	public String getActivityTypeMemo() {
		return activityTypeMemo;
	}

	public void setActivityTypeMemo(String activityTypeMemo) {
		this.activityTypeMemo = activityTypeMemo;
	}

}
