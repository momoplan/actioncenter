package com.ruyicai.actioncenter.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * 
 * 充值金额转为可提现金额
 */
@RooJavaBean
@RooToString
@RooJson
@Entity()
@Table(name = "FUND2DRAW")
public class Fund2Draw {

	/**
	 * 充值交易id
	 */
	@Id
	@Column(name = "TTRANSACTIONID", length = 100)
	private String ttransactionid;

	@Column(name = "USERNO", length = 16)
	private String userno;

	@Column(name = "AMT")
	private BigDecimal amt;

	@Column(name = "CREATETIME")
	private Date createTime;

	@Column(name = "DRAWTIME")
	private Date drawTime;
	
	@Column(name = "SUCCESSTIME")
	private Date successTime;

	/**
	 * 0:未增加提现，1:以增加提现，2:参与活动不能提现
	 */
	@Column(name = "STATE")
	private int state;
}
