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
@Table(name = "prize_info")
public class PrizeInfo{

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private int id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "level")
	private String level;
	
	@Column(name = "sum")
	private int sum;
	
	@Column(name = "remain_num")
	private int remainNum;
	
	@Column(name = "arise_probability")
	private int ariseProbability;
	
	@Column(name = "delay_probability")
	private String delayProbability;
	
	@Column(name = "start_date")
	private Date startDate;
	
	@Column(name = "end_date")
	private Date endDate;
	
	@Column(name = "active_times")
	private String activeTimes;
	
	@Column(name = "valid")
	private String valid;
}
