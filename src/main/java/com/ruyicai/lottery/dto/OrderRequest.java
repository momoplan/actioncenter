package com.ruyicai.lottery.dto;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import com.ruyicai.actioncenter.consts.BetType;
import com.ruyicai.actioncenter.consts.SubaccountType;
import com.ruyicai.actioncenter.consts.TorderState;

@RooToString
@RooJavaBean
public class OrderRequest {

	String batchcode;
	String lotno;
	BigDecimal amt;
	BigDecimal bettype = BetType.touzhu.value();
	String userno;
	BigDecimal lotmulti;
	String buyuserno;
	String channel = "1";
	String subchannel = "00092493";
	SubaccountType subaccount;
	BigDecimal paytype = TorderState.payType_no.value();
	BigDecimal oneamount = new BigDecimal(200);

	String memo;
	String desc;

	List<BetRequest> betRequests;

	BigDecimal prizeend = BigDecimal.ZERO;
	BigDecimal prizeendamt = BigDecimal.ZERO;
	BigDecimal leijiprizeendamt = BigDecimal.ZERO;
	BigDecimal accountnomoneysms = BigDecimal.ZERO;
	List<SubscribeRequest> subscribeRequests;
	CaseLotRequest caseLotRequest;
	/** 投注方式，（0-单式，1-复式，2-胆拖，3-单式上传），追号时使用 */
	BigDecimal drawway;
	/** 订单类型 0-单式上传，1-复式，2-胆拖，3-多方案 */
	BigDecimal lotsType;

	BigDecimal deduct = BigDecimal.ZERO;
	
	String agencyno;
	
	BigDecimal endsms;
	
	BigDecimal cancancel;
	
	String Blessing;

	public BigDecimal getDrawway() {
		return drawway;
	}

	public void setDrawway(BigDecimal drawway) {
		this.drawway = drawway;
	}
}
