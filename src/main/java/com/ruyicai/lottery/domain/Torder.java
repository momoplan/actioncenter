package com.ruyicai.lottery.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJson
@RooToString
public class Torder {

	private String id;

	private String batchcode;

	private String lotno;

	private BigDecimal amt;

	private BigDecimal paytype;

	private BigDecimal orderstate;

	private BigDecimal bettype;

	private BigDecimal prizestate;

	private BigDecimal orderprizeamt;

	private BigDecimal orderpreprizeamt;

	/** @deprecated 是否有战绩 0：没有，1：有 */
	private BigDecimal hasachievement;

	private String winbasecode;

	private BigDecimal ordertype;

	private String tsubscribeflowno;

	private String tlotcaseid;

	private Date createtime;

	private String userno;

	private String buyuserno;

	private String memo;

	private String subaccount;

	private BigDecimal betnum;

	private Date canceltime;

	private Date endtime;

	private String desc;

	private String betcode;

	private BigDecimal alreadytrans;

	private BigDecimal lotmulti;

	private String prizeinfo;

	private String orderinfo;

	private String body;

	private BigDecimal instate;

	private BigDecimal paystate;

	/** 方案类型 */
	private BigDecimal lotsType;

	private Date encashtime;

	private String eventcode;

	private String agencyno;

	private String channel;

	private String subchannel;

	private String playtype;

	private transient BigDecimal orderamt;

	private transient Date modifytime;

	private transient BigDecimal orderprize;

}
