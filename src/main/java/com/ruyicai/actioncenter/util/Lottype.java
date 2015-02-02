package com.ruyicai.actioncenter.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class Lottype {

	private static Map<String, String> map = new LinkedHashMap<String, String>();

	static {
		map.put("B00001", "北单胜平负");
		map.put("B00002", "北单总进球");
		map.put("B00003", "北单半全场");
		map.put("B00004", "北单上下单双");
		map.put("B00005", "北单比分");
		map.put("F47102", "七乐彩");
		map.put("F47103", "3D");
		map.put("F47104", "双色球");
		map.put("F47107", "内蒙快三");	// 快三
		map.put("F47108", "吉林快三");	// 新快三
		map.put("F47109", "重庆快乐十分");
		map.put("J00001", "竞彩足球胜平负");
		map.put("J00002", "竞彩足球比分");
		map.put("J00003", "竞彩足球总进球");
		map.put("J00004", "竞彩足球半场胜平负");
		map.put("J00005", "竞彩篮球胜负");
		map.put("J00006", "竞彩篮球让分胜负");
		map.put("J00007", "竞彩篮球胜分差");
		map.put("J00008", "竞彩篮球大小分");
		map.put("J00009", "冠军");
		map.put("J00010", "冠亚军");
		map.put("J00011", "竞彩足球混合");
		map.put("J00012", "竞彩篮球混合");
		map.put("J00013", "竞彩足球让球胜负平");
		map.put("T01001", "超级大乐透");
		map.put("T01002", "排列三");
		map.put("T01003", "胜负彩");
		map.put("T01004", "任九场");
		map.put("T01005", "进球彩");
		map.put("T01006", "半全场");
		map.put("T01007", "时时彩");
		map.put("T01008", "单场");
		map.put("T01009", "七星彩");
		map.put("T01010", "多乐彩(十一选五)");
		map.put("T01011", "排列五");
		map.put("T01012", "十一运夺金");
		map.put("T01013", "22选5");
		map.put("T01014", "广东十一选五");
		map.put("T01015", "广东快乐十分");
		map.put("T01016", "重庆十一选五");
		map.put("T01018", "幸运赛车");
		map.put("T01019", "江西时时彩");//新时时彩
		map.put("T01020", "快乐扑克3");
	}

	public static Map<String, String> getMap() {
		return map;
	}

	public static String getName(String lotno) {
		return map.get(lotno);
	}
}
