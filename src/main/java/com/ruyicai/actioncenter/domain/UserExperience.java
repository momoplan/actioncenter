package com.ruyicai.actioncenter.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.LockModeType;
import javax.persistence.Transient;
import javax.persistence.TypedQuery;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.util.Page;
import com.ruyicai.actioncenter.util.Page.Sort;
import com.ruyicai.actioncenter.util.PropertyFilter;
import com.ruyicai.lottery.domain.Tuserinfo;

/**
 * 用户体验官
 * @author 李晨星
 * @date 2013-9-17 上午10:29:54
 */
@RooJavaBean
@RooJson
@RooToString
@RooEntity(versionField = "", table = "USEREXPERIENCE", identifierField = "userno")
public class UserExperience implements Comparable<UserExperience> {

	/**
	 * 用户id
	 */
	@Id
	@Column(name = "USERNO", length = 20)
	private String userno;
	
	/**
	 * 姓名
	 */
	@Column(name = "NAME", length = 20)
	private String name;
	
	/**
	 * 年龄
	 */
	@Column(name = "AGE")
	private Integer age;
	
	/**
	 * 联系方式
	 */
	@Column(name = "CONTACT", length = 20)
	private String contact;
	
	/**
	 * 应聘时间
	 */
	@Column(name = "APPLYTIME")
	private Date applytime;
	
	/**
	 * 票数
	 */
	@Column(name = "VOTES")
	private Integer votes;
	
	/**
	 * 是否呗选中
	 * 0否1是
	 */
	@Column(name = "SELECTED")
	private Integer selected;
	
	/**
	 * 问题1	0-false 1-true
	 */
	@Column(name = "QUESTION1")
	private Integer question1;
	
	/**
	 * 问题2	0-false 1-true
	 */
	@Column(name = "QUESTION2")
	private Integer question2;
	
	/**
	 * 问题3
	 */
	@Column(name = "QUESTION3", columnDefinition="TEXT")
	private String question3;
	
	/**
	 * 问题4
	 */
	@Column(name = "QUESTION4", columnDefinition = "TEXT")
	private String question4;
	
	/**
	 * 问题5
	 */
	@Column(name = "QUESTION5", columnDefinition = "TEXT")
	private String question5;
	
	@Transient
	private Tuserinfo userinfo;
	
	@Transient
	private Integer rank;
	
	
	/**
	 * 创建UserExperience
	 * @return
	 */
	@Transactional
	public static UserExperience createUserExperience(UserExperience userExperience) {
		userExperience.setApplytime(new Date());
		userExperience.setVotes(0);
		userExperience.setSelected(0);
		userExperience.persist();
		return userExperience;
	}
	
	/**
	 * 查找，可加锁
	 * @param userno	用户id
	 * @param lock		true枷锁，false不加锁
	 * @return
	 */
	public static UserExperience findUserExperience(String userno, boolean lock) {
		UserExperience userExperience = entityManager().find(UserExperience.class, userno, lock ? LockModeType.PESSIMISTIC_WRITE : LockModeType.NONE);
		return userExperience;
	}
	
	/**
	 * 分页查询
	 * @param conditionMap
	 * @param page
	 */
	public static void findByPage(Map<String, Object> conditionMap,
			Page<UserExperience> page) {
		String sql = "SELECT o FROM UserExperience o ";
		String countSql = "SELECT count(*) FROM UserExperience o ";
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
			orderSql.append(" o.applytime DESC ");
		}
		String tsql = sql + whereSql.toString() + orderSql.toString();
		String tCountSql = countSql + whereSql.toString();
		TypedQuery<UserExperience> q = entityManager().createQuery(tsql, UserExperience.class);
		TypedQuery<Long> total = entityManager().createQuery(tCountSql, Long.class);
		if (conditionMap != null && conditionMap.size() > 0) {
			PropertyFilter.setMatchValue2Query(q, pfList);
			PropertyFilter.setMatchValue2Query(total, pfList);
		}
		q.setFirstResult(page.getPageIndex() * page.getMaxResult()).setMaxResults(page.getMaxResult());
		List<UserExperience> resultList = q.getResultList();
		int count = total.getSingleResult().intValue();
		page.setList(resultList);
		page.setTotalResult(count);
	}

	@Override
	public int compareTo(UserExperience o) {
		if(this.applytime.before(o.getApplytime())) {
			return 1;
		} else {
			return 0;
		}
	}


}
