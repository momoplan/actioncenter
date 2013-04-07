package com.ruyicai.actioncenter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.actioncenter.domain.VipUser;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml" })
public class VipUserTest {

	@Test
	public void testFind() {
		System.out.println(VipUser.findIfNotExistsCreate("123456", "2012-05"));
		System.out.println(VipUser.findIfNotExistsCreate("123456", "2012-05"));
	}
}
