package com.ruyicai.actioncenter.service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.consts.ActionJmsType;
import com.ruyicai.actioncenter.dao.Fund2DrawDao;
import com.ruyicai.actioncenter.dao.TactivityDao;
import com.ruyicai.actioncenter.dao.VipUserDao;
import com.ruyicai.actioncenter.domain.Chong20Mobile;
import com.ruyicai.actioncenter.domain.FirstChargeUser;
import com.ruyicai.actioncenter.domain.Fund2Draw;
import com.ruyicai.actioncenter.domain.SendMoneyDetails;
import com.ruyicai.actioncenter.domain.Tactivity;
import com.ruyicai.actioncenter.domain.TaddNumActivity;
import com.ruyicai.actioncenter.domain.Tagent;
import com.ruyicai.actioncenter.domain.Tjmsservice;
import com.ruyicai.actioncenter.domain.VipUser;
import com.ruyicai.actioncenter.domain.YinLianNewUser;
import com.ruyicai.actioncenter.domain.YinLianOldUser;
import com.ruyicai.actioncenter.exception.RuyicaiException;
import com.ruyicai.actioncenter.util.DateUtil;
import com.ruyicai.actioncenter.util.ErrorCode;
import com.ruyicai.actioncenter.util.JsonUtil;
import com.ruyicai.lottery.domain.Tuserinfo;
import com.ruyicai.lottery.dto.OrderRequest;

@Service
public class TactionService {

	private Logger logger = LoggerFactory.getLogger(TactionService.class);

	@Autowired
	private LotteryService lotteryService;

	@Autowired
	private TactivityDao tactivityDao;

	@Autowired
	private SendActivityPrizeJms sendActivityPrizeJms;

	@Autowired
	private VipUserDao vipUserDao;

	@Value("${addNumOneYearSwitch}")
	private String addNumOneYearSwitch;

	@Autowired
	private Fund2DrawDao fund2DrawDao;

	public void processFundJMSCustomer(String ttransactionid, Long ladderpresentflag, String userno, Long amtLong,
			Integer actionJmsType, String businessId, Integer businessType, String bankid) {
		logger.info(
				"processFundJMSCustomer  userno:{},amt:{},actionJmsType:{},ttransactionid:{},ladderpresentflag:{},businessId:{},businessType:{}",
				new String[] { userno, amtLong.toString(), actionJmsType + "", ttransactionid, ladderpresentflag + "",
						businessId, businessType + "" });
		if (userno == null || amtLong == null || actionJmsType == null) {
			return;
		}
		BigDecimal amt = new BigDecimal(amtLong);
		try {
			if (actionJmsType == ActionJmsType.CHONGZHI_SUCCESS.value) {
				/** 充值可提现功能 */
				if (bankid != null) {
					if (bankid.equalsIgnoreCase("gyj001") || bankid.equalsIgnoreCase("szf001")
							|| bankid.equalsIgnoreCase("ump002")) {
						logger.info("不可提现充值userno:{},ttransactionid:{},bankid:{}", new String[] { userno,
								ttransactionid, bankid });
					} else {
						save2cashTransaction(ttransactionid, userno, amt);
					}
				} else {
					save2cashTransaction(ttransactionid, userno, amt);
				}
			}
		} catch (Exception e) {
			logger.error("充值可提现功能异常 ttransactionid:" + ttransactionid + ",userno:" + userno + ",amt:" + amtLong
					+ ",actionJmsType:" + actionJmsType, e);
		}
		Tuserinfo tuserinfo = lotteryService.findTuserinfoByUserno(userno);
		if (tuserinfo == null) {
			return;
		}
		if (tuserinfo.getChannel() != null && tuserinfo.getChannel().equals("991")) {
			logger.info("如意彩大户渠道不参加活动userno:" + userno);
			return;
		}
		try {
			if (actionJmsType == ActionJmsType.CHONGZHI_SUCCESS.value) {
				/** 充值满百送5%活动 */
				chongzhiManBaiZengSong(ttransactionid, ladderpresentflag, tuserinfo, amt);
				/** 第一次充值活动 */
				firshChongzhiZengSong(ttransactionid, tuserinfo, amt, bankid);
				yinlianOldUser(ttransactionid, tuserinfo, amt, bankid);
			}
			if (actionJmsType == ActionJmsType.GOUCAI_SUCCESS.value) {
				logger.info("购彩成功事件");
				vipCase(tuserinfo, amt, businessId);
			}
		} catch (Exception e) {
			logger.error("活动异常  userno:" + userno + ",amt:" + amtLong + ",actionJmsType:" + actionJmsType, e);
		}
	}

