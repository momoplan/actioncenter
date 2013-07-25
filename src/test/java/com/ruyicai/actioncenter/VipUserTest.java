package com.ruyicai.actioncenter;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.actioncenter.dao.VipUserDao;
import com.ruyicai.actioncenter.domain.VipUser;
import com.ruyicai.actioncenter.util.DateUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml" })
public class VipUserTest {

	@Autowired
	private VipUserDao vipUserDao;

	@Test
	public void testFind() {
		String currentMonth = DateUtil.format("yyyy-MM", new Date());
		VipUser user = vipUserDao.findVipUser("00000264", currentMonth, true);
		System.out.println(user);
		if (user == null) {
			System.out.println(vipUserDao.createVipUser("00000264", currentMonth));
		}
	}
}
