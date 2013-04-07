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
@RooEntity(versionField = "", table = "SUNINGREGISTER", identifierField = "mobileid")
public class SuningRegister {

	@Id
	@Column(name = "MOBILEID", length = 50)
	private String mobileid;

	@Column(name = "USERNO", length = 50)
	private String userno;

	@Column(name = "CREATETIME")
	private Date createTime;

	@Transactional
	public static SuningRegister createSuningRegister(String mobileid, String userno) {
		SuningRegister suningRegister = new SuningRegister();
		suningRegister.setMobileid(mobileid);
		suningRegister.setUserno(userno);
		suningRegister.setCreateTime(new Date());
		suningRegister.persist();
		return suningRegister;
	}

}
