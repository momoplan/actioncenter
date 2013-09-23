package com.ruyicai.actioncenter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ruyicai.actioncenter.util.HttpUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/META-INF/spring/applicationContext.xml",
		"classpath:/META-INF/spring/applicationContext-jms.xml",
		"classpath:/META-INF/spring/applicationContext-memcache.xml" })
public class UserExperienceTest {

	@Test
	public void test() {
		try {
			for(int i = 0; i < 5; i++) {
				HttpUtil.post("http://192.168.0.118:8000/actioncenter/userexperience/addAvailableVoteTimes", "userno=00000042&times=1");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
