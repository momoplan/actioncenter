package com.ruyicai.actioncenter.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.LockModeType;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField = "", table = "FIRSTCHARGEUSER", identifierField = "userno")
public class FirstChargeUser {

	@Id
	@Column(name = "USERNO", length = 50)
	private String userno;

	@Column(name = "CREATETIME")
	private Date createTime;

	@Column(name = "STATE")
	private Integer state;

	@Column(name = "PRIZETIME")
	private Date prizeTime;

	@Column(name = "TTRANSACTIONID")
	private String ttransactionid;

	public static FirstChargeUser findFirstChargeUser(String userno, boolean lock) {
		FirstChargeUser fcu = entityManager().find(FirstChargeUser.class, userno,
				lock ? LockModeType.PESSIMISTIC_WRITE : LockModeType.NONE);
		return fcu;
	}

	public static FirstChargeUser createFirstChargeUser(String userno, Integer state, String ttransactionid) {
		FirstChargeUser fcu = new FirstChargeUser();
		fcu.setUserno(userno);
		fcu.setCreateTime(new Date());
		fcu.setState(state);
		fcu.setTtransactionid(ttransactionid);
		entityManager().persist(fcu);
		return fcu;
	}

	public static FirstChargeUser updateFirstChargeUser(FirstChargeUser fcu, Integer state) {
		if (fcu != null) {
			fcu.setState(state);
			fcu.setPrizeTime(new Date());
			entityManager().merge(fcu);
		}
		return fcu;
	}

	public static FirstChargeUser findFirstChargeUser(String userno, Integer state) {
		List<FirstChargeUser> resultList = entityManager()
				.createQuery("SELECT o FROM FirstChargeUser o WHERE o.userno = :userno and o.state = :state",
						FirstChargeUser.class).setParameter("userno", userno).setParameter("state", state)
				.getResultList();
		if (resultList != null && resultList.size() > 0) {
			return resultList.get(0);
		} else {
			return null;
		}
	}
}
