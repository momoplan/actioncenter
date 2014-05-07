package com.ruyicai.actioncenter.jms.listener;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.camel.Body;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.consts.ActionJmsType;
import com.ruyicai.actioncenter.dao.TactivityDao;
import com.ruyicai.actioncenter.dao.VipUserDao;
import com.ruyicai.actioncenter.domain.FirstOrder;
import com.ruyicai.actioncenter.domain.Tactivity;
import com.ruyicai.actioncenter.domain.Tjmsservice;
import com.ruyicai.actioncenter.domain.VipUser;
import com.ruyicai.actioncenter.service.LotteryService;
import com.ruyicai.actioncenter.service.SendActivityPrizeJms;
import com.ruyicai.actioncenter.service.UserExperienceService;
import com.ruyicai.actioncenter.util.DateUtil;
import com.ruyicai.actioncenter.util.JsonUtil;
import com.ruyicai.lottery.domain.Torder;
import com.ruyicai.lottery.domain.Tuserinfo;

@Service
public class OrderAfterBetListener {

	private Logger logger = LoggerFactory.getLogger(OrderAfterBetListener.class);

	@Autowired
	private LotteryService lotteryService;

	@Autowired
	private TactivityDao tactivityDao;

	@Autowired
	private VipUserDao vipUserDao;

	@Autowired
	private SendActivityPrizeJms sendActivityPrizeJms;

	@Value("${ruyicaiUserno}")
	private String ruyicaiUserno;

	@Autowired
	private UserExperienceService userExperienceService;

	public void orderAfterBetCustomer(@Body String orderJson) {
		if (StringUtils.isBlank(orderJson)) {
			return;
		}
		logger.info("订单出票成功orderJson:" + orderJson);
		Torder order = JsonUtil.fromJsonToObject(orderJson, Torder.class);
		if (order == null) {
			return;
		}
		String userno = order.getUserno();
		if (userno.equals(ruyicaiUserno)) {
			logger.info("如意彩账户购买,不赠送userno:" + userno);
			return;
		}
		Tuserinfo tuserinfo = lotteryService.findTuserinfoByUserno(userno);
		if (tuserinfo == null) {
			return;
		}
		if (tuserinfo.getChannel() != null && tuserinfo.getChannel().equals("991")) {
			logger.info("如意彩大户渠道不参加活动userno:" + userno);
			return;
		}
		ssqzengsong(order, tuserinfo);
		try {
			firstorder(order, tuserinfo);
		} catch (Exception e) {
			logger.error("广东快乐十分首单活动异常", e);
		}

		// 普通投注 增加用户购彩金额、返点
		this.vipCase(tuserinfo, order.getAmt(), order.getId());
	}

	@Transactional
	public Boolean vipCase(Tuserinfo tuserinfo, BigDecimal amt, String businessId) {
		Boolean flag = false;
		logger.info("VIP购彩活动开始,businessId:" + businessId + " userno:" + tuserinfo.getUserno() + " amt:" + amt);
		try {
			if (!tuserinfo.getSubChannel().equals("00092493")) {
				return flag;
			}
			if (!Tjmsservice.createTjmsservice(businessId, ActionJmsType.VIP_USER_GOUCAI_ZENGSONG)) {
				logger.info("大客户赠送已重复 businessId:" + businessId + " userno:" + tuserinfo.getUserno());
				return false;
			}
			// 增加vip购彩金额
			this.doAddVipUserBuyAmount(tuserinfo, amt);
			// 查找大用户购彩活动并且赠送彩金
			flag = this.doFindActivityAndPresent(tuserinfo, amt, businessId);
			logger.info("VIP购彩活动结束");
		} catch (Exception e) {
			logger.error("VIP购彩活动异常", e);
		}
		return flag;
	}

	/**
	 * 增加本月用户购彩金额
	 * 
	 * @param tuserinfo
	 *            用户
	 * @param amt
	 *            购彩金额
	 */
	@Transactional
	public void doAddVipUserBuyAmount(Tuserinfo tuserinfo, BigDecimal amt) {
		String currentMonth = DateUtil.format("yyyy-MM", new Date());
		if (tuserinfo != null) {
			VipUser vipUser = vipUserDao.findVipUser(tuserinfo.getUserno(), currentMonth, true);
			if (vipUser == null) {
				vipUser = vipUserDao.createVipUser(tuserinfo.getUserno(), currentMonth);
			}
			if (vipUser != null) {
				vipUser.setBuyamt(vipUser.getBuyamt() == null ? amt : vipUser.getBuyamt().add(amt));
				vipUser.setModifyTime(new Date());
				vipUserDao.merge(vipUser);
				logger.info("大客户userno:{},增加购买金额{}", new String[] { tuserinfo.getUserno(), amt + "" });
			}
		}
	}

