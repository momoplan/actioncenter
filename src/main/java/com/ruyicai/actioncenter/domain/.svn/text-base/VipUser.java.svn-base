package com.ruyicai.actioncenter.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;

import org.apache.commons.lang.StringUtils;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField = "", table = "VipUser", identifierField = "id")
public class VipUser {

	@EmbeddedId
	private VipUserPK id;

	@Column(name = "BUYAMT")
	public BigDecimal buyamt;

	@Column(name = "MODIFYTIME")
	public Date modifyTime;

	@Transactional
	public static VipUser findIfNotExistsCreate(String userno, String yearAndMonth) {
		if (StringUtils.isBlank(userno)) {
			throw new IllegalArgumentException("the arguments userno is require");
		}
		if (StringUtils.isBlank(yearAndMonth)) {
			throw new IllegalArgumentException("the arguments yearAndMonth is require");
		}
		if (!yearAndMonth.matches("^\\d{4}\\-\\d{2}")) {
			throw new IllegalArgumentException("the arguments yearAndMonth format error,eg:2012-05");
		}
		VipUser vipUser = findVipUser(new VipUserPK(userno, yearAndMonth));
		if (vipUser != null) {
			return vipUser;
		} else {
			vipUser = new VipUser();
			vipUser.setId(new VipUserPK(userno, yearAndMonth));
			vipUser.setBuyamt(BigDecimal.ZERO);
			vipUser.setModifyTime(new Date());
			vipUser.persist();
			return vipUser;
		}
	}
}
