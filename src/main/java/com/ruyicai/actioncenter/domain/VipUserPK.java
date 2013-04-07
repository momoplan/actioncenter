package com.ruyicai.actioncenter.domain;

import javax.persistence.Column;

import org.springframework.roo.addon.entity.RooIdentifier;
import org.springframework.roo.addon.tostring.RooToString;

@RooIdentifier
@RooToString
public class VipUserPK {

	private static final long serialVersionUID = 1L;

	/** 用户编号 */
	@Column(name = "USERNO", length = 50)
	public String userno;

	/** 年月 格式yyyy-MM */
	@Column(name = "YEARANDMONTH", length = 7)
	private String yearAndMonth;

}
