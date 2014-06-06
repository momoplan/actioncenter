package com.ruyicai.actioncenter.domain;

import javax.persistence.Id;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooJson
@RooEntity(versionField = "", table = "WORLDCUPBIGUSER")
public class WorldCupBigUser {

	@Id
	String userno;
}
