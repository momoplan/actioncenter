package com.ruyicai.actioncenter.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import com.ruyicai.actioncenter.consts.ActionJmsType;

@RooJavaBean
@RooToString
@RooEntity(identifierType = TjmsservicePK.class, versionField = "", table = "TJMSSERVICE")
public class Tjmsservice {

	@Column(name = "memo", length = 100)
	private String memo;
	@Column(name = "processtime")
	private Date processtime;

	private static Tjmsservice createTjmsservice(String value, BigDecimal type, Date processtime, String memo) {
		TjmsservicePK id = new TjmsservicePK(value, type);
		Tjmsservice tjmsservice = new Tjmsservice();
		tjmsservice.setId(id);
		tjmsservice.setMemo(memo);
		tjmsservice.setProcesstime(processtime);
		tjmsservice.persist();
		return tjmsservice;
	}

	public static boolean createTjmsservice(String value, ActionJmsType actionJmsType) {
		TjmsservicePK id = new TjmsservicePK(value, actionJmsType.decimal());
		Tjmsservice tjmsservice = Tjmsservice.findTjmsservice(id);
		if (tjmsservice == null) {
			Tjmsservice.createTjmsservice(value, actionJmsType.decimal(), new Date(), actionJmsType.memo);
			return true;
		} else {
			return false;
		}
	}
}
