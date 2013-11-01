package com.ruyicai.actioncenter.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooJson
@org.hibernate.annotations.Entity(dynamicUpdate = true)
@Entity()
@Table(name = "VipUser")
public class VipUser {

	@EmbeddedId
	private VipUserPK id;

	@Column(name = "BUYAMT")
	public BigDecimal buyamt;

	@Column(name = "MODIFYTIME")
	public Date modifyTime;
}
