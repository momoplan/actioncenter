package com.ruyicai.actioncenter.jms.listener;

import org.apache.camel.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ruyicai.actioncenter.service.TactionService;

@Service
public class FundJmsListener {

	@Autowired
	private TactionService actionService;

	public void fundJmsCustomer(@Header("TTRANSACTIONID") String ttransactionid,
			@Header("LADDERPRESENTFLAG") Long ladderpresentflag, @Header("USERNO") String userno,
			@Header("AMT") Long amt, @Header("TYPE") Integer type, @Header("BUSINESSID") String businessId,
			@Header("BUSINESSTYPE") Integer businessType,@Header("BANKID") String bankid) {
		actionService.processFundJMSCustomer(ttransactionid, ladderpresentflag, userno, amt, type, businessId,
				businessType,bankid);
	}

}
