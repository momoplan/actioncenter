package com.ruyicai.actioncenter;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.actioncenter.jms.listener.CaselotBetFullListener;
import com.ruyicai.actioncenter.service.LotteryService;
import com.ruyicai.lottery.domain.CaseLot;
import com.ruyicai.lottery.domain.Tuserinfo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml" })
public class LotteryServiceTest {

	@Autowired
	private LotteryService lotteryService;

	@Autowired
	private CaselotBetFullListener caselotBetFullListener;

	@Test
	public void testDirectChargeProcess() {
		Tuserinfo tuserinfo = lotteryService.findTuserinfoByUserno("00000717");
		System.out.println(tuserinfo);
		Boolean flag = lotteryService.directChargeProcess(tuserinfo.getUserno(), new BigDecimal(100000),
				tuserinfo.getSubChannel(), tuserinfo.getChannel(), null, "", "");
		Assert.assertEquals(flag, true);
	}

	@Test
	public void testFindTtransactionCount() {
		BigDecimal sum = lotteryService.findTtransactionSum("00000619", "2011-11-11 11:11:11");
		System.out.println("sum=" + sum);
	}

	@Test
	public void testAddDraw() {
		lotteryService.addDrawAmount("00000619", "20111221000000000000000000029217", new BigDecimal(10000));
	}

	@Test
	public void testFindTransaction() {
		lotteryService.deductDrawBalance("00000444", "TE201303110000000000000000320019");
	}

	@Test
	public void testFindCaselot() {
		CaseLot caseLot = lotteryService.findCaseLotById("C00000000256213");
		System.out.println(caseLot);
	}

	@Test
	public void testBetFull() {
		String json = "{\"batchcode\":\"2012240\",\"buyAmtByFollower\":54000,\"buyAmtByStarter\":2000,\"caselotinfo\":null,\"commisionRatio\":10,\"content\":null,\"description\":\"昨晚就差一个数，希望今天可以中，20元的投注会有几倍的回报，大家谨慎投注！\",\"displayState\":2,\"displayStateMemo\":\"满员\",\"endTime\":null,\"full\":true,\"hasachievement\":0,\"id\":\"C00000005838929\",\"isWinner\":0,\"lotno\":\"F47103\",\"lotsType\":1,\"minAmt\":2000,\"orderid\":\"BJ2012090303906675\",\"participantCount\":25,\"playtype\":\"32\",\"safeAmt\":0,\"sortState\":0,\"startTime\":1346628259435,\"starter\":\"00000042\",\"state\":3,\"title\":null,\"totalAmt\":56000,\"visibility\":1,\"winBigAmt\":0,\"winDetail\":null,\"winEndTime\":null,\"winFlag\":null,\"winLittleAmt\":null,\"winPreAmt\":0,\"winStartTime\":null}";
		caselotBetFullListener.encashCustomer(json);
	}

	@Test
	public void testfindCaseLotBuyAllPrizeamtById() {
		Integer prize = lotteryService.findCaseLotBuyAllPrizeamtById("C00000000584537", "00000138");
		System.out.println(prize);
	}

	@Test
	public void testSelectMinAmt() {
		BigDecimal minAmt = lotteryService.selectMinAmtBySubscribeno("0000000002408095");
		System.out.println(minAmt);
	}
}
