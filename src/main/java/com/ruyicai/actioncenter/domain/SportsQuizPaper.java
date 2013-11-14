package com.ruyicai.actioncenter.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
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

@RooJavaBean
@RooToString
@RooJson
@RooEntity(table = "SPORTSQUIZPAPER", versionField = "")
public class SportsQuizPaper {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name="mobileid", length = 20)
	private String mobileid;
	
	@Column(name = "answerid")
	private Integer answerid;
	
	@Column(name = "STATE")
	private Integer state;
	
	@Column(name = "createtime")
	private Date createtime;
	
	public static void findByPage(Page<SportsQuizPaper> page) {
		String sql = "SELECT o FROM SportsQuizPaper o ";
		String countSql = "SELECT count(*) FROM SportsQuizPaper o ";
		String whereSql = " WHERE o.state = 0";
		String tsql = sql + whereSql;
		String tCountSql = countSql + whereSql;
		TypedQuery<SportsQuizPaper> query = entityManager().createQuery(tsql, SportsQuizPaper.class);
		TypedQuery<Long> total = entityManager().createQuery(tCountSql, Long.class);
		query.setFirstResult(page.getPageIndex() * page.getMaxResult()).setMaxResults(page.getMaxResult());
		query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
		List<SportsQuizPaper> resultList = query.getResultList();
		int count = total.getSingleResult().intValue();
		page.setList(resultList);
		page.setTotalResult(count);
	}
	
	public static SportsQuizPaper findByMobileid(String mobileid) {
		String sql = "SELECT o FROM SportsQuizPaper o WHERE o.mobileid = ? ";
		TypedQuery<SportsQuizPaper> query = entityManager().createQuery(sql, SportsQuizPaper.class);
		query.setParameter(1, mobileid);
		List<SportsQuizPaper> resultList = query.getResultList();
		if(resultList == null || resultList.isEmpty() == true) {
			return null;
		} else {
			return resultList.get(0);
		}
	}

}
