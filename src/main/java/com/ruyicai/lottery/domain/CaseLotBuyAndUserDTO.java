package com.ruyicai.lottery.domain;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

@RooJson
@RooJavaBean
public class CaseLotBuyAndUserDTO {
	private CaseLotBuy caseLotBuy;
	private Tuserinfo userinfo;
}
