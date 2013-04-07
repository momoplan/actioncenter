package com.ruyicai.actioncenter.consts;

import javax.annotation.PostConstruct;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoutesConfiguration {

	private Logger logger = LoggerFactory.getLogger(RoutesConfiguration.class);

	@Autowired
	private CamelContext camelContext;

	@PostConstruct
	public void init() throws Exception {
		logger.info("init camel routes");
		camelContext.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				deadLetterChannel("jms:queue:dead").maximumRedeliveries(-1);
				from("jms:queue:VirtualTopicConsumers.actioncenter.sendActivityPrize?concurrentConsumers=10").to(
						"bean:sendActivityPrizeListener?method=sendActivityPrizeCustomer")
						.routeId("actioncenter发放活动奖金");
				from("jms:queue:VirtualTopicConsumers.actioncenter.actioncenter?concurrentConsumers=20").to(
						"bean:fundJmsListener?method=fundJmsCustomer").routeId("actioncenter用户充值或购彩");
				from("jms:queue:VirtualTopicConsumers.actioncenter.caselotBetFull").to(
						"bean:caselotBetFullListener?method=encashCustomer").routeId("actioncenter合买成功");
				from("jms:queue:VirtualTopicConsumers.actioncenter.addnumsuccess?concurrentConsumers=20").to(
						"bean:addNumSuccessListener?method=addNumSuccessCustomer").routeId("actioncenter追号成功");
				from("jms:queue:VirtualTopicConsumers.actioncenter.orderPirzeend?concurrentConsumers=20").to(
						"bean:orderEncashListener?method=orderEncashCustomer").routeId("actioncenter派奖加奖");
				from("jms:queue:VirtualTopicConsumers.actioncenter.dispatchCaseLotFinish?concurrentConsumers=20").to(
						"bean:dispatchCaseLotFinishListener?method=dispatchCaseLotFinishCustomer").routeId(
						"actioncenter合买派奖完成");
				from("jms:queue:VirtualTopicConsumers.actioncenter.orderAfterBetTopic?concurrentConsumers=20").to(
						"bean:orderAfterBetListener?method=orderAfterBetCustomer").routeId("actioncenter订单投注成功");
				from("jms:queue:VirtualTopicConsumers.actioncenter.userCreated?concurrentConsumers=10").to(
						"bean:suningRegisterListener?method=userCreatedCustomer").routeId("actioncenter用户注册活动监听");
				from("jms:queue:VirtualTopicConsumers.actioncenter.userModify?concurrentConsumers=10").to(
						"bean:suningRegisterListener?method=userModifyCustomer").routeId("actioncenter用户修改活动监听");
			}
		});
	}
}
