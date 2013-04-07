package com.ruyicai.actioncenter.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField = "", table = "TaddNumActivity")
public class TaddNumActivity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "USERNO", length = 10)
	private String userno;

	@Column(name = "FLOWNO", length = 16)
	private String flowno;

	@Column(name = "CREATETIME")
	private Date createTime;

	@Column(name = "MODIFYTIME")
	private Date modifyTime;

	@Column(name = "ADDNUMSTATE", length = 1, scale = 0)
	private BigDecimal addNumState;

	@Column(name = "PARAMS", length = 1000)
	private String params;

	@Transactional
	public static TaddNumActivity createTaddNumActivity(String userno, String params) {
		TaddNumActivity activity = new TaddNumActivity();
		activity.setUserno(userno);
		activity.setParams(params);
		activity.setAddNumState(BigDecimal.ZERO);
		activity.setCreateTime(new Date());
		activity.persist();
		return activity;
	}

	@Transactional
	public TaddNumActivity updateTaddNumActivity(String flowno) {
		this.setFlowno(flowno);
		this.setAddNumState(BigDecimal.ONE);
		this.setModifyTime(new Date());
		this.merge();
		return this;
	}

	public static TaddNumActivity findTaddNumActivityByFlownoAndUserno(String flowno, String userno) {
		List<TaddNumActivity> resultList = entityManager()
				.createQuery("SELECT o FROM TaddNumActivity o WHERE o.flowno = ? and o.userno = ? and o.addNumState = '1' ",
						TaddNumActivity.class).setParameter(1, flowno).setParameter(2, userno).getResultList();
		if (resultList != null && resultList.size() > 0) {
			return resultList.get(0);
		} else {
			return null;
		}
	}
}
