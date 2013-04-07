package com.ruyicai.lottery.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * 用户战绩信息
 */
@RooJavaBean
@RooJson
@RooToString
public class Tuserachievement {

	private Long id;

	/** 用户编号 */
	private String userno;

	/** 彩种 */
	private String lotno;

	/** 有效战绩 */
	private BigDecimal effectiveAchievement;

	/** 灰色战绩 */
	private BigDecimal ineffectiveAchievement;

	/** 最近修改时间 */
	private Date lastModifyTime;
}
