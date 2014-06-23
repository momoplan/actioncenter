package com.ruyicai.actioncenter.consts;

public enum Fund2DrawState {

	waitToDraw(0, "等待转可提现"), haveDraw(1, "已转可提现"), haveJoinAction(2, "参与活动不转可提现"),hasError(3,"提现失败");

	private Integer state;

	private String memo;

	private Fund2DrawState(Integer state, String memo) {
		this.state = state;
	}

	public Integer value() {
		return state;
	}

	public String memo() {
		return memo;
	}
}
