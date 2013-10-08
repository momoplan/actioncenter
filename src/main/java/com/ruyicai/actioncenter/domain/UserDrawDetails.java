package com.ruyicai.actioncenter.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJson
@RooToString
@Entity
@Table(name = "user_draw_details")
public class UserDrawDetails{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private int id;
	
	@Column(name = "userno")
	private String userno;
	
	@Column(name = "prize_id")
	private int prizeId;
	
	@Column(name = "pay_object")
	private String payObject;
	
	@Column(name = "gain_object")
	private String gainObject;
	
	@Column(name = "draw_date")
	private Date drawDate;
}
