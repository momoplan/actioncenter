package com.ruyicai.actioncenter.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * 用户奖金派发记录
 */
@RooJavaBean
@RooToString
@RooJson
@Entity()
@Table(name = "TUSERPRIZEDETAIL")
public class TuserPrizeDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id;

	/** 用户编号 */
	@Column(name = "userno")
	private String userno;

	/** 金额 */
	@Column(name = "amt")
	private BigDecimal amt;

	/** 活动类型 */
	@Column(name = "activityType")
	private Integer activityType;

	/** 创建时间 */
	@Column(name = "createTime")
	private Date createTime;

	/** 奖金是否派发成功。0：等待派发，1：派发成功 ，2：派发失败 */
	@Column(name = "state")
	private Integer state = 0;

	@Column(name = "businessId")
	private String businessId;

	/** 活动描述 */
	private transient String activityTypeMemo;

	public String getActivityTypeMemo() {
		return activityTypeMemo;
	}

	public void setActivityTypeMemo(String activityTypeMemo) {
		this.activityTypeMemo = activityTypeMemo;
	}

}
