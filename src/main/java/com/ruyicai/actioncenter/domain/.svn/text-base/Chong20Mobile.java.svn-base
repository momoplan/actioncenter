package com.ruyicai.actioncenter.domain;

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
@RooEntity(versionField = "", table = "CHONG20MOBILE", identifierField = "mobileid")
public class Chong20Mobile {

	@Id
	@Column(name = "MOBILEID", length = 50)
	private String mobileid;

	@Column(name = "USERNO", length = 50)
	private String userno;

	@Transactional
	public static Chong20Mobile createChong20Mobile(String mobileid, String userno) {
		Chong20Mobile chong20Mobile = new Chong20Mobile();
		chong20Mobile.setMobileid(mobileid);
		chong20Mobile.setUserno(userno);
		chong20Mobile.persist();
		return chong20Mobile;
	}
}
