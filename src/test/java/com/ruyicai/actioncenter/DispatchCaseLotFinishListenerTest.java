package com.ruyicai.actioncenter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.actioncenter.jms.listener.DispatchCaseLotFinishListener;
import com.ruyicai.actioncenter.service.LotteryService;
import com.ruyicai.lottery.domain.CaseLot;
import com.ruyicai.lottery.domain.Torder;
import com.ruyicai.lottery.domain.Tuserinfo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml" })
public class DispatchCaseLotFinishListenerTest {

	@Autowired
	private DispatchCaseLotFinishListener listener;

	@Autowired
	private LotteryService lotteryService;

	@Test
	public void test2chuan1() {
		String caselotid = "C00000000740631";
		CaseLot caseLot = lotteryService.findCaseLotById(caselotid);
		Torder torder = lotteryService.findTorderById(caseLot.getOrderid());
		Tuserinfo userinfo = lotteryService.findTuserinfoByUserno(caseLot.getStarter());
		listener.addPrize2Chuan1(caseLot, torder, userinfo);
	}

	@Test
	public void testlanqiu() {
		String caselotid = "C00000000740631";
		CaseLot caseLot = lotteryService.findCaseLotById(caselotid);
		Torder torder = lotteryService.findTorderById(caseLot.getOrderid());
		Tuserinfo userinfo = lotteryService.findTuserinfoByUserno(caseLot.getStarter());
		listener.addPrizeLanQiu(caseLot, torder, userinfo);
	}
}