	@Transactional
	public Boolean yinlianOldUser(String ttransactionid, Tuserinfo tuserinfo, BigDecimal amt, String bankid) {
		Boolean flag = false;
		if (tuserinfo == null) {
			return flag;
		}
		Tactivity tactivity = tactivityDao.findTactivity(null, null, tuserinfo.getSubChannel(), null,
				ActionJmsType.YinLian_Old_User_Zengsong.value);
		if (tactivity == null) {
			logger.info("老用户充值赠送活动未开启");
			return flag;
		}
		String userno = tuserinfo.getUserno();
		if (bankid == null) {
			logger.info("无bankid. userno:{},ttransactionid:{}", new String[] { userno, ttransactionid });
			return flag;
		}
		if (bankid.equalsIgnoreCase("lhj001") || bankid.equalsIgnoreCase("upmp01")) {
			YinLianOldUser yinLianOldUser = YinLianOldUser.findYinLianOldUser(userno);
			if (yinLianOldUser != null) {
				logger.info("活动已参加userno:" + userno);
				return flag;
			}
			String express = tactivity.getExpress();
			Map<String, Object> activity = JsonUtil.transferJson2Map(express);
			String dateStr = (String) activity.get("beforedate");
			Date date = DateUtil.parse(dateStr);
			Date regtime = tuserinfo.getRegtime();
			if (date.compareTo(regtime) > 0) {
				long chargeAmt = amt.longValue();
				BigDecimal prize = BigDecimal.ZERO;
				Integer step1 = (Integer) activity.get("step1");
				Integer step1prize = (Integer) activity.get("step1prize");
				Integer step2 = (Integer) activity.get("step2");
				Integer step2prize = (Integer) activity.get("step2prize");
				Integer step3 = (Integer) activity.get("step3");
				Integer step3prize = (Integer) activity.get("step3prize");
				Integer step4 = (Integer) activity.get("step4");
				Integer step4prize = (Integer) activity.get("step4prize");
				if (chargeAmt >= step1 && chargeAmt < step2) {
					prize = new BigDecimal(step1prize);
				} else if (chargeAmt >= step2 && chargeAmt < step3) {
					prize = new BigDecimal(step2prize);
				} else if (chargeAmt >= step3 && chargeAmt < step4) {
					prize = new BigDecimal(step3prize);
				} else if (chargeAmt >= step4) {
					prize = new BigDecimal(step4prize);
				}
				if (prize.compareTo(BigDecimal.ZERO) > 0) {
					if (Tjmsservice.createTjmsservice(ttransactionid, ActionJmsType.Encash_Jingcai_2Chan1)) {
						logger.info(tactivity.getMemo() + prize);
						YinLianOldUser oldUser = new YinLianOldUser();
						oldUser.setUserno(userno);
						oldUser.setAmt(amt);
						oldUser.setCreateTime(new Date());
						oldUser.persist();
						sendActivityPrizeJms.sendPrize2UserJMS(tuserinfo.getUserno(), prize,
								ActionJmsType.YinLian_Old_User_Zengsong, tactivity.getMemo(), ttransactionid, "",
								ttransactionid);
						flag = true;
					}
				}
			} else {
				logger.info("用户注册时间" + regtime + "不在" + dateStr + "之前");
			}
			return flag;
		} else {
			logger.info("不是银联手机支付userno:{},ttransactionid:{},bankid:{}",
					new String[] { userno, ttransactionid, bankid });
			return flag;
		}
	}

