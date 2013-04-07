package com.ruyicai.actioncenter.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.LockModeType;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField = "", table = "SSCPRIZEDDETAIL", identifierField = "id")
public class SSCPrizedDetail {

	@EmbeddedId
	private SSCPrizedDetailPK id;

	@Column(name = "TOTALPRIZEAMT")
	private BigDecimal totalPrizeAmt;

	public static SSCPrizedDetail findSSCPrizedDetail(SSCPrizedDetailPK id, boolean lock) {
		if (id == null) {
			return null;
		}
		SSCPrizedDetail detail = entityManager().find(SSCPrizedDetail.class, id,
				lock ? LockModeType.PESSIMISTIC_WRITE : LockModeType.NONE);
		return detail;
	}

	@Transactional
	public static void addPrize(String userno, String day, BigDecimal prize) {
		SSCPrizedDetail detail = SSCPrizedDetail.findSSCPrizedDetail(new SSCPrizedDetailPK(userno, day), true);
		if (detail == null) {
			detail = new SSCPrizedDetail();
			detail.setId(new SSCPrizedDetailPK(userno, day));
			detail.setTotalPrizeAmt(prize);
			detail.persist();
		} else {
			detail.setTotalPrizeAmt(detail.getTotalPrizeAmt() == null ? prize : detail.getTotalPrizeAmt().add(prize));
			detail.merge();
		}
	}
}
