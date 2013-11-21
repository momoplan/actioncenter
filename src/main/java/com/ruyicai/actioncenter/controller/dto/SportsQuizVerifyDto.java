package com.ruyicai.actioncenter.controller.dto;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

@RooJson
@RooJavaBean
public class SportsQuizVerifyDto {

	private String username;
	
	private String password;
	
	private Integer correctAnswerid;
	
	private Integer userAnswerid;
}