	@Transactional
	private Boolean save2cashTransaction(String ttransactionid, String userno, BigDecimal amt) {
		logger.info("保存充值等待可提现ttransactionid:{},userno:{},amt:{}", new String[] { ttransactionid, userno, amt + "" });
		Boolean flag = false;
		Tactivity tactivity = tactivityDao.findTactivity(null, null, "00092493", null, ActionJmsType.Fund2Draw.value);
		if (tactivity != null) {
			Fund2Draw draw = fund2DrawDao.createFund2Draw(ttransactionid, userno, amt);
			if (draw != null) {
				flag = true;
			}
		} else {
			logger.info("未开启");
		}
		return flag;
	}

	@Transactional
	public Boolean chongzhiManBaiZengSong(String ttransactionid, Long ladderpresentflag, Tuserinfo tuserinfo,
			BigDecimal amt) {
		logger.info("充值满百开始");
		Boolean flag = false;
		if (tuserinfo != null) {
			Tactivity tactivity = tactivityDao.findTactivity(null, null, tuserinfo.getSubChannel(), null,
					ActionJmsType.CHONGZHI_100_ZENGSONG.value);
			if (tactivity != null) {
				// if (ladderpresentflag != null && 1 == ladderpresentflag) {
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer step = (Integer) activity.get("step");
				Integer present = (Integer) activity.get("present");
				if (amt.compareTo(new BigDecimal(step)) >= 0) {
					flag = true;
					BigDecimal prizeamt = amt.multiply(new BigDecimal(present)).divideToIntegralValue(
							new BigDecimal(100));
					logger.info("充值满百赠送,userno:{},amt:{},prizeamt:{}", new String[] { tuserinfo.getUserno(), amt + "",
							prizeamt + "" });
					sendActivityPrizeJms.sendPrize2UserJMS(tuserinfo.getUserno(), prizeamt,
							ActionJmsType.CHONGZHI_100_ZENGSONG, tactivity.getMemo(), ttransactionid, "",
							ttransactionid);
				} else {
					logger.info("不满足充值满百赠送条件,amt:" + amt + ",userno:" + tuserinfo.getUserno());
				}
			} else {
				logger.info("充值满百赠送活动未开启");
			}
		}
		logger.info("充值满百结束");
		return flag;
	}

