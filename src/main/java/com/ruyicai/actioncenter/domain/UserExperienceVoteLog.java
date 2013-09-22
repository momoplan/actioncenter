package com.ruyicai.actioncenter.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TypedQuery;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import com.ruyicai.actioncenter.util.Page;
import com.ruyicai.actioncenter.util.Page.Sort;
import com.ruyicai.actioncenter.util.PropertyFilter;

@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField = "", table = "USEREXPERIENCEVOTELOG", identifierField = "id")
public class UserExperienceVoteLog {

	/**
	 * id自增
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;
	
	@Column(name = "VOTERUSERNO")
	private String voteruserno;
	
	@Column(name = "USERNO")
	private String userno;
	
	@Column(name = "VOTETIME")
	private Date votetime;
	
	/**
	 * 创建用户投票记录
	 * @param voteruserno	投票人用户id
	 * @param userno			被投票人用户id
	 * @return	UserExperienceVoteLog 用户投票记录
	 */
	public static UserExperienceVoteLog create(String voteruserno, String userno) {
		UserExperienceVoteLog userExperienceVoteLog = new UserExperienceVoteLog();
		userExperienceVoteLog.setVoteruserno(voteruserno);
		userExperienceVoteLog.setUserno(userno);
		userExperienceVoteLog.setVotetime(new Date());
		userExperienceVoteLog.persist();
		return userExperienceVoteLog;
	}
	
	/**
	 * 分页查询
	 * @param conditionMap
	 * @param page
	 */
	public static void findByPage(Map<String, Object> conditionMap,
			Page<UserExperienceVoteLog> page) {
		String sql = "SELECT o FROM UserExperienceVoteLog o ";
		String countSql = "SELECT count(*) FROM UserExperienceVoteLog o ";
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
			orderSql.append(" o.votetime DESC ");
		}
		String tsql = sql + whereSql.toString() + orderSql.toString();
		String tCountSql = countSql + whereSql.toString();
		System.out.println(tsql);
		TypedQuery<UserExperienceVoteLog> q = entityManager().createQuery(tsql, UserExperienceVoteLog.class);
		TypedQuery<Long> total = entityManager().createQuery(tCountSql, Long.class);
		if (conditionMap != null && conditionMap.size() > 0) {
			PropertyFilter.setMatchValue2Query(q, pfList);
			PropertyFilter.setMatchValue2Query(total, pfList);
		}
		q.setFirstResult(page.getPageIndex() * page.getMaxResult()).setMaxResults(page.getMaxResult());
		List<UserExperienceVoteLog> resultList = q.getResultList();
		int count = total.getSingleResult().intValue();
		page.setList(resultList);
		page.setTotalResult(count);
	}
}
