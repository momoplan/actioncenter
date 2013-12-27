package com.ruyicai.actioncenter;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.actioncenter.domain.Chong20Mobile;
import com.ruyicai.actioncenter.service.LotteryService;
import com.ruyicai.actioncenter.service.TactionService;
import com.ruyicai.lottery.domain.Tuserinfo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml" })
public class TactionServiceTest {

	@Autowired
	private LotteryService lotteryService;

	@Autowired
	private TactionService tactionService;

	@Test
	public void testChong20Song20() throws InterruptedException {
		Tuserinfo tuserinfo = lotteryService.findTuserinfoByUserno("00001554");
		tactionService.firshChongzhiZengSong20(System.currentTimeMillis() + "", tuserinfo, new BigDecimal(100));
		Thread.sleep(2 * 1000);
		Chong20Mobile mobile = Chong20Mobile.findChong20Mobile("13810747309");
		System.out.println(mobile);
	}
}
