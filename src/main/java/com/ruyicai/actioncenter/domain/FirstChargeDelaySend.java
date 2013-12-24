package com.ruyicai.actioncenter.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

/**
 * 首次充值延迟赠送
 * 
 */
@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField = "", table = "FIRSTCHARGEDELAYSEND")
public class FirstChargeDelaySend {

	@Column(name = "USERNO", length = 20)
	private String userno;

	@Column(name = "AMT", columnDefinition = "decimal")
	private BigDecimal amt;

	@Column(name = "MEMO", length = 50)
	private String memo;

	@Column(name = "BUSINESSID", length = 50)
	private String businessId;

	@Column(name = "SENDTIME")
	private Date sendTime;

	@Column(name = "CREATETIME")
	private Date createTime;

	@Column(name = "SENDSTATE")
	private int sendState;

	@Transactional
	public static FirstChargeDelaySend createFirstChargeDelaySend(String userno, BigDecimal amt, String memo,
			Date sendTime, String businessId) {
		FirstChargeDelaySend fcds = new FirstChargeDelaySend();
		fcds.setUserno(userno);
		fcds.setAmt(amt);
		fcds.setMemo(memo);
		fcds.setSendTime(sendTime);
		fcds.setBusinessId(businessId);
		fcds.setCreateTime(new Date());
		fcds.setSendState(0);
		fcds.persist();
		return fcds;
	}

	public static List<FirstChargeDelaySend> findFirstChargeDelaySendBySendTime(Date sendTime) {
		return entityManager()
				.createQuery("SELECT o FROM FirstChargeDelaySend o WHERE o.sendState = 0 and o.sendTime <= :sendTime",
						FirstChargeDelaySend.class).setParameter("sendTime", sendTime).getResultList();
	}
}
