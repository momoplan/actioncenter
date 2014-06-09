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
 * 银联新用户充值活动
 * 
 * @author sunyang
 * 
 */
@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField = "", table = "YinLianNewUser", identifierField = "userno")
public class YinLianNewUser {
	
	@Id
	@Column(name = "USERNO", length = 50)
	private String userno;

	private BigDecimal amt;

	private Date createTime;

}
