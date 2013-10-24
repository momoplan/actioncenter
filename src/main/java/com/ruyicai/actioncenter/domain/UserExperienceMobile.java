package com.ruyicai.actioncenter.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户体验官投票手机号记录表
 * @author LiChenxing
 * @date 2013年10月24日 上午11:12:23
 */
@RooJavaBean
@RooJson
@RooEntity(versionField = "", table = "USEREXPERIENCEMOBILE", identifierField = "mobileid")
public class UserExperienceMobile {
	
	@Id
	@Column(name = "MOBILEID", length = 50)
	private String mobileid;

	@Column(name = "USERNO", length = 50)
	private String userno;
	
	@Column(name = "CREATETIME")
	private Date createtime;
	
	@Transactional
	public static UserExperienceMobile createUserExperienceMobile(String mobileid, String userno) {
		UserExperienceMobile uem = new UserExperienceMobile();
		uem.setMobileid(mobileid);
		uem.setUserno(userno);
		uem.setCreatetime(new Date());
		uem.persist();
		return uem;
	}
}