	@Transactional
	public Boolean firshChongzhiZengSong(String ttransactionid, Tuserinfo tuserinfo, BigDecimal amt, String bankid) {
		logger.info("第一次充值开始");
		Boolean flag = false;
		if (tuserinfo.getSubChannel().equals("984") || tuserinfo.getSubChannel().equals("985")) {
			logger.info("U付如意彩用户不参与活动,user:" + tuserinfo.getUserno());
			return flag;
		}
		if (tuserinfo != null) {
			Tactivity yinliantactivity = tactivityDao.findTactivity(null, null, tuserinfo.getSubChannel(), null,
					ActionJmsType.YinLian_New_User_Zengsong.value);
			if (yinliantactivity != null) {
				if (bankid != null && (bankid.equalsIgnoreCase("lhj001") || bankid.equalsIgnoreCase("upmp01"))) {
					String express = yinliantactivity.getExpress();
					Map<String, Object> activity = JsonUtil.transferJson2Map(express);
					Date regtime = tuserinfo.getRegtime();
					String regtimeStr = DateUtil.format("yyyyMMdd", regtime);
					String todayStr = DateUtil.format("yyyyMMdd", new Date());
					Integer step = (Integer) activity.get("step");
					Integer prizeamt = (Integer) activity.get("prizeamt");
					if (amt.compareTo(new BigDecimal(step)) >= 0 && regtimeStr.equals(todayStr)) {
						Integer count = lotteryService.findTtransaction(tuserinfo.getUserno());
						if (count == 1 || count == null) {
							YinLianNewUser newUser = YinLianNewUser.findYinLianNewUser(tuserinfo.getUserno());
							if (newUser == null) {
								if (StringUtils.isNotBlank(tuserinfo.getMobileid())) {
									Chong20Mobile chong20Mobile = Chong20Mobile.findChong20Mobile(tuserinfo
											.getMobileid());
									if (chong20Mobile == null) {
										logger.info("银联第一次充值赠送,userno:{},amt:{},prizeamt:{}",
												new String[] { tuserinfo.getUserno(), amt + "", prizeamt + "" });
										Chong20Mobile.createChong20Mobile(tuserinfo.getMobileid(),
												tuserinfo.getUserno());
										newUser = new YinLianNewUser();
										newUser.setUserno(tuserinfo.getUserno());
										newUser.setAmt(amt);
										newUser.setCreateTime(new Date());
										newUser.persist();
										sendActivityPrizeJms.sendPrize2UserJMS(tuserinfo.getUserno(), new BigDecimal(
												prizeamt), ActionJmsType.YinLian_New_User_Zengsong, yinliantactivity
												.getMemo(), ttransactionid, "", ttransactionid);
										flag = true;
									} else {
										logger.info("银联第一次充值活动,用户手机号已赠送过.mobileid:{}amt:{},user:{}", new String[] {
												tuserinfo.getMobileid(), amt + "", tuserinfo.toString() });
										return flag;
									}
								} else {
									logger.info("银联第一次充值活动,用户信息未完善.amt:{},user:{}",
											new String[] { amt + "", tuserinfo.toString() });
								}
							} else {
								logger.info("银联第一次充值活动已参加过.userno:{},amt:{},count:{}",
										new String[] { tuserinfo.getUserno(), amt + "" });
								return flag;
							}
						} else {
							logger.info("银联不是第一次充值，不参加活动.userno:{},amt:{},count:{}",
									new String[] { tuserinfo.getUserno(), amt + "", count + "" });
							return flag;
						}
					} else {
						logger.info("银联第一次充值，不满足条件.userno:{},amt:{},sep:{},regtime,{}",
								new String[] { tuserinfo.getUserno(), amt + "", step + "", regtimeStr });
					}
				} else {
					logger.info("不是银联手机支付userno:{},ttransactionid:{},bankid:{}", new String[] { tuserinfo.getUserno(),
							ttransactionid, bankid });
				}
			}
			if (flag == false) {
				Tactivity tactivity = tactivityDao.findTactivity(null, null, tuserinfo.getSubChannel(), null,
						ActionJmsType.FIRST_CHONGZHI_ZENGSONG.value);
				if (tactivity != null) {
					String express = tactivity.getExpress();
					Map<String, Object> activity = JsonUtil.transferJson2Map(express);
					Date regtime = tuserinfo.getRegtime();
					String regtimeStr = DateUtil.format("yyyyMMdd", regtime);
					String todayStr = DateUtil.format("yyyyMMdd", new Date());
					Integer step = (Integer) activity.get("step");
					Integer prizeamt = (Integer) activity.get("prizeamt");
					if (amt.compareTo(new BigDecimal(step)) >= 0 && regtimeStr.equals(todayStr)) {
						Integer count = lotteryService.findTtransaction(tuserinfo.getUserno());
						if (count == 1 || count == null) {
							if (StringUtils.isNotBlank(tuserinfo.getMobileid())) {
								Chong20Mobile chong20Mobile = Chong20Mobile.findChong20Mobile(tuserinfo.getMobileid());
								if (chong20Mobile == null) {
									logger.info("第一次充值赠送,userno:{},amt:{},prizeamt:{}",
											new String[] { tuserinfo.getUserno(), amt + "", prizeamt + "" });
									Chong20Mobile.createChong20Mobile(tuserinfo.getMobileid(), tuserinfo.getUserno());
									sendActivityPrizeJms.sendPrize2UserJMS(tuserinfo.getUserno(), new BigDecimal(
											prizeamt), ActionJmsType.FIRST_CHONGZHI_ZENGSONG, tactivity.getMemo(),
											ttransactionid, "", ttransactionid);
									flag = true;
								} else {
									logger.info("第一次充值活动,用户手机号已赠送过.mobileid:{}amt:{},user:{}",
											new String[] { tuserinfo.getMobileid(), amt + "", tuserinfo.toString() });
								}
							} else {
								logger.info("第一次充值活动,用户信息未完善.amt:{},user:{}",
										new String[] { amt + "", tuserinfo.toString() });
								FirstChargeUser fcu = FirstChargeUser.findFirstChargeUser(tuserinfo.getUserno());
								if (fcu == null) {
									logger.info("创建FirstChargeUser等待用户完善信息userno:" + tuserinfo.getUserno()
											+ ",ttransactionid:" + ttransactionid);
									FirstChargeUser.createFirstChargeUser(tuserinfo.getUserno(), 0, ttransactionid);
								} else {
									logger.info("等待用户完善信息已创建userno:" + tuserinfo.getUserno() + ",ttransactionid:"
											+ ttransactionid);
								}
							}
						} else {
							logger.info("不是第一次充值，不参加活动.userno:{},amt:{},count:{}", new String[] {
									tuserinfo.getUserno(), amt + "", count + "" });
						}
					} else {
						logger.info("第一次充值，不满足条件.userno:{},amt:{},sep:{},regtime,{}",
								new String[] { tuserinfo.getUserno(), amt + "", step + "", regtimeStr });
					}
				} else {
					logger.info("首次充值赠送活动未开启");
				}
			} else {
				logger.info("已参加银联手机活动userno:" + tuserinfo.getUserno());
			}
		}
		logger.info("第一次充值结束");
		return flag;
	}

