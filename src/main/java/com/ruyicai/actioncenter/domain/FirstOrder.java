package com.ruyicai.actioncenter.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField = "", table = "FIRSTORDER", identifierField = "userno")
public class FirstOrder {

	@Id
	@Column(name = "USERNO", length = 50)
	private String userno;

	@Column(name = "ORDERID")
	private String orderid;

}
