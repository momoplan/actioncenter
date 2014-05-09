package com.ruyicai.actioncenter.jms.listener;

import org.apache.camel.Body;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CaselotFinishListener {

	private Logger logger = LoggerFactory.getLogger(CaselotFinishListener.class);

	public void caselotFinishCustomer(@Body String caseLotJson) {
		logger.info("合买结期 caseLotJson:" + caseLotJson);
	}

}
