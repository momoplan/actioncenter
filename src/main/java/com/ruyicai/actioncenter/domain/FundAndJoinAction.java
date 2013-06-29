package com.ruyicai.actioncenter.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * 
 * 充值并参与活动
 */
@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField = "", table = "FUNDANDJOINACTION", identifierField = "ttransactionid")
public class FundAndJoinAction {

	/**
	 * 充值交易id
	 */
	@Id
	@Column(name = "TTRANSACTIONID", length = 100)
	private String ttransactionid;

	@Column(name = "USERNO", length = 16)
	private String userno;

	/** 活动类型 */
	@Column(name = "ACTIVITYTYPE")
	private Integer activityType;

	@Column(name = "TUSERPRIZEDETAILID")
	private Long tuserPrizeDetailId;

	/** 创建时间 */
	@Column(name = "CREATETIME")
	private Date createTime;

	public static FundAndJoinAction createFundAndJoinAction(String ttransactionid, String userno, Integer activityType,
			Long tuserPrizeDetailId) {
		FundAndJoinAction fundAndJoinAction = new FundAndJoinAction();
		fundAndJoinAction.setTtransactionid(ttransactionid);
		fundAndJoinAction.setUserno(userno);
		fundAndJoinAction.setActivityType(activityType);
		fundAndJoinAction.setTuserPrizeDetailId(tuserPrizeDetailId);
		fundAndJoinAction.setCreateTime(new Date());
		fundAndJoinAction.persist();
		return fundAndJoinAction;
	}
}
