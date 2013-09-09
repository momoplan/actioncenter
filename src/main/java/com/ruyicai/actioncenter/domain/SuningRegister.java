package com.ruyicai.actioncenter.domain;

import java.util.Date;
import java.util.List;

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

	public static SuningRegister findSuningRegisterByUserno(String userno) {
		if (userno == null || userno.length() == 0)
			return null;
		List<SuningRegister> resultList = entityManager()
				.createQuery("SELECT o FROM SuningRegister o WHERE o.userno = ?", SuningRegister.class)
				.setParameter(1, userno).getResultList();
		if (resultList != null && resultList.size() > 0) {
			return resultList.get(0);
		} else {
			return null;
		}
	}

}
