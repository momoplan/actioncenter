package com.ruyicai.actioncenter.consts;

import java.math.BigDecimal;


public enum TorderState {

	payType_no(0,"未付款"),
	payType_has(1,"已付款"),
	payType_unduct(2, "已返款"),
	
	orderState_fail(-1, "失败"),
	orderState_wait(0,"等待处理"),
	orderState_ok(1,"已出票"),
	orderState_Null(2,"空订单"),
	orderState_cancel(3, "撤消"),
	orderState_revoked(4, "合买流单"),
	
	prizeState_no(0,"准备进入等待开奖状态"),
	prizeState_wait(1,"等待开奖状态"),
	prizeState_processing(2,"派奖处理中"),
	prizeState_complete(3,"完成派奖"),
	prizeState_tall(4,"中得大奖"),
	
	orderType_bet(0,"普通投注订单"),
	orderType_subscribe(1,"追号订单"),
	orderType_lotcase(2,"合买订单");
	
	private BigDecimal value;
	
	
	private String memo;
	
	private TorderState(int value,String memo){
		this.value = new BigDecimal(value);
		this.memo = memo;
	}
	
	
	public BigDecimal value() {
		return value;
	}
	
	public String memo() {
		return memo;
	}
}