	/**
	 * 查找大户购彩赠送活动，符合条件的用户赠送彩金
	 * 
	 * @param tuserinfo
	 *            用户
	 * @param amt
	 *            本次购彩金额
	 * @param businessId
	 * @return
	 */
	@Transactional
	public Boolean doFindActivityAndPresent(Tuserinfo tuserinfo, BigDecimal amt, String businessId) {
		Boolean flag = false;
		if (tuserinfo != null) {
			Tactivity tactivity = tactivityDao.findTactivity(null, null, tuserinfo.getSubChannel(), null,
					ActionJmsType.VIP_USER_GOUCAI_ZENGSONG.value);
			if (tactivity != null) {
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer step = (Integer) activity.get("step");
				Integer present = (Integer) activity.get("present");
				Calendar calendar = Calendar.getInstance();

				calendar.add(Calendar.MONTH, -1);
				String lastMonth = DateUtil.format("yyyy-MM", calendar.getTime());
				VipUser lastMonthVipUser = vipUserDao.findVipUser(tuserinfo.getUserno(), lastMonth);
				if (lastMonthVipUser != null && lastMonthVipUser.getBuyamt().compareTo(new BigDecimal(step)) >= 0) {
					if (amt.compareTo(BigDecimal.ZERO) >= 0) {
						flag = true;
						BigDecimal prizeamt = amt.multiply(new BigDecimal(present)).divideToIntegralValue(
								new BigDecimal(100));
						logger.info("大客户userno:{},时间:{},总购彩金额:{},本次购彩:{},赠送金额:{}", new String[] {
								lastMonthVipUser.getId().getUserno(), lastMonthVipUser.getId().getYearAndMonth(),
								lastMonthVipUser.getBuyamt() + "", amt + "", prizeamt + "" });
						sendActivityPrizeJms.sendPrize2UserJMS(tuserinfo.getUserno(), prizeamt,
								ActionJmsType.VIP_USER_GOUCAI_ZENGSONG, tactivity.getMemo(), businessId, "", "");
					}
				} else {
					logger.info("不满大户购彩赠送条件userno:" + tuserinfo.getUserno() + ",vipUser:"
							+ (lastMonthVipUser == null ? "" : lastMonthVipUser));
				}
			}
		}
		return flag;
	}

	@Transactional
	public void firstorder(Torder order, Tuserinfo tuserinfo) {
		Tactivity tactivity = tactivityDao.findTactivity(order.getLotno(), null, tuserinfo.getSubChannel(), null,
				ActionJmsType.First_Order.value);
		if (tactivity != null) {
			Date regtime = tuserinfo.getRegtime();
			String regtimeStr = DateUtil.format("yyyyMMdd", regtime);
			String todayStr = DateUtil.format("yyyyMMdd", new Date());
			if (!regtimeStr.equals(todayStr)) {
				logger.info("非当天注册用户userno:" + tuserinfo.getUserno());
				return;
			}
			if (StringUtils.isBlank(tuserinfo.getMobileid())) {
				logger.info("未完善手机信息用户userno:" + tuserinfo.getUserno());
				return;
			}
			String express = tactivity.getExpress();
			Map<String, Object> activity = JsonUtil.transferJson2Map(express);
			Integer amt = (Integer) activity.get("amt");
			Integer prize = (Integer) activity.get("prize");
			if (amt == null || prize == null) {
				logger.error("参数不正确amt:{},prize:{}", new String[] { amt + "", prize + "" });
				return;
			}
			BigDecimal orderAmt = order.getAmt();
			if (orderAmt.compareTo(new BigDecimal(amt)) >= 0) {
				FirstOrder firstOrder = FirstOrder.findFirstOrder(tuserinfo.getUserno());
				if (firstOrder != null) {
					logger.info(tactivity.getMemo() + "活动已参加过userno:" + tuserinfo.getUserno());
					return;
				} else {
					FirstOrder fo = new FirstOrder();
					fo.setUserno(tuserinfo.getUserno());
					fo.setOrderid(order.getId());
					fo.setCreateTime(new Date());
					fo.persist();
					if (Tjmsservice.createTjmsservice(order.getId(), ActionJmsType.First_Order)) {
						logger.info(ActionJmsType.First_Order.memo + "prize:" + prize.longValue());
						sendActivityPrizeJms.sendPrize2UserJMS(tuserinfo.getUserno(), new BigDecimal(prize),
								ActionJmsType.First_Order, tactivity.getMemo(), order.getId(), "", "");
					}
				}
			}
		}
	}

	public void ssqzengsong(Torder order, Tuserinfo tuserinfo) {
		Tactivity tactivity = tactivityDao.findTactivity(order.getLotno(), null, tuserinfo.getSubChannel(), null,
				ActionJmsType.Friday_SSQ_ZENGSONG.value);
		if (tactivity != null) {
			String express = tactivity.getExpress();
			Map<String, Object> activity = JsonUtil.transferJson2Map(express);
			String week = (String) activity.get("week");
			Integer step = (Integer) activity.get("step");
			Integer prizeamt = (Integer) activity.get("prizeamt");
			if (week == null || step == null || prizeamt == null) {
				logger.error("参数不正确week:{},step:{},prizeamt:{}", new String[] { week + "", step + "", prizeamt + "" });
				return;
			}
			Calendar cal = Calendar.getInstance();
			String cw = cal.get(Calendar.DAY_OF_WEEK) - 1 + "";
			if (week.indexOf(cw) != -1) {
				Long amt = order.getAmt().longValue();
				if (amt >= step) {
					BigDecimal prize = new BigDecimal(prizeamt);
					if (prize.compareTo(BigDecimal.ZERO) > 0) {
						if (Tjmsservice.createTjmsservice(order.getId(), ActionJmsType.Friday_SSQ_ZENGSONG)) {
							logger.info(ActionJmsType.Friday_SSQ_ZENGSONG.memo + "prize:" + prize.longValue());
							sendActivityPrizeJms.sendPrize2UserJMS(tuserinfo.getUserno(), prize,
									ActionJmsType.Friday_SSQ_ZENGSONG, tactivity.getMemo(), order.getId(), "", "");
						}
					}
				}
			}
		}
	}

	@Transactional
	public void addUserExperienceVoteTime(String userno, BigDecimal amt) {
		userExperienceService.addAvailableVoteTimesByBuyAMT(userno, amt.divide(new BigDecimal(200)).intValue());
	}
}
