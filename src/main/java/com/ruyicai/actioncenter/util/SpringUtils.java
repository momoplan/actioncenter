package com.ruyicai.actioncenter.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @ClassName: SpringUtils
 * @Description: spring工具类
 * 
 */
@Service
public class SpringUtils implements ApplicationContextAware {

	private static Logger logger = LoggerFactory.getLogger(SpringUtils.class);

	private static ApplicationContext applicationContext = null;

	@Override
	public void setApplicationContext(ApplicationContext contex) throws BeansException {
		logger.info("==========init spring context start============");
		if (SpringUtils.applicationContext == null) {
			SpringUtils.applicationContext = contex;
			logger.info("==========init spring context successful============");
		}
	}

	/**
	 * 取得存储在静态变量中的ApplicationContext.
	 */
	public static ApplicationContext getApplicationContext() {
		assertContextInjected();
		return applicationContext;
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String name) {
		assertContextInjected();
		return (T) applicationContext.getBean(name);
	}

	/**
	 * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
	 */
	public static <T> T getBean(Class<T> requiredType) {
		assertContextInjected();
		return applicationContext.getBean(requiredType);
	}

	/**
	 * 清除SpringContextHolder中的ApplicationContext为Null.
	 */
	public static void clear() {
		logger.debug("清除SpringUtils中的ApplicationContext:" + applicationContext);
		applicationContext = null;
	}

	/**
	 * 检查ApplicationContext不为空.
	 */
	private static void assertContextInjected() {
		if (applicationContext == null) {
			throw new IllegalStateException("SpringUtils未在系统加载时初始化");
		}
	}

}
