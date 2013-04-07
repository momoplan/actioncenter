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

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.util.Page;
import com.ruyicai.actioncenter.util.Page.Sort;
import com.ruyicai.actioncenter.util.PropertyFilter;

@RooJavaBean
@RooToString
@RooJson
@RooEntity(table = "SENDMONEYDETAILS", versionField = "")
public class SendMoneyDetails {

	/** ID */
	@Id
	@GenericGenerator(name = "generator", strategy = "org.hibernate.id.UUIDHexGenerator")
	@GeneratedValue(generator = "generator")
	@Column(name = "ID")
	private String id;

	/** mgr中赠送的用户名 */
	@Column(name = "SENDUSERNAME", length = 50)
	private String sendusername;

	/** 接收人用户编号 */
	@Column(name = "RECIVERUSERNO", length = 50)
	private String reciverUserno;

	/** 红包内容描述 */
	@Column(name = "CONTENT", columnDefinition = "text")
	private String content;

	/** 赠送金额 */
	@Column(name = "AMT", columnDefinition = "decimal")
	private BigDecimal amt;

	/** 领取时间 */
	@Column(name = "RECIVETIME")
	private Date reciveTime;

	/** 创建时间 */
	@Column(name = "CREATETIME")
	private Date createTime;

	/**
	 * 领取状态 0:未领取，1:已领取
	 */
	@Column(name = "RECIVESTATE")
	private Integer reciveState;

	public static SendMoneyDetails findSendMoneyDetails(String id, boolean lock) {
		EntityManager entityManager = SendMoneyDetails.entityManager();
		SendMoneyDetails details = entityManager.find(SendMoneyDetails.class, id, lock ? LockModeType.PESSIMISTIC_WRITE
				: LockModeType.NONE);
		return details;
	}

	@Transactional
	public static SendMoneyDetails createSendMoneyDetails(String sendusername, String reciverUserno, BigDecimal amt,
			String content) {
		SendMoneyDetails details = new SendMoneyDetails();
		details.setSendusername(sendusername);
		details.setReciverUserno(reciverUserno);
		details.setAmt(amt);
		details.setContent(content);
		details.setCreateTime(new Date());
		details.setReciveState(0);
		details.persist();
		return details;
	}

	public static void findReciverSendMoneyDetailsByPage(String reciverUserno, Map<String, Object> conditionMap,
			Page<SendMoneyDetails> page) {
		EntityManager em = SendMoneyDetails.entityManager();
		String sql = "SELECT o FROM SendMoneyDetails o ";
		String countSql = "SELECT count(*) FROM SendMoneyDetails o ";
		StringBuilder whereSql = new StringBuilder(" WHERE 1=1 ");
		List<PropertyFilter> pfList = null;
		if (conditionMap != null && conditionMap.size() > 0) {
			pfList = PropertyFilter.buildFromMap(conditionMap);
			String buildSql = PropertyFilter.transfer2Sql(pfList, "o");
			whereSql.append(buildSql);
		}
		if (StringUtils.isNotBlank(reciverUserno)) {
			whereSql.append(" AND o.reciverUserno = :reciverUserno ");
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
		TypedQuery<SendMoneyDetails> q = em.createQuery(tsql, SendMoneyDetails.class);
		TypedQuery<Long> total = em.createQuery(tCountSql, Long.class);
		if (conditionMap != null && conditionMap.size() > 0) {
			PropertyFilter.setMatchValue2Query(q, pfList);
			PropertyFilter.setMatchValue2Query(total, pfList);
		}
		if (StringUtils.isNotBlank(reciverUserno)) {
			q.setParameter("reciverUserno", reciverUserno);
			total.setParameter("reciverUserno", reciverUserno);
		}
		q.setFirstResult(page.getPageIndex()).setMaxResults(page.getMaxResult());
		List<SendMoneyDetails> resultList = q.getResultList();
		int count = total.getSingleResult().intValue();
		page.setList(resultList);
		page.setTotalResult(count);
	}

	public static void findSenderSendMoneyDetailsByPage(Map<String, Object> conditionMap, Page<SendMoneyDetails> page) {
		EntityManager em = SendMoneyDetails.entityManager();
		String sql = "SELECT o FROM SendMoneyDetails o ";
		String countSql = "SELECT count(*) FROM SendMoneyDetails o ";
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
		TypedQuery<SendMoneyDetails> q = em.createQuery(tsql, SendMoneyDetails.class);
		TypedQuery<Long> total = em.createQuery(tCountSql, Long.class);
		if (conditionMap != null && conditionMap.size() > 0) {
			PropertyFilter.setMatchValue2Query(q, pfList);
			PropertyFilter.setMatchValue2Query(total, pfList);
		}
		q.setFirstResult(page.getPageIndex()).setMaxResults(page.getMaxResult());
		List<SendMoneyDetails> resultList = q.getResultList();
		int count = total.getSingleResult().intValue();
		page.setList(resultList);
		page.setTotalResult(count);
	}
}
