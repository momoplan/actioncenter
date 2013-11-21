package com.ruyicai.actioncenter.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

@RooJavaBean
@RooToString
@RooJson
@RooEntity(table = "SPORTSQUIZUSERLOG", versionField = "")
public class SportsQuizUserLog {
	
	@Id
	@Column(name = "username")
	private String username;
	
	@Column(name = "password")
	private String password;
	
	@Column(name = "createtime")
	private Date createtime;
	
	@Transactional
	public static SportsQuizUserLog saveSportsQuizUserLog(String username, String password) {
		SportsQuizUserLog squl = new SportsQuizUserLog();
		squl.setUsername(username);
		squl.setPassword(password);
		squl.setCreatetime(new Date());
		squl.persist();
		return squl;
	}
}
