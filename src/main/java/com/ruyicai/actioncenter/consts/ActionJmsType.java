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
	Encash_DuoLeCai_AddPrize(11, "重庆11选5加奖"),
	Encash_DaXiaoDanShuang_AddPrize(12, "大小单双加奖"),
	Encash_ZuCai_AddPrize(13, "足彩加奖"),
	Friday_SSQ_ZENGSONG(15,"购买双色球赠送"),
	NEW_USER_REGISTER(16,"新用户注册赠送"),
	Encash_LanQiu_AddPrize(17,"竞彩篮球加奖"),
	Encash_Jingcai_2Chan1(18,"竞彩2串1加奖"),
	Kuai3_JiaJiang(19,"快3加奖"),
	FuCai3D_JiaJiang(20,"3D加奖"),
	AddNum15(21,"追号满15期赠送"),
	Fund2Draw(22,"增加可提现金额"),
	First_Order(23,"广东快乐十分首单赠送"),
	Coupon(24,"兑换券赠送彩金"),
	BeiDan_JiaJiang(26, "北单加奖"),
	World_Cup_BigUser(27,"世界杯大户加奖"),
	YinLian_New_User_Zengsong(28,"银联手机充值新用户赠送"),
	YinLian_Old_User_Zengsong(29,"银联手机充值老用户赠送"),
	Encash_JingCai_AddPrize(30,"竞彩足球全面加奖"),
	Encash_JingCaiDanGuan_AddPrize(31,"竞彩单关固赔加奖"),
	Xingyunsaiche_AddPrize(32,"幸运赛车加奖"),
	XinKuai3_AddPrize(33,"新快三加奖"),
	Shiyiyunduojin_AddPrize(34,"山东11运夺金加奖"),
	Encash_JXSSC_AddPrize(35,"新时时彩加奖"),
	Encash_LanQiuDanGuan_AddPrize(36,"篮球单关固赔加奖");

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
