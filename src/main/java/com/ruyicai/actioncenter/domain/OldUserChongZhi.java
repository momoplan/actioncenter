package com.ruyicai.actioncenter.domain;

import java.math.BigDecimal;
import java.util.Date;

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
@RooEntity(versionField = "", table = "OldUserChongZhi", identifierField = "userno")
public class OldUserChongZhi {

	@Id
	@Column(name = "USERNO", length = 50)
	private String userno;

	@Column(name = "CHONGZHIAMT", columnDefinition = "decimal")
	private BigDecimal chongZhiAmt;

	@Column(name = "ZENGSONGAMT", columnDefinition = "decimal")
	private BigDecimal zengsongAmt;

	@Column(name = "CREATETIME")
	private Date createTime;

	@Transactional
	public static OldUserChongZhi createOldUserChongZhi(String userno, BigDecimal chongZhiAmt, BigDecimal zengsongAmt) {
		OldUserChongZhi oucz = new OldUserChongZhi();
		oucz.setUserno(userno);
		oucz.setChongZhiAmt(chongZhiAmt);
		oucz.setZengsongAmt(zengsongAmt);
		oucz.setCreateTime(new Date());
		oucz.persist();
		return oucz;
	}
}
