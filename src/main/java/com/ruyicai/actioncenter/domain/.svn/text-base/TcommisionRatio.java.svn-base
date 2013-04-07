package com.ruyicai.actioncenter.domain;

import java.math.BigDecimal;

import javax.persistence.Column;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * 代理用户佣金比例
 */
@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField = "", table = "TCOMMISIONRATIO", identifierField = "id")
public class TcommisionRatio {

	/** 用户编号 */
	@Column(name = "userno", nullable = false)
	private String userno;

	/** 彩种 */
	@Column(name = "lotno", nullable = false)
	private String lotno;

	/** 佣金比例 */
	@Column(name = "commisionRatio")
	private BigDecimal commisionRatio;

	/** 佣金类型:1 代理佣金,2被代理佣金 */
	@Column(name = "commisionType")
	private Integer commisionType;
}
