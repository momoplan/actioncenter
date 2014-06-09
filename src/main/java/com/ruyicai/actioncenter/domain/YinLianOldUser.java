package com.ruyicai.actioncenter.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * 银联老用户充值活动
 * 
 * @author sunyang
 * 
 */
@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField = "", table = "YinLianOldUser", identifierField = "userno")
public class YinLianOldUser {

	@Id
	@Column(name = "USERNO", length = 50)
	private String userno;

	private BigDecimal amt;

	private Date createTime;
}
