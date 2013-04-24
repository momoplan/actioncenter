package com.ruyicai.actioncenter;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.collections.map.LinkedMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.actioncenter.consts.ActionJmsType;
import com.ruyicai.actioncenter.domain.Tactivity;
import com.ruyicai.actioncenter.domain.TaddNumActivity;
import com.ruyicai.actioncenter.domain.TuserPrizeDetail;
import com.ruyicai.actioncenter.jms.listener.AddNumSuccessListener;
import com.ruyicai.actioncenter.jms.listener.CaselotBetFullListener;
import com.ruyicai.actioncenter.jms.listener.FundJmsListener;
import com.ruyicai.actioncenter.service.TactionService;
import com.ruyicai.actioncenter.util.JsonUtil;
import com.ruyicai.actioncenter.util.Page;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml" })
public class TactivityTest {

	@Autowired
	CaselotBetFullListener caselotBetFullListener;

	@Autowired
	AddNumSuccessListener addNumSuccessListener;

	@Autowired
	TactionService tactionService;

	@Autowired
	FundJmsListener fundJmsListener;

	@Test
	@SuppressWarnings("unchecked")
	public void testCreateTactivity() {
		Map<String, Object> map = new LinkedMap();
		map.put("step1min", 1000);
		map.put("step1max", 9900);
		map.put("step1present", 4);
		map.put("step2min", 10000);
		map.put("step2max", 49900);
		map.put("step2present", 5);
		map.put("step3", 50000);
		map.put("step3present", 7);
		String express = JsonUtil.toJson(map);
		Tactivity.saveOrUpdate(null, null, "00092493", null, ActionJmsType.CASELOT_SUCCESS.value, express, 1, null);

		Map<String, Object> map2 = new LinkedMap();
		map2.put("prize", 200);
		String express2 = JsonUtil.toJson(map2);
		Tactivity.saveOrUpdate(null, null, "00092493", null, ActionJmsType.AddNumOneYear.value, express2, 1, null);

		Map<String, Object> map5 = new LinkedMap();
		map5.put("step", 500000);
		map5.put("present", 2);
		String express5 = JsonUtil.toJson(map5);
		Tactivity.saveOrUpdate(null, null, "00092493", null, ActionJmsType.VIP_USER_GOUCAI_ZENGSONG.value, express5, 1,
				ActionJmsType.VIP_USER_GOUCAI_ZENGSONG.memo);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreateTactivity2() {
		Map<String, Object> map = new LinkedMap();
		map.put("step1min", 2000);
		map.put("step1max", 4900);
		map.put("step1prize", 300);
		map.put("step2min", 5000);
		map.put("step2max", 9900);
		map.put("step2prize", 900);
		map.put("step3min", 10000);
		map.put("step3max", 19900);
		map.put("step3prize", 1800);
		map.put("step4", 20000);
		map.put("step4prize", 4200);
		String express = JsonUtil.toJson(map);
		Tactivity.saveOrUpdate("T01007", null, "00092493", null, ActionJmsType.EncashAddPrize.value, express, 0, null);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreateTactivity3() {
		Map<String, Object> map = new LinkedMap();
		map.put("step", 10000);
		map.put("present", 5);
		String express = JsonUtil.toJson(map);
		Tactivity.saveOrUpdate(null, null, "00092493", null, ActionJmsType.CHONGZHI_100_ZENGSONG.value, express, 1,
				null);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreateTactivity4() {
		Map<String, Object> map = new LinkedMap();
		map.put("step", 100000);
		map.put("prizeamt", 5000);
		String express = JsonUtil.toJson(map);
		Tactivity.saveOrUpdate(null, "502", "00092493", null, ActionJmsType.Encash_2chuan1_AddPrize.value, express, 1,
				ActionJmsType.Encash_2chuan1_AddPrize.memo);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreateTactivity5() {
		Map<String, Object> map = new LinkedMap();
		map.put("step1min", 10000);
		map.put("step1max", 29900);
		map.put("step1prize", 400);
		map.put("step2min", 30000);
		map.put("step2max", 49900);
		map.put("step2prize", 1500);
		map.put("step3min", 50000);
		map.put("step3max", 99900);
		map.put("step3prize", 3000);
		map.put("step4", 100000);
		map.put("step4prize", 7000);
		String express = JsonUtil.toJson(map);
		Tactivity.saveOrUpdate("T01010", null, "00092493", null, ActionJmsType.Encash_DuoLeCai_AddPrize.value, express,
				1, ActionJmsType.Encash_DuoLeCai_AddPrize.memo);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreateTactivity6() {
		Map<String, Object> map = new LinkedMap();
		map.put("step1min", 2000);
		map.put("step1max", 4900);
		map.put("step1prize", 200);
		map.put("step2min", 5000);
		map.put("step2max", 9900);
		map.put("step2prize", 600);
		map.put("step3min", 10000);
		map.put("step3max", 19900);
		map.put("step3prize", 1200);
		map.put("step4", 20000);
		map.put("step4prize", 2800);
		String express = JsonUtil.toJson(map);
		Tactivity.saveOrUpdate("T01007", "DD", "00092493", null, ActionJmsType.Encash_DaXiaoDanShuang_AddPrize.value,
				express, 1, ActionJmsType.Encash_DaXiaoDanShuang_AddPrize.memo);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreateTactivity7() {
		Map<String, Object> map3 = new LinkedMap();
		map3.put("step", 2000);
		map3.put("regStartTime", "20120820");
		map3.put("regEndTime", "20120830");
		map3.put("prizeamt", 1000);
		String express3 = JsonUtil.toJson(map3);
		Tactivity.saveOrUpdate(null, null, "00092493", null, ActionJmsType.FIRST_CHONGZHI_ZENGSONG.value, express3, 1,
				ActionJmsType.FIRST_CHONGZHI_ZENGSONG.memo);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreateTactivity8() {
		Map<String, Object> map = new LinkedMap();
		map.put("step", 100000);
		map.put("prizeamt", 10000);
		String express = JsonUtil.toJson(map);
		Tactivity.saveOrUpdate("T01003", null, "00092493", null, ActionJmsType.Encash_ZuCai_AddPrize.value, express, 1,
				ActionJmsType.Encash_ZuCai_AddPrize.memo);
		Tactivity.saveOrUpdate("T01004", null, "00092493", null, ActionJmsType.Encash_ZuCai_AddPrize.value, express, 1,
				ActionJmsType.Encash_ZuCai_AddPrize.memo);
		Tactivity.saveOrUpdate("T01005", null, "00092493", null, ActionJmsType.Encash_ZuCai_AddPrize.value, express, 1,
				ActionJmsType.Encash_ZuCai_AddPrize.memo);
		Tactivity.saveOrUpdate("T01006", null, "00092493", null, ActionJmsType.Encash_ZuCai_AddPrize.value, express, 1,
				ActionJmsType.Encash_ZuCai_AddPrize.memo);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreateTactivity9() {
		Map<String, Object> map3 = new LinkedMap();
		map3.put("step", 2000);
		map3.put("prizeamt", 1000);
		String express3 = JsonUtil.toJson(map3);
		Tactivity.saveOrUpdate(null, null, "00092493", "872", ActionJmsType.SUNING_ZENGSONG.value, express3, 1,
				ActionJmsType.SUNING_ZENGSONG.memo);
		Tactivity.saveOrUpdate(null, null, "00092493", "902", ActionJmsType.SUNING_ZENGSONG.value, express3, 1,
				ActionJmsType.SUNING_ZENGSONG.memo);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCreateTactivity10() {
		Map<String, Object> map3 = new LinkedMap();
		map3.put("week", "1,3,5");
		map3.put("step", 3000);
		map3.put("prizeamt", 100);
		String express3 = JsonUtil.toJson(map3);
		Tactivity.saveOrUpdate("F47104", null, "00092493", null, ActionJmsType.Friday_SSQ_ZENGSONG.value, express3, 1,
				ActionJmsType.Friday_SSQ_ZENGSONG.memo);
	}

	@Test
	public void testCreateTactivity11() {
		Map<String, Object> map3 = new LinkedMap();
		map3.put("prizeamt", 300);
		String express3 = JsonUtil.toJson(map3);
		Tactivity.saveOrUpdate(null, null, "00092493", "872", ActionJmsType.SuNing_REGISTER.value, express3, 1,
				ActionJmsType.SuNing_REGISTER.memo);
		Tactivity.saveOrUpdate(null, null, "00092493", "902", ActionJmsType.SuNing_REGISTER.value, express3, 1,
				ActionJmsType.SuNing_REGISTER.memo);
	}

	@Test
	public void testCreateTactivity12() {
		Map<String, Object> map3 = new LinkedMap();
		map3.put("minChargeAmt",10000);
		map3.put("percent", 6);
		map3.put("beforedate", "2013-05-01 00:00:00");
		map3.put("maxamt", 60000);
		String express3 = JsonUtil.toJson(map3);
		Tactivity.saveOrUpdate(null, null, "00092493", null, ActionJmsType.OLD_USER_CHONGZHI_ZENGSONG.value, express3,
				1, ActionJmsType.OLD_USER_CHONGZHI_ZENGSONG.memo);
	}

	@Test
	public void testCreateTactivity13() {
		Map<String, Object> map3 = new LinkedMap();
		map3.put("step1", 20000);
		map3.put("step1prize", 1800);
		map3.put("step2", 50000);
		map3.put("step2prize", 3800);
		map3.put("step3", 200000);
		map3.put("step3prize", 6800);
		map3.put("step4", 500000);
		map3.put("step4prize", 12800);
		map3.put("step5", 1000000);
		map3.put("step5prize", 68800);
		map3.put("step6", 5000000);
		map3.put("step6prize", 388800);
		String express3 = JsonUtil.toJson(map3);
		Tactivity.saveOrUpdate(null, "502", "00092493", null, ActionJmsType.Encash_Jingcai_2Chan1.value, express3, 1,
				ActionJmsType.Encash_Jingcai_2Chan1.memo);
	}

	@Test
	public void testCreateTactivity17() {
		Map<String, Object> map = new LinkedMap();
		map.put("step1min", 2000);
		map.put("step1max", 99900);
		map.put("step1prizeamt", 2000);
		map.put("step", 100000);
		map.put("prizeamt", 5000);
		String express3 = JsonUtil.toJson(map);
		Tactivity.saveOrUpdate("J00005", null, "00092493", null, ActionJmsType.Encash_LanQiu_AddPrize.value, express3,
				1, ActionJmsType.Encash_LanQiu_AddPrize.memo);
		Tactivity.saveOrUpdate("J00006", null, "00092493", null, ActionJmsType.Encash_LanQiu_AddPrize.value, express3,
				1, ActionJmsType.Encash_LanQiu_AddPrize.memo);
		Tactivity.saveOrUpdate("J00007", null, "00092493", null, ActionJmsType.Encash_LanQiu_AddPrize.value, express3,
				1, ActionJmsType.Encash_LanQiu_AddPrize.memo);
		Tactivity.saveOrUpdate("J00008", null, "00092493", null, ActionJmsType.Encash_LanQiu_AddPrize.value, express3,
				1, ActionJmsType.Encash_LanQiu_AddPrize.memo);
	}

	@Test
	public void testCaselotBetFull() throws InterruptedException {
		int nextInt = new Random().nextInt(10000);
		String body = "{\"batchcode\":\"2011148\",\"buyAmtByFollower\":0,\"buyAmtByStarter\":10000,\"caselotinfo\":null,\"commisionRatio\":10,\"content\":null,\"description\":\"\",\"displayState\":2,\"displayStateMemo\":\"满员\",\"endTime\":null,\"full\":true,\"hasachievement\":0,\"id\":\"C"
				+ nextInt
				+ "00000026031\",\"isWinner\":0,\"lotno\":\"F47104\",\"lotsType\":0,\"minAmt\":100,\"orderid\":\"2011121300009030\",\"participantCount\":1,\"safeAmt\":0,\"sortState\":0,\"startTime\":1323763106050,\"starter\":\"00000619\",\"state\":3,\"title\":\"\",\"totalAmt\":10000,\"version\":3,\"visibility\":0,\"winBigAmt\":0,\"winDetail\":null,\"winEndTime\":null,\"winFlag\":null,\"winLittleAmt\":null,\"winPreAmt\":0,\"winStartTime\":null}";
		caselotBetFullListener.encashCustomer(body);
		Thread.sleep(1000 * 10);
	}

	@Test
	public void testAddNumOneYear() throws Exception {
		String userno = "00000619";
		String body = "{\"accountnomoneysms\":0,\"amt\":400,\"batchcode\":\"2011343\",\"betRequests\":[{\"amt\":200,\"betcode\":\"0001060708^\"}],\"bettype\":\"0\",\"buyuserno\":\""
				+ userno
				+ "\",\"caseLotRequest\":null,\"channel\":\"2\",\"desc\":\"单期奖金≥1000元终止追号\",\"drawway\":0,\"endsms\":\"0\",\"leijiprizeendamt\":0,\"lotmulti\":1,\"lotno\":\"F47103\",\"lotsType\":0,\"memo\":\"直选单式\",\"oneamount\":200,\"paytype\":0,\"prizeend\":1,\"prizeendamt\":1000,\"subchannel\":\"00092493\",\"subscribeRequests\":[{\"amt\":200,\"batchcode\":\"2011343\",\"desc\":\"2_1_0\",\"endtime\":1313477552255,\"lotmulti\":1},{\"amt\":200,\"batchcode\":\"2011344\",\"desc\":\"4_1_0\",\"endtime\":1313477552255,\"lotmulti\":1}],\"userno\":\"00000205\"}";
		String flowno = tactionService.addNumOneYear(body);
		System.out.println("flowno=" + flowno);
		addNumSuccessListener.addNumSuccessCustomer(flowno, userno);
	}

	@Test
	public void testTaddNumActivity() {
		TaddNumActivity activity = TaddNumActivity.createTaddNumActivity("123456", "json");
		System.out.println(activity);
		activity.updateTaddNumActivity("123456");
		System.out.println(activity);
		activity.remove();
	}

	@Test
	public void testFundJmsListener() {
		fundJmsListener.fundJmsCustomer(null, 0L, "00000619", 2000L, ActionJmsType.CHONGZHI_SUCCESS.value, "22222222",
				1);
	}

	@Test
	public void testChongZhiJmsListener() {
		fundJmsListener.fundJmsCustomer(null, 0L, "00000193", 500000L, ActionJmsType.GOUCAI_SUCCESS.value,
				"3333333333333333", 3);
	}

	@Test
	public void testfindTuserPrizeDetailByPage() {
		Page<TuserPrizeDetail> page = new Page<TuserPrizeDetail>(0, 20);
		TuserPrizeDetail.findTuserPrizeDetailByPage(new HashMap<String, Object>(), page);
		for (TuserPrizeDetail detail : page.getList()) {
			System.out.println(detail);
		}
	}
}
