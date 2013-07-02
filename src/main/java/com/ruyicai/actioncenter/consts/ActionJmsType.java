package com.ruyicai.actioncenter.consts;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ActionJmsType {

	SEND_ACTION_PRIZE_SUCCESS(99, "奖励发送成功"),
	CHONGZHI_SUCCESS(1, "充值成功"),
	GOUCAI_SUCCESS(2, "购彩成功"),
	CASELOT_SUCCESS(3, "合买发单返奖"),
	AddNumOneYear(4, "追号包年赠送"),
	EncashAddPrize(5, "时时彩加奖"),
	FIRST_CHONGZHI_ZENGSONG(6, "首次充值赠送"),
	CHONGZHI_100_ZENGSONG(7, "充值赠送"),
	OLD_USER_CHONGZHI_ZENGSONG(8, "老用户充值赠送"),
	VIP_USER_GOUCAI_ZENGSONG(9, "大户购彩赠送"),
	Encash_2chuan1_AddPrize(10, "竞彩足球2串1加奖"),
	Encash_DuoLeCai_AddPrize(11, "广东11选5加奖"),
	Encash_DaXiaoDanShuang_AddPrize(12, "大小单双加奖"),
	Encash_ZuCai_AddPrize(13, "足彩加奖"),
	SUNING_ZENGSONG(14,"苏宁首次充值赠送"),
	Friday_SSQ_ZENGSONG(15,"购买双色球赠送"),
	SuNing_REGISTER(16,"苏宁新用户注册赠送"),
	Encash_LanQiu_AddPrize(17,"竞彩篮球加奖"),
	Encash_Jingcai_2Chan1(18,"竞彩2串1加奖"),
	Kuai3_JiaJiang(19,"快3加奖"),
	FuCai3D_JiaJiang(20,"3D加奖"),
	CashTransaction(21,"充值金额可提现"),
	Fund2Draw(22,"增加可提现金额");

	public int value;

	public String memo;

	public BigDecimal decimal() {
		return new BigDecimal(value);
	}

	private ActionJmsType(int value, String memo) {
		this.value = value;
		this.memo = memo;
	}

	private static final Map<Integer, ActionJmsType> lookup = new HashMap<Integer, ActionJmsType>();

	static {
		for (ActionJmsType s : EnumSet.allOf(ActionJmsType.class)) {
			lookup.put(s.value, s);
		}
	}

	public static ActionJmsType get(Integer value) {
		return lookup.get(value);
	}
}
