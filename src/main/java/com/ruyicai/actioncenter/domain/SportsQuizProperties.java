package com.ruyicai.actioncenter.domain;

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

@RooJavaBean
@RooToString
@RooJson
@RooEntity(table = "SPORTSQUIZPROPERTIES", versionField = "")
public class SportsQuizProperties {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "spkey", length = 100, unique = true)
	private String spkey;
	
	@Column(name = "spvalue", length = 100)
	private String spvalue;
	
	public static SportsQuizProperties getSportsQuizPropertiesByKeyAndLock(String key) {
		TypedQuery<SportsQuizProperties> query = entityManager().createQuery("SELECT o FROM SportsQuizProperties o WHERE o.spkey = ?", SportsQuizProperties.class);
		query.setParameter(1, key).setLockMode(LockModeType.PESSIMISTIC_WRITE);
		List<SportsQuizProperties> list = query.getResultList();
		if(list == null || list.size() == 0) {
			return null;
		} else {
			return list.get(0);
		}
	}
	
	public static SportsQuizProperties getSportsQuizPropertiesByKey(String key) {
		TypedQuery<SportsQuizProperties> query = entityManager().createQuery("SELECT o FROM SportsQuizProperties o WHERE o.spkey = ?", SportsQuizProperties.class);
		query.setParameter(1, key);
		List<SportsQuizProperties> list = query.getResultList();
		if(list == null || list.size() == 0) {
			return null;
		} else {
			return list.get(0);
		}
	}
}