	@Transactional
	public Boolean chargeCase(Tagent tagent, BigDecimal amt) {
		Boolean flag = false;
		Tuserinfo tuserinfo = lotteryService.findTuserinfoByUserno(tagent.getUserno());
		Tactivity tactivity = tactivityDao.findTactivity(null, null, tuserinfo.getSubChannel(), null,
				ActionJmsType.CHONGZHI_SUCCESS.value);
		if (tactivity != null) {
			Tagent parentAgent = Tagent.findTagent(tagent.getAgentId());
			String express = tactivity.getExpress();
			Map<String, Object> activity = JsonUtil.transferJson2Map(express);
			Integer borderamt = (Integer) activity.get("borderamt");
			Integer ltPrizeRate = (Integer) activity.get("ltPrizeRate");
			Integer gtPrizeRate = (Integer) activity.get("gtPrizeRate");
			BigDecimal prize = BigDecimal.ZERO;
			if (amt.compareTo(new BigDecimal(borderamt)) < 0) {
				prize = amt.multiply(new BigDecimal(ltPrizeRate)).divide(new BigDecimal(100));
			} else {
				prize = amt.multiply(new BigDecimal(gtPrizeRate)).divide(new BigDecimal(100));
			}
			logger.info("下线userno:{}充值{}，赠送上线userno:{}彩金{}",
					new String[] { tagent.getUserno(), amt + "", parentAgent.getUserno(), prize.toString() });
			flag = true;
			sendActivityPrizeJms.sendPrize2UserJMS(parentAgent.getUserno(), prize, ActionJmsType.CHONGZHI_SUCCESS,
					tactivity.getMemo(), "", "", "");
		}
		return flag;
	}

	/**
	 * 用户注册时注册代理用户
	 * 
	 * @param userno
	 *            注册用户编号
	 * @param mobileid
	 *            上级代理手机号
	 * @return
	 */
	@Transactional
	public void registerAgent(String userno, String mobileid) {
		if (StringUtils.isBlank(userno) || StringUtils.isBlank(mobileid)) {
			throw new IllegalArgumentException("the arguments userno or mobileid is required");
		}
		Tagent agent = Tagent.findTagentByUserno(userno);
		if (agent != null && agent.getAgentId() != null) {
			logger.info("注册代理失败。该用户已经被代理userno:" + userno);
			throw new RuyicaiException("注册代理失败,该用户已经被代理.");
		}
		Tuserinfo parentAgentUserinfo = lotteryService.findTuserinfoByMobileid(mobileid);
		if (parentAgentUserinfo == null) {
			logger.info("注册代理失败。上级代理用户mobileid:" + mobileid + "没有在lottery中查询到");
			throw new RuyicaiException("注册代理失败,没有找到该手机号.");
		}
		if (parentAgentUserinfo.getUserno().equals(userno)) {
			logger.info("注册用户和代理用户相同");
			throw new RuyicaiException("注册用户和代理用户相同");
		}
		Tagent parentAgent = Tagent.createIfNotExists(parentAgentUserinfo.getUserno(), null, null, null);
		logger.info("创建上级代理用户" + parentAgent);
		Tagent childAgent = Tagent.saveOrUpdate(userno, null, parentAgent.getId(), null);
		logger.info("创建下级代理用户" + childAgent);
	}

