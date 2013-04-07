package com.ruyicai.actioncenter.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import org.apache.commons.lang.StringUtils;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField = "", table = "OldUser", identifierField = "userno")
public class OldUser {

	@Id
	@Column(name = "USERNO", length = 50)
	public String userno;

	@Column(name = "state")
	public Integer state;

	@Column(name = "MODIFYTIME")
	public Date modifyTime;

	@Column(name = "HASJOIN")
	public Boolean hasJoin;

	@Transactional
	public static OldUser createOldUser(String userno) {
		OldUser user = new OldUser();
		user.setUserno(userno);
		user.setState(0);
		user.setModifyTime(new Date());
		user.setHasJoin(false);
		user.persist();
		return user;
	}

	@Transactional
	public static void updateStateDisable(String userno, boolean hasJoin) {
		if (StringUtils.isBlank(userno)) {
			throw new IllegalArgumentException("the arguments userno is required");
		}
		OldUser user = OldUser.findOldUser(userno);
		if (user != null) {
			user.setState(1);
			user.setModifyTime(new Date());
			user.setHasJoin(hasJoin);
			user.merge();
		}
	}
}
