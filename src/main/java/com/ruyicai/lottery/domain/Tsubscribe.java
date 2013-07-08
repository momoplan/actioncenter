package com.ruyicai.lottery.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJson
public class Tsubscribe {

	private String flowno;

	/** 用户编号 */
	private String userno;

	/** 彩种编号 */
	private String lotno;

	/** 彩票购买期数 */
	private BigDecimal batchnum;

	/** 剩余购买期数 */
	private BigDecimal lastnum;

	/** 开始投注的期号 */
	private String beginbatch;

	/** 上次投注的期号 */
	private String lastbatch;

	/** 彩票注数 */
	private BigDecimal betnum;

	private BigDecimal drawway;

	private BigDecimal sellway;

	/** 彩票注码 */
	private String betcode;

	/** 单次投注金额 */
	private BigDecimal amount;

	/** 剩余投注总金额 */
	private BigDecimal totalamt;

	/** 订单创建时间 */
	private Date ordertime;

	/** 追号方案验证mac码 */
	private String mac;

	/** 0追号，1套餐 */
	private BigDecimal type;

	/** 0,正常，1 暂停，2注销,3结束 */
	private BigDecimal state;

	/** 结束时间 */
	private Date endtime;

	/** 倍数 */
	private BigDecimal lotmulti;

	private String channel;

	private String subchannel;

	/** 1:投注账户;2:奖金账户 */
	private String subaccount;

	/** 中奖停止追号，1：中奖停止，NUll：继续追号，不为NULL时中奖即停止 */
	private BigDecimal prizeend;

	/** 订单追号账户金额不足短信 1：发送，其他：不发送 */
	private BigDecimal accountnomoneysms;

	/** 追号描述 */
	private String memo;

	/** 最近修改时间 */
	private Date changetime;

	private String desc;

	/** 追号中大奖并且奖金大于等于prizeendamt停止追号 */
	private BigDecimal prizeendamt;

	/** 达到累计收益金额，则停止追号 */
	private BigDecimal leijiprizeendamt;

	/** 追号剩余期不足短信 1：未发送追；2：已发送 */
	private BigDecimal endsms;

	/** 是否可以撤销 1:已付款，不可以； 其他：可以 */
	private BigDecimal cancancel;

	/** 总追号期数 */
	private Integer totalBatchCount;

	/** 已追号期数 */
	private Integer hasAddBatchCount;

	/**
	 * 订单信息
	 */
	private String orderinfo;

	/**
	 * @see com.ruyicai.lottery.consts.TorderState
	 */
	private BigDecimal paytype;
}