	@Transactional
	public String addNumOneYear(String body) {
		if (!addNumOneYearSwitch.equals("1")) {
			logger.info("活动已停止或未开启,直接追号一年,不参加活动");
			String flowno = lotteryService.subscribeOrder(body);
			return flowno;
		} else {
			logger.info("参加追号包年活动");
			OrderRequest orderRequest = JsonUtil.fromJsonToObject(body, OrderRequest.class);
			String buyuserno = orderRequest.getBuyuserno();
			Tuserinfo tuserinfo = lotteryService.findTuserinfoByUserno(buyuserno);
			if (tuserinfo == null) {
				throw new RuyicaiException(ErrorCode.UserMod_UserNotExists);
			}
			if (StringUtils.isBlank(tuserinfo.getMobileid()) || StringUtils.isBlank(tuserinfo.getName())
					|| StringUtils.isBlank(tuserinfo.getCertid()) || tuserinfo.getCertid().equals("111111111111111111")) {
				throw new RuyicaiException(ErrorCode.UserMod_InformationNotEnough);
			}
			TaddNumActivity activity = TaddNumActivity.findTaddNumActivity(buyuserno);
			if (activity != null) {
				throw new RuyicaiException(ErrorCode.ActionCenter_HadJoin);
			}
			TaddNumActivity taddNumActivity = TaddNumActivity.createTaddNumActivity(buyuserno, null);
			String flowno = lotteryService.subscribeOrder(body);
			taddNumActivity.updateTaddNumActivity(flowno);
			return flowno;
		}
	}

	/**
	 * 领取赠送奖金
	 * 
	 * @param sendMoneyid
	 *            赠送彩金ID
	 * @return SendMoneyDetails
	 */
	@Transactional
	public SendMoneyDetails reciveSendMoney(String sendMoneyid) {
		logger.info("领取赠送sendMoneyid:{}", new String[] { sendMoneyid });
		SendMoneyDetails details = SendMoneyDetails.findSendMoneyDetails(sendMoneyid, true);
		if (details.getReciveState() == 1) {
			throw new RuyicaiException(ErrorCode.HavereciveMoney);
		}
		String reciverUserno = details.getReciverUserno();
		Tuserinfo reciveTuserinfo = lotteryService.findTuserinfoByUserno(reciverUserno);
		lotteryService.directChargeProcess(reciveTuserinfo.getUserno(), details.getAmt(),
				reciveTuserinfo.getSubChannel(), reciveTuserinfo.getChannel(), "领取红包" + details.getSendusername(),
				null, null);

		details.setReciveState(1);
		details.setReciveTime(new Date());
		details.merge();
		logger.info("领取成功 presentId:{},reciverUserno:{}", new String[] { details.getId(), details.getReciverUserno() });
		return details;
	}

	@Transactional
	public Boolean vipCase(Tuserinfo tuserinfo, BigDecimal amt, String businessId) {
		Boolean flag = false;
		logger.info("VIP购彩活动开始,businessId:" + businessId + " userno:" + tuserinfo.getUserno() + " amt:" + amt);
		try {
			if (!tuserinfo.getSubChannel().equals("00092493")) {
				return flag;
			}
			// 查找大用户购彩活动并且赠送彩金
			if (!Tjmsservice.createTjmsservice(businessId, ActionJmsType.VIP_USER_GOUCAI_ZENGSONG)) {
				logger.info("大客户赠送已重复 businessId:" + businessId + " userno:" + tuserinfo.getUserno());
				return false;
			}
			// 增加vip购彩金额
			this.doAddVipUserBuyAmount(tuserinfo, amt);
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
}
