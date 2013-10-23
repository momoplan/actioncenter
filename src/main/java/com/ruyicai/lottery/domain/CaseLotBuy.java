package com.ruyicai.lottery.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJson
@RooToString
public class CaseLotBuy {

	/** ID */
	private Long id;

	/** 合买ID */
	private String caselotId;

	/** 用户编号 */
	private String userno;

	/** 购买金额 */
	private BigDecimal num;

	/** 购买时间 */
	private Date buyTime;

	/** 1:正常，0:撤资 */
	private Integer flag;

	/** 购买时的保底金额 */
	private BigDecimal safeAmt;

	/** 冻结的保底金额，也就是实际可保底的金额 */
	private BigDecimal freezeSafeAmt;

	/** 购买类型。1:发起者，0:参与者 */
	private Integer buyType;

	/** 中奖金额 */
	private BigDecimal prizeAmt;

	/** 佣金金额 */
	private BigDecimal commisionPrizeAmt;

	/** 渠道 */
	private String channel;

}
