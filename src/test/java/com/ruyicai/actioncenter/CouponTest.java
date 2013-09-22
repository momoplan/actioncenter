package com.ruyicai.actioncenter;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml" })
public class CouponTest {

	@Test
	public void test() {
		for(int i = 0; i < 5; i++) {
			Thread t1 = new UseCouponThread("00001139", "190G97FKTSPH");
			t1.start();
		}
	}

}
