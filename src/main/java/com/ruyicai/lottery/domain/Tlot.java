package com.ruyicai.lottery.domain;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

import com.ruyicai.actioncenter.consts.SubaccountType;

@RooToString
@RooJson
public class Tlot {

	private String flowno;

	private String userno;

	private String lotno;

	private String lotpwd;

	private String agencyno;

	private String batchcode;

	private String validcode;

	private String runcode;

	private BigDecimal betnum;

	private BigDecimal drawway;

	private BigDecimal sellway;

	private String betcode;

	private String checkcode;

	private BigDecimal amt;

	private Date ordertime;

	private BigDecimal settleflag;

	private BigDecimal prizeamt;

	private BigDecimal preprizeamt;

	private BigDecimal prizelittle;

	private String prizeinfo = " ";

	private Date prizetime;

	private String machineno;

	private Date giveuptime;

	private BigDecimal state;

	private BigDecimal transferstate;

	private BigDecimal bettype;

	private String subscribeno;

	private String pbatchcode;

	private String caseid;

	private String buyUserno;

	private BigDecimal lotmulti;

	private String channel;

	private String subchannel;

	private String torderid;

	private BigDecimal instate;

	private String subaccount;

	private BigDecimal paystate;

	private String currentbetcode;

	private BigDecimal failnum;

	private Date lotcenterordertime;

	private String lotcentercashterm;

	private Date lotcentercashtime;

	private String messageid;

	private BigDecimal returnvalue;

	private String eventcode;

	private BigDecimal lotcenterstate;

	private BigDecimal lotcenterisvalid;

	private String maxtermid;

	private String peilu;

	private BigDecimal realprizeamt;

	private Date printtime;

	private Date latestprinttime;

	private String saletime;

	private BigDecimal sendnum;

	private String errmsg;

