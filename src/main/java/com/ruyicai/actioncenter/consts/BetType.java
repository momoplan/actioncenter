package com.ruyicai.actioncenter.consts;

import java.math.BigDecimal;

public enum BetType {
	// 投注状态，0：失败，1：成功，2：处理中

	zhuihao(0), taocan(1), touzhu(2),hemai(3),zengsong(4),zengsong_nosms(5);

	BigDecimal state;

	public int intValue() {
		
		return state.intValue();
	}
	
	public BigDecimal value() {
		return state;
	}

	BetType(int val) {
		this.state = new BigDecimal(val);
	}
}
