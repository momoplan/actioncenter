package com.ruyicai.lottery.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJson
@RooToString
public class CaseLot {

	/** ID */
	private String id;

	/** 发起人用户编号 */
	private String starter;

	/** 合买最小购买金额。也代表每注多少钱oneamount */
	private Long minAmt;

	/** 方案总金额 */
	private Long totalAmt;

	/** 方案保底金额 */
	private Long safeAmt;

	/** 发起人购买金额 */
	private Long buyAmtByStarter;

	/** 佣金比例 */
	private BigDecimal commisionRatio;

	/** 方案开始时间 */
	private Date startTime;

	/** 方案结束时间 */
	private Date endTime;

	/** 方案内容保密状态 @see CaseLotState */
	private Integer visibility;

	/** 方案描述 */
	private String description;

	/** 方案标题 */
	private String title;

	/** 方案状态 */
	private BigDecimal state;

	/** 用户看到的状态 */
	private BigDecimal displayState;

	/** 用户看到的状态描述 */
	private String displayStateMemo;

	/** 方案内容描述 */
	private String content;

	/** 订单编号 */
	private String orderid;

	/** 彩种 */
	private String lotno;

	/** 期号 */
	private String batchcode;

	/** 是否中奖 */
	private BigDecimal winFlag;

	/** 暂时没用 */
	private Long winLittleAmt;

	/** 税后奖金 */
	private Long winBigAmt;

	/** 税前奖金 */
	private Long winPreAmt;

	/** 中奖时间 */
	private Date winStartTime;

	/** 中奖结束时间，暂无用 */
	private Date winEndTime;

	/** 中奖信息 */
	private String winDetail;

	/** 方案信息 */
	private String caselotinfo;

	/** 参与人购买金额 */
	private long buyAmtByFollower;

	/** 0：普通合买，3：申请置顶合买大厅，4:申请置顶合买中心，8：置顶合买大厅，9：置顶合买中心 */
	private Integer sortState;

	/** 参与人数 */
	private Long participantCount;

	/** 方案类型 */
	private BigDecimal lotsType;

	/** @deprecated 是否有战绩 0：没有，1：有 */
	private BigDecimal hasachievement;

	/** 是否中奖 0：没有，1：有.(取消的合买也会有中奖状态) */
	private BigDecimal isWinner;

	/** 玩法 */
	private String playtype;

	/**
	 * 认购是否已满
	 */
	public boolean isFull() {
		return this.buyAmtByStarter + this.buyAmtByFollower == this.totalAmt;
	}
}
