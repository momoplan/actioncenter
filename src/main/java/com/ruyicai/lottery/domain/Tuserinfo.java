package com.ruyicai.lottery.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooJson
@RooToString
public class Tuserinfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String userno;

	private Date modtime;

	private Date regtime;

	private String qq;

	private String userName;

	private String msn;

	private String name;

	private String phone;

	private String nickname;

	private BigDecimal type;

	private String mobileid;

	private BigDecimal state;

	private String agencyno;

	private String password;

	private String certid;

	private String email;

	private String address;

	private String info;

	private String mac;

	private Character accesstype;

	private String channel;

	private String leave;

	private String subChannel;

	private String imei;

}
