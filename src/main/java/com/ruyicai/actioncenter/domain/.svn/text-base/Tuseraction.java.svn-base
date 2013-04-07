package com.ruyicai.actioncenter.domain;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * 购彩活动记录新用户总采购金额表
 */
@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField = "", table = "TUSERACTION", identifierField = "id", finders = { "findTuseractionsByUsernoEquals" })
public class Tuseraction {

	/** 用户编号 */
	@Column(name = "userno", nullable = false)
	private String userno;

	/** 创建时间 */
	@Column(name = "createTime", nullable = false)
	private Date createTime;

	/** 总购彩金额 */
	@Column(name = "totalBuyAmt")
	private BigDecimal totalBuyAmt;

	public static Tuseraction createIfNotExists(String userno, BigDecimal amt) {
		Tuseraction tuseraction = null;
		List<Tuseraction> resultList = Tuseraction.findTuseractionsByUsernoEquals(userno).getResultList();
		if (resultList != null && resultList.size() > 0) {
			tuseraction = resultList.get(0);
			tuseraction.setTotalBuyAmt(tuseraction.getTotalBuyAmt().add(amt));
			tuseraction.merge();
		} else {
			tuseraction = new Tuseraction();
			tuseraction.setUserno(userno);
			tuseraction.setTotalBuyAmt(amt == null ? BigDecimal.ZERO : amt);
			tuseraction.setCreateTime(new Date());
			tuseraction.persist();
			tuseraction.flush();
		}
		return tuseraction;
	}
}
