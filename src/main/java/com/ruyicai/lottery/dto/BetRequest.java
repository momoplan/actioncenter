package com.ruyicai.lottery.dto;

import java.math.BigDecimal;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

@RooJavaBean
@RooJson
public class BetRequest {

	private String betcode;

	private BigDecimal amt;
	/**	投注方式，（0-单式，1-复式，2-胆拖，3-单式上传）*/
	private BigDecimal drawway;
}