	public String getErrmsg() {
		return errmsg;
	}

	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}

	private transient String tlotresultid;

	public String getLotcentercashterm() {
		return lotcentercashterm;
	}

	public void setLotcentercashterm(String lotcentercashterm) {
		this.lotcentercashterm = lotcentercashterm;
	}

	public Date getLotcentercashtime() {
		return lotcentercashtime;
	}

	public void setLotcentercashtime(Date lotcentercashtime) {
		this.lotcentercashtime = lotcentercashtime;
	}

	public String getTlotresultid() {
		return tlotresultid;
	}

	public void setTlotresultid(String tlotresultid) {
		this.tlotresultid = tlotresultid;
	}

	public String getCurrentbetcode() {
		return currentbetcode;
	}

	public void setCurrentbetcode(String currentbetcode) {
		this.currentbetcode = currentbetcode;
	}

	public BigDecimal getFailnum() {
		return failnum;
	}

	public void setFailnum(BigDecimal failnum) {
		this.failnum = failnum;
	}

	public BigDecimal getPaystate() {
		return paystate;
	}

	public void setPaystate(BigDecimal paystate) {
		this.paystate = paystate;
	}

	public SubaccountType getSubaccountType() {
		if (StringUtils.isBlank(subaccount)) {
			return null;
		}
		try {
			return SubaccountType.valueOf(subaccount);
		} catch (Exception e) {
			return null;
		}
	}

	public String getSubaccount() {
		return subaccount;
	}

	public void setSubaccount(String subaccount) {
		this.subaccount = subaccount;
	}

	public BigDecimal getInstate() {
		return instate;
	}

	public void setInstate(BigDecimal instate) {
		this.instate = instate;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getSubchannel() {
		return subchannel;
	}

	public void setSubchannel(String subchannel) {
		this.subchannel = subchannel;
	}

	public BigDecimal getLotmulti() {
		return lotmulti;
	}

	public void setLotmulti(BigDecimal lotmulti) {
		this.lotmulti = lotmulti;
	}

	public String getFlowno() {
		return this.flowno;
	}

	public void setFlowno(String flowno) {
		this.flowno = flowno;
	}

	public String getUserno() {
		return " ".equals(this.userno) ? null : this.userno;
	}

	public void setUserno(String userno) {
		this.userno = userno;
	}

	public String getLotno() {
		return " ".equals(this.lotno) ? null : this.lotno;
	}

	public void setLotno(String lotno) {
		this.lotno = lotno;
	}

	public String getLotpwd() {
		return " ".equals(this.lotpwd) ? null : this.lotpwd;
	}

	public void setLotpwd(String lotpwd) {
		this.lotpwd = lotpwd;
	}

	public String getAgencyno() {
		return " ".equals(this.agencyno) ? null : this.agencyno;
	}

	public void setAgencyno(String agencyno) {
		this.agencyno = agencyno;
	}

	public String getBatchcode() {
		return " ".equals(this.batchcode) ? null : this.batchcode;
	}

	public void setBatchcode(String batchcode) {
		this.batchcode = batchcode;
	}

	public String getValidcode() {
		return " ".equals(this.validcode) ? null : this.validcode;
	}

	public void setValidcode(String validcode) {
		this.validcode = validcode;
	}

	public String getRuncode() {
		return " ".equals(this.runcode) ? null : this.runcode;
	}

	public void setRuncode(String runcode) {
		this.runcode = runcode;
	}

	public BigDecimal getBetnum() {
		return this.betnum;
	}

	public void setBetnum(BigDecimal betnum) {
		this.betnum = betnum;
	}

	public BigDecimal getDrawway() {
		return this.drawway;
	}

	public void setDrawway(BigDecimal drawway) {
		this.drawway = drawway;
	}

	public BigDecimal getSellway() {
		return this.sellway;
	}

	public void setSellway(BigDecimal sellway) {
		this.sellway = sellway;
	}

	public String getBetcode() {
		return " ".equals(this.betcode) ? null : this.betcode;
	}

	public void setBetcode(String betcode) {
		this.betcode = betcode;
	}

	public String getCheckcode() {
		return " ".equals(this.checkcode) ? null : this.checkcode;
	}

	public void setCheckcode(String checkcode) {
		this.checkcode = checkcode;
	}

	public BigDecimal getAmt() {
		return this.amt;
	}

	public void setAmt(BigDecimal amt) {
		this.amt = amt;
	}

	public Date getOrdertime() {
		return this.ordertime;
	}

	public void setOrdertime(Date ordertime) {
		this.ordertime = ordertime;
	}

	public BigDecimal getSettleflag() {
		return this.settleflag;
	}

	public void setSettleflag(BigDecimal settleflag) {
		this.settleflag = settleflag;
	}

	public BigDecimal getPrizeamt() {
		return this.prizeamt;
	}

	public void setPrizeamt(BigDecimal prizeamt) {
		this.prizeamt = prizeamt;
	}

	public BigDecimal getPreprizeamt() {
		return preprizeamt;
	}

	public void setPreprizeamt(BigDecimal preprizeamt) {
		this.preprizeamt = preprizeamt;
	}

	public BigDecimal getPrizelittle() {
		return this.prizelittle;
	}

	public void setPrizelittle(BigDecimal prizelittle) {
		this.prizelittle = prizelittle;
	}

	public String getPrizeinfo() {
		return " ".equals(this.prizeinfo) ? null : this.prizeinfo;
	}

	public void setPrizeinfo(String prizeinfo) {
		if (prizeinfo == null) {
			prizeinfo = " ";
		}
		this.prizeinfo = prizeinfo;
	}

	public Date getPrizetime() {
		return this.prizetime;
	}

	public void setPrizetime(Date prizetime) {
		this.prizetime = prizetime;
	}

	public String getMachineno() {
		return " ".equals(this.machineno) ? null : this.machineno;
	}

	public void setMachineno(String machineno) {
		this.machineno = machineno;
	}

	public Date getGiveuptime() {
		return this.giveuptime;
	}

	public void setGiveuptime(Date giveuptime) {
		this.giveuptime = giveuptime;
	}

	public BigDecimal getState() {
		return this.state;
	}

	public void setState(BigDecimal state) {
		this.state = state;
	}

	public BigDecimal getTransferstate() {
		return this.transferstate;
	}

	public void setTransferstate(BigDecimal transferstate) {
		this.transferstate = transferstate;
	}

	public BigDecimal getBettype() {
		return this.bettype;
	}

	public void setBettype(BigDecimal bettype) {
		this.bettype = bettype;
	}

	public String getSubscribeno() {
		return " ".equals(this.subscribeno) ? null : this.subscribeno;
	}

	public void setSubscribeno(String subscribeno) {
		this.subscribeno = subscribeno;
	}

	public String getPbatchcode() {
		return " ".equals(this.pbatchcode) ? null : this.pbatchcode;
	}

	public void setPbatchcode(String pbatchcode) {
		this.pbatchcode = pbatchcode;
	}

	public String getCaseid() {
		return " ".equals(this.caseid) ? null : this.caseid;
	}

	public void setCaseid(String caseid) {
		this.caseid = caseid;
	}

	public String getTorderid() {
		return torderid;
	}

	public void setTorderid(String torderid) {
		this.torderid = torderid;
	}

	public Date getLotcenterordertime() {
		return lotcenterordertime;
	}

	public void setLotcenterordertime(Date lotcenterordertime) {
		this.lotcenterordertime = lotcenterordertime;
	}

	public String getBuyUserno() {
		if (StringUtils.isBlank(buyUserno)) {
			return this.userno;
		}
		return buyUserno;
	}

	public void setBuyUserno(String buyUserno) {
		this.buyUserno = buyUserno;
	}

	public String getMessageid() {
		return messageid;
	}

	public void setMessageid(String messageid) {
		this.messageid = messageid;
	}

	public BigDecimal getReturnvalue() {
		return returnvalue;
	}

	public void setReturnvalue(BigDecimal returnvalue) {
		this.returnvalue = returnvalue;
	}

	public String getEventcode() {
		return eventcode;
	}

	public void setEventcode(String eventcode) {
		this.eventcode = eventcode;
	}

	public BigDecimal getLotcenterstate() {
		return lotcenterstate;
	}

	public void setLotcenterstate(BigDecimal lotcenterstate) {
		this.lotcenterstate = lotcenterstate;
	}

	public BigDecimal getLotcenterisvalid() {
		return lotcenterisvalid;
	}

	public void setLotcenterisvalid(BigDecimal lotcenterisvalid) {
		this.lotcenterisvalid = lotcenterisvalid;
	}

	public String getMaxtermid() {
		return maxtermid;
	}

	public void setMaxtermid(String maxtermid) {
		this.maxtermid = maxtermid;
	}

	public String getPeilu() {
		return peilu;
	}

	public void setPeilu(String peilu) {
		this.peilu = peilu;
	}

	public BigDecimal getRealprizeamt() {
		return realprizeamt;
	}

	public void setRealprizeamt(BigDecimal realprizeamt) {
		this.realprizeamt = realprizeamt;
	}

	public Date getPrinttime() {
		return printtime;
	}

	public void setPrinttime(Date printtime) {
		this.printtime = printtime;
	}

	public Date getLatestprinttime() {
		return latestprinttime;
	}

	public void setLatestprinttime(Date latestprinttime) {
		this.latestprinttime = latestprinttime;
	}

	public String getSaletime() {
		return saletime;
	}

	public void setSaletime(String saletime) {
		this.saletime = saletime;
	}

	public BigDecimal getSendnum() {
		return sendnum;
	}

	public void setSendnum(BigDecimal sendnum) {
		this.sendnum = sendnum;
	}

}
