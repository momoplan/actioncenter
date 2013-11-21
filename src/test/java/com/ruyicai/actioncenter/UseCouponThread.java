package com.ruyicai.actioncenter;

import com.ruyicai.actioncenter.util.HttpUtil;

public class UseCouponThread extends Thread {
	
	private String userno;
	private String couponCode;
	
	public UseCouponThread() {}
	
	public UseCouponThread(String userno, String couponCode) {
		this.userno = userno;
		this.couponCode = couponCode;
	}

	@Override
	public void run() {
		super.run();
		try {
			String param = "couponCode=" + couponCode + "&userno=" + userno;
//			String result = HttpUtil.post("http://192.168.0.118:8000/actioncenter/coupon/useCoupon", param);
			for(int i = 0 ; i < 20 ; i++) {
				String result = HttpUtil.post("http://192.168.0.118:8000/actioncenter/sportsquiz/pv", "callBackMethod=myMethod");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
