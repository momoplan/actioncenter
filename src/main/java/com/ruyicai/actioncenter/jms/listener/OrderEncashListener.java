package com.ruyicai.actioncenter.jms.listener;

import java.math.BigDecimal;
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
import com.ruyicai.actioncenter.dao.TuserPrizeDetailDao;
import com.ruyicai.actioncenter.domain.SSCPrizedDetail;
import com.ruyicai.actioncenter.domain.SSCPrizedDetailPK;
import com.ruyicai.actioncenter.domain.Tactivity;
import com.ruyicai.actioncenter.domain.Tjmsservice;
import com.ruyicai.actioncenter.domain.WorldCupBigUser;
import com.ruyicai.actioncenter.service.LotteryService;
import com.ruyicai.actioncenter.service.SendActivityPrizeJms;
import com.ruyicai.actioncenter.util.DateUtil;
import com.ruyicai.actioncenter.util.JsonUtil;
import com.ruyicai.lottery.domain.Torder;
import com.ruyicai.lottery.domain.Tuserinfo;

@Service
public class OrderEncashListener {

	private Logger logger = LoggerFactory.getLogger(OrderEncashListener.class);

	@Autowired
	private LotteryService lotteryService;

	@Autowired
	private TactivityDao tactivityDao;
	
	@Autowired
	private TuserPrizeDetailDao tuserPrizeDetailDao;

	@Autowired
	private SendActivityPrizeJms sendActivityPrizeJms;

	@Value("${ruyicaiUserno}")
	private String ruyicaiUserno;

	public void orderEncashCustomer(@Body String orderJson) {
		logger.info("中奖加奖,orderJson:" + orderJson);
		if (StringUtils.isBlank(orderJson)) {
			return;
		}
		Torder order = JsonUtil.fromJsonToObject(orderJson, Torder.class);
		if (order == null) {
			return;
		}
		String userno = order.getUserno();
		Tuserinfo orderUserInfo = lotteryService.findTuserinfoByUserno(userno);
		if (orderUserInfo != null && orderUserInfo.getChannel() != null && orderUserInfo.getChannel().equals("991")) {
			logger.info("如意彩大户渠道不参加活动userno:" + userno);
			return;
		}
		try {
			addPrizeSSC(order, orderUserInfo);
			addPrize2Chuan1(order, orderUserInfo);
			addPrize11Xuan5(order, orderUserInfo);
			addPrizeShiyiyunDuojin(order, orderUserInfo);
			addPrizeDaXiaoDanShuang(order, orderUserInfo);
			addPrizeZuCai(order, orderUserInfo);
			addPrizeLanQiu(order, orderUserInfo);
			addPrizeJingcai2chuan1(order, orderUserInfo);
			addPrizeKuai3(order, orderUserInfo);
			addPrizeXinKuai3(order, orderUserInfo);
			addPrize3D(order, orderUserInfo);
			addBeiDan(order, orderUserInfo);
			addWorldCupBigUser(order, orderUserInfo);
			addPrizeJingcai(order, orderUserInfo);
			addPrizeJingcaiDanguan(order, orderUserInfo);
			addPrizeXingyunsaiche(order, orderUserInfo);
		} catch (Exception e) {
			logger.error("加奖活动异常", e);
		}
	}

	@Transactional
	public void addWorldCupBigUser(Torder order, Tuserinfo orderUserInfo) {
		Tactivity tactivity = tactivityDao.findTactivity(null, null, orderUserInfo.getSubChannel(), null,
				ActionJmsType.World_Cup_BigUser.value);
		if (order.getLotno().startsWith("J")) {
			if (tactivity != null) {
				String userno = orderUserInfo.getUserno();
				Long orderprizeamt = order.getOrderprizeamt().longValue();
				if (orderprizeamt > 0) {
					WorldCupBigUser worldCupBigUser = WorldCupBigUser.findWorldCupBigUser(userno);
					if (worldCupBigUser != null) {
						BigDecimal prize = BigDecimal.ZERO;
						String express = tactivity.getExpress();
						Map<String, Object> activity = JsonUtil.transferJson2Map(express);
						Integer percent = (Integer) activity.get("percent");
						prize = new BigDecimal(orderprizeamt).multiply(new BigDecimal(percent)).divide(
								new BigDecimal(100));
						if (prize.compareTo(BigDecimal.ZERO) > 0) {
							if (Tjmsservice.createTjmsservice(order.getId(), ActionJmsType.World_Cup_BigUser)) {
								logger.info(tactivity.getMemo() + "prize:" + prize.longValue());
								sendActivityPrizeJms.sendPrize2UserJMS(userno, prize, ActionJmsType.World_Cup_BigUser,
										tactivity.getMemo(), order.getId(), "", "");
							}
						}
					} else {
						logger.info("不是世界杯大用户userno:" + userno);
					}
				}
			}
		} else {
			logger.info("不是竞彩订单lotno:" + order.getLotno());
		}
	}

	@Transactional
	public void addBeiDan(Torder order, Tuserinfo orderUserInfo) {
		String userno = orderUserInfo.getUserno();
		if (userno.equals(ruyicaiUserno)) {
			logger.info("如意彩账户购买,不加奖");
			return;
		}
		Tuserinfo tuserinfo = orderUserInfo;
		Tactivity tactivity = tactivityDao.findTactivity(order.getLotno(), null, tuserinfo.getSubChannel(), null,
				ActionJmsType.BeiDan_JiaJiang.value);
		if (tactivity != null) {
			Long orderprizeamt = order.getOrderprizeamt().longValue();
			if (orderprizeamt > 0) {
				BigDecimal prize = BigDecimal.ZERO;
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer step1min = (Integer) activity.get("step1min");
				Integer step1max = (Integer) activity.get("step1max");
				Integer step1prize = (Integer) activity.get("step1prize");
				Integer step2min = (Integer) activity.get("step2min");
				Integer step2max = (Integer) activity.get("step2max");
				Integer step2prize = (Integer) activity.get("step2prize");
				Integer step3min = (Integer) activity.get("step3min");
				Integer step3max = (Integer) activity.get("step3max");
				Integer step3prize = (Integer) activity.get("step3prize");
				Integer step4 = (Integer) activity.get("step4");
				Integer step4prize = (Integer) activity.get("step4prize");
				if (orderprizeamt >= step1min && orderprizeamt <= step1max) {
					prize = new BigDecimal(step1prize);
				} else if (orderprizeamt >= step2min && orderprizeamt <= step2max) {
					prize = new BigDecimal(step2prize);
				} else if (orderprizeamt >= step3min && orderprizeamt <= step3max) {
					prize = new BigDecimal(step3prize);
				} else if (orderprizeamt >= step4) {
					prize = new BigDecimal(step4prize);
				}
				if (prize.compareTo(BigDecimal.ZERO) > 0) {
					if (Tjmsservice.createTjmsservice(order.getId(), ActionJmsType.BeiDan_JiaJiang)) {
						logger.info(tactivity.getMemo() + "prize:" + prize.longValue());
						sendActivityPrizeJms.sendPrize2UserJMS(userno, prize, ActionJmsType.BeiDan_JiaJiang,
								tactivity.getMemo(), order.getId(), "", "");
					}
				}
			}
		}
	}

	@Transactional
	public void addPrize3D(Torder order, Tuserinfo orderUserInfo) {
		String userno = orderUserInfo.getUserno();
		if (userno.equals(ruyicaiUserno)) {
			logger.info("如意彩账户购买,不加奖");
			return;
		}
		Tuserinfo tuserinfo = orderUserInfo;
		Tactivity tactivity = tactivityDao.findTactivity(order.getLotno(), order.getPlaytype(),
				tuserinfo.getSubChannel(), null, ActionJmsType.FuCai3D_JiaJiang.value);
		if (tactivity != null) {
			Long orderprizeamt = order.getOrderprizeamt().longValue();
			if (orderprizeamt > 0) {
				BigDecimal prize = BigDecimal.ZERO;
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer step = (Integer) activity.get("step");
				Integer prizeamt = (Integer) activity.get("prizeamt");
				Integer topprize = (Integer) activity.get("topprize");
				int multiple = orderprizeamt.intValue() / step;
				if (multiple > 0) {
					prize = new BigDecimal(multiple * prizeamt);
				}
				if (prize.compareTo(new BigDecimal(topprize)) > 0) {
					prize = new BigDecimal(topprize);
				}
				if (prize.compareTo(BigDecimal.ZERO) > 0) {
					if (Tjmsservice.createTjmsservice(order.getId(), ActionJmsType.FuCai3D_JiaJiang)) {
						logger.info(tactivity.getMemo() + "prize:" + prize.longValue());
						sendActivityPrizeJms.sendPrize2UserJMS(userno, prize, ActionJmsType.FuCai3D_JiaJiang,
								tactivity.getMemo(), order.getId(), "", "");
					}
				}
			}
		}
	}

	@Transactional
	public void addPrizeKuai3(Torder order, Tuserinfo orderUserInfo) {
		String userno = orderUserInfo.getUserno();
		if (userno.equals(ruyicaiUserno)) {
			logger.info("如意彩账户购买,不加奖");
			return;
		}
		Tuserinfo tuserinfo = orderUserInfo;
		Tactivity tactivity = tactivityDao.findTactivity(order.getLotno(), null, tuserinfo.getSubChannel(), null,
				ActionJmsType.Kuai3_JiaJiang.value);
		if (tactivity != null) {
			Long orderprizeamt = order.getOrderprizeamt().longValue();
			if (orderprizeamt > 0) {
				BigDecimal prize = BigDecimal.ZERO;
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer minprize = (Integer) activity.get("minprize");
				Integer percent = (Integer) activity.get("percent");
				Integer topprize = (Integer) activity.get("topprize");

				if (orderprizeamt >= minprize) {
					prize = new BigDecimal(orderprizeamt).multiply(new BigDecimal(percent)).divide(new BigDecimal(100));
					if (prize.compareTo(new BigDecimal(topprize)) > 0) {
						prize = new BigDecimal(topprize);
					}
					if (prize.compareTo(BigDecimal.ZERO) > 0) {
						if (Tjmsservice.createTjmsservice(order.getId(), ActionJmsType.Kuai3_JiaJiang)) {
							logger.info(tactivity.getMemo() + "prize:" + prize.longValue());
							sendActivityPrizeJms.sendPrize2UserJMS(userno, prize, ActionJmsType.Kuai3_JiaJiang,
									tactivity.getMemo(), order.getId(), "", "");
						}
					}
				} else {
					logger.info("中奖金额小于" + minprize + "不参与活动");
				}

			}
		}
	}
	
	@Transactional
	public void addPrizeXinKuai3(Torder order, Tuserinfo orderUserInfo) {
		String userno = orderUserInfo.getUserno();
		if (userno.equals(ruyicaiUserno)) {
			logger.info("如意彩账户购买,不加奖");
			return;
		}
		Tuserinfo tuserinfo = orderUserInfo;
		Tactivity tactivity = tactivityDao.findTactivity(order.getLotno(), null, tuserinfo.getSubChannel(), null,
				ActionJmsType.XinKuai3_AddPrize.value);
		if (tactivity != null) {
			Long orderprizeamt = order.getOrderprizeamt().longValue();
			if (orderprizeamt > 0) {
				BigDecimal prize = BigDecimal.ZERO;
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer minprize = (Integer) activity.get("minprize");
				Integer percent = (Integer) activity.get("percent");
				Integer topprize = (Integer) activity.get("topprize");

				if (orderprizeamt >= minprize) {
					prize = new BigDecimal(orderprizeamt).multiply(new BigDecimal(percent)).divide(new BigDecimal(100));
					if (prize.compareTo(new BigDecimal(topprize)) > 0) {
						prize = new BigDecimal(topprize);
					}
					if (prize.compareTo(BigDecimal.ZERO) > 0) {
						if (Tjmsservice.createTjmsservice(order.getId(), ActionJmsType.XinKuai3_AddPrize)) {
							logger.info(tactivity.getMemo() + "新快三prize:" + prize.longValue());
							sendActivityPrizeJms.sendPrize2UserJMS(userno, prize, ActionJmsType.XinKuai3_AddPrize,
									tactivity.getMemo(), order.getId(), "", "");
						}
					}
				} else {
					logger.info("中奖金额小于" + minprize + "不参与活动");
				}
			}
		}
	}

	@Transactional
	public void addPrizeJingcai2chuan1(Torder order, Tuserinfo orderUserInfo) {
		String userno = null;
		Tuserinfo tuserinfo = null;
		Long orderprizeamt = 0L;
		if (StringUtils.isNotBlank(order.getTlotcaseid())) {
			return;
		}
		userno = orderUserInfo.getUserno();
		tuserinfo = orderUserInfo;
		orderprizeamt = order.getOrderprizeamt().longValue();
		if (userno.equals(ruyicaiUserno)) {
			logger.info("如意彩账户购买,不加奖");
			return;
		}
		String lotno = order.getLotno();
		if (!lotno.startsWith("J")) {
			return;
		}
		Tactivity tactivity = tactivityDao.findTactivity(null, order.getPlaytype(), tuserinfo.getSubChannel(), null,
				ActionJmsType.Encash_Jingcai_2Chan1.value);
		if (tactivity != null) {
			if (orderprizeamt > 0) {
				BigDecimal prize = BigDecimal.ZERO;
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer step1min = (Integer) activity.get("step1min");
				Integer step1max = (Integer) activity.get("step1max");
				Integer step1prize = (Integer) activity.get("step1prize");
				Integer step2min = (Integer) activity.get("step2min");
				Integer step2max = (Integer) activity.get("step2max");
				Integer step2prize = (Integer) activity.get("step2prize");
				Integer step3min = (Integer) activity.get("step3min");
				Integer step3max = (Integer) activity.get("step3max");
				Integer step3prize = (Integer) activity.get("step3prize");
				Integer step4min = (Integer) activity.get("step4min");
				Integer step4max = (Integer) activity.get("step4max");
				Integer step4prize = (Integer) activity.get("step4prize");
				Integer step5min = (Integer) activity.get("step5min");
				Integer step5max = (Integer) activity.get("step5max");
				Integer step5prize = (Integer) activity.get("step5prize");
				Integer step6min = (Integer) activity.get("step6min");
				Integer step6max = (Integer) activity.get("step6max");
				Integer step6prize = (Integer) activity.get("step6prize");
				Integer step7 = (Integer) activity.get("step7");
				Integer step7prize = (Integer) activity.get("step7prize");
				if (orderprizeamt >= step1min && orderprizeamt <= step1max) {
					prize = new BigDecimal(step1prize);
				} else if (orderprizeamt >= step2min && orderprizeamt <= step2max) {
					prize = new BigDecimal(step2prize);
				} else if (orderprizeamt >= step3min && orderprizeamt <= step3max) {
					prize = new BigDecimal(step3prize);
				} else if (orderprizeamt >= step4min && orderprizeamt <= step4max) {
					prize = new BigDecimal(step4prize);
				} else if (orderprizeamt >= step5min && orderprizeamt <= step5max) {
					prize = new BigDecimal(step5prize);
				} else if (orderprizeamt >= step6min && orderprizeamt <= step6max) {
					prize = new BigDecimal(step6prize);
				} else if (orderprizeamt >= step7) {
					prize = new BigDecimal(step7prize);
				}
				if (prize.compareTo(BigDecimal.ZERO) > 0) {
					Tactivity wcbu = tactivityDao.findTactivity(null, null, orderUserInfo.getSubChannel(), null,
							ActionJmsType.World_Cup_BigUser.value);
					if (wcbu != null) {
						WorldCupBigUser worldCupBigUser = WorldCupBigUser.findWorldCupBigUser(userno);
						if (worldCupBigUser != null) {
							logger.info("参与世界杯大户活动，不再参加此活动userno:" + userno);
							return;
						}
					}
					if (Tjmsservice.createTjmsservice(order.getId(), ActionJmsType.Encash_Jingcai_2Chan1)) {
						logger.info(tactivity.getMemo() + prize);
						sendActivityPrizeJms.sendPrize2UserJMS(userno, prize, ActionJmsType.Encash_Jingcai_2Chan1,
								tactivity.getMemo(), order.getId(), "", "");
					}
				}
			}
		}
	}

	@Transactional
	public void addPrizeLanQiu(Torder order, Tuserinfo orderUserInfo) {
		String userno = null;
		Tuserinfo tuserinfo = null;
		Long orderprizeamt = 0L;// order.getOrderprizeamt().longValue();
		if (StringUtils.isNotBlank(order.getTlotcaseid())) {
			// 合买加奖由DispatchCaseLotFinishListener处理
			return;
		}
		userno = orderUserInfo.getUserno();
		tuserinfo = orderUserInfo;
		orderprizeamt = order.getOrderprizeamt().longValue();
		if (userno.equals(ruyicaiUserno)) {
			logger.info("如意彩账户购买,不加奖");
			return;
		}
		Tactivity tactivity = tactivityDao.findTactivity(order.getLotno(), null, tuserinfo.getSubChannel(), null,
				ActionJmsType.Encash_LanQiu_AddPrize.value);
		if (tactivity != null) {
			// Long orderprizeamt = order.getOrderprizeamt().longValue();
			if (orderprizeamt > 0) {
				BigDecimal prize = BigDecimal.ZERO;
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer step1min = (Integer) activity.get("step1min");
				Integer step1max = (Integer) activity.get("step1max");
				Integer step1prize = (Integer) activity.get("step1prize");
				Integer step2min = (Integer) activity.get("step2min");
				Integer step2max = (Integer) activity.get("step2max");
				Integer step2prize = (Integer) activity.get("step2prize");
				Integer step3min = (Integer) activity.get("step3min");
				Integer step3max = (Integer) activity.get("step3max");
				Integer step3prize = (Integer) activity.get("step3prize");
				Integer step4min = (Integer) activity.get("step4min");
				Integer step4max = (Integer) activity.get("step4max");
				Integer step4prize = (Integer) activity.get("step4prize");
				Integer step5min = (Integer) activity.get("step5min");
				Integer step5max = (Integer) activity.get("step5max");
				Integer step5prize = (Integer) activity.get("step5prize");
				Integer step6min = (Integer) activity.get("step6min");
				Integer step6max = (Integer) activity.get("step6max");
				Integer step6prize = (Integer) activity.get("step6prize");
				Integer step7 = (Integer) activity.get("step7");
				Integer step7prize = (Integer) activity.get("step7prize");
				if (orderprizeamt >= step1min && orderprizeamt <= step1max) {
					prize = new BigDecimal(step1prize);
				} else if (orderprizeamt >= step2min && orderprizeamt <= step2max) {
					prize = new BigDecimal(step2prize);
				} else if (orderprizeamt >= step3min && orderprizeamt <= step3max) {
					prize = new BigDecimal(step3prize);
				} else if (orderprizeamt >= step4min && orderprizeamt <= step4max) {
					prize = new BigDecimal(step4prize);
				} else if (orderprizeamt >= step5min && orderprizeamt <= step5max) {
					prize = new BigDecimal(step5prize);
				} else if (orderprizeamt >= step6min && orderprizeamt <= step6max) {
					prize = new BigDecimal(step6prize);
				} else if (orderprizeamt >= step7) {
					prize = new BigDecimal(step7prize);
				}
				
				if (prize.compareTo(BigDecimal.ZERO) > 0) {
					Tactivity wcbu = tactivityDao.findTactivity(null, null, orderUserInfo.getSubChannel(), null,
							ActionJmsType.World_Cup_BigUser.value);
					if (wcbu != null) {
						WorldCupBigUser worldCupBigUser = WorldCupBigUser.findWorldCupBigUser(userno);
						if (worldCupBigUser != null) {
							logger.info("参与世界杯大户活动，不再参加此活动userno:" + userno);
							return;
						}
					}
					if (Tjmsservice.createTjmsservice(order.getId(), ActionJmsType.Encash_LanQiu_AddPrize)) {
						logger.info(tactivity.getMemo() + "prize:" + prize.longValue());
						sendActivityPrizeJms.sendPrize2UserJMS(userno, prize, ActionJmsType.Encash_LanQiu_AddPrize,
								tactivity.getMemo(), order.getId(), "", "");
					}
				}
			}
		}
	}

	@Transactional
	public void addPrizeZuCai(Torder order, Tuserinfo orderUserInfo) {
		String userno = null;
		Tuserinfo tuserinfo = null;
		if (StringUtils.isNotBlank(order.getTlotcaseid())) {
			// 合买加奖由DispatchCaseLotFinishListener处理
			return;
		}
		userno = orderUserInfo.getUserno();
		tuserinfo = orderUserInfo;
		if (userno.equals(ruyicaiUserno)) {
			logger.info("如意彩账户购买,不加奖");
			return;
		}
		Tactivity tactivity = tactivityDao.findTactivity(order.getLotno(), null, tuserinfo.getSubChannel(), null,
				ActionJmsType.Encash_ZuCai_AddPrize.value);
		if (tactivity != null) {
			Long orderprizeamt = order.getOrderprizeamt().longValue();
			if (orderprizeamt > 0) {
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer step = (Integer) activity.get("step");
				Integer prizeamt = (Integer) activity.get("prizeamt");
				if (orderprizeamt >= step) {
					BigDecimal prize = new BigDecimal(prizeamt);
					if (prize.compareTo(BigDecimal.ZERO) > 0) {
						if (Tjmsservice.createTjmsservice(order.getId(), ActionJmsType.Encash_ZuCai_AddPrize)) {
							logger.info(tactivity.getMemo() + "prize:" + prize.longValue());
							sendActivityPrizeJms.sendPrize2UserJMS(userno, prize, ActionJmsType.Encash_ZuCai_AddPrize,
									tactivity.getMemo(), order.getId(), "", "");
						}
					}
				}
			}
		}
	}
	
	@Transactional
	public void addPrizeJingcai(Torder order, Tuserinfo orderUserInfo){
		String userno = null;
		Tuserinfo tuserinfo = null;
		String lotno = order.getLotno();
		if (!lotno.startsWith("J")) {
			return;
		}
		if(lotno.equals("J00001") || lotno.equals("J00002") || lotno.equals("J00003")
				|| lotno.equals("J00004") || lotno.equals("J00011") || lotno.equals("J00013")){
			logger.info("五大联赛新赛季竞彩足球加奖活动...."+lotno);
			if (StringUtils.isNotBlank(order.getTlotcaseid())) {
				// 合买加奖由DispatchCaseLotFinishListener处理
				return;
			}
			userno = orderUserInfo.getUserno();
			tuserinfo = orderUserInfo;
			if (userno.equals(ruyicaiUserno)) {
				logger.info("如意彩账户购买,不加奖");
				return;
			}
			Tactivity tactivity = tactivityDao.findTactivity(order.getLotno(), null, tuserinfo.getSubChannel(), null,
					ActionJmsType.Encash_JingCai_AddPrize.value);
			if (tactivity != null) {
				Long orderprizeamt = order.getOrderprizeamt().longValue();
				if(orderprizeamt > 0){
					BigDecimal prize = BigDecimal.ZERO;
					String express = tactivity.getExpress();
					Map<String, Object> activity = JsonUtil.transferJson2Map(express);
					Integer minprize = (Integer) activity.get("minprize");
					Integer percent = (Integer) activity.get("percent");
					Integer topprize = (Integer) activity.get("topprize");
					
					if(orderprizeamt >= minprize){
						BigDecimal amtTotal = tuserPrizeDetailDao.statisticPrizeDetail(order.getUserno(), ActionJmsType.Encash_JingCai_AddPrize.value, new Date());
						if(amtTotal.compareTo(new BigDecimal(topprize))<0){
							prize = new BigDecimal(orderprizeamt).divide(new BigDecimal(minprize)).setScale(0,BigDecimal.ROUND_DOWN)
									.multiply(new BigDecimal(minprize)).multiply(new BigDecimal(percent)).divide(new BigDecimal(100));
							if(prize.compareTo(new BigDecimal(topprize))>=0){
								prize = new BigDecimal(topprize);
							}else{
								if((prize.add(amtTotal)).compareTo(new BigDecimal(topprize))>0){
									prize = new BigDecimal(topprize).subtract(amtTotal);
								}
							}
							if (Tjmsservice.createTjmsservice(order.getId(), ActionJmsType.Encash_JingCai_AddPrize)) {
								logger.info(tactivity.getMemo() + "prize:" + prize.longValue());
								sendActivityPrizeJms.sendPrize2UserJMS(userno, prize, ActionJmsType.Encash_JingCai_AddPrize,
										tactivity.getMemo(), order.getId(), "", "");
							}
						}else{
							logger.info("用户:"+userno+"当日加奖金额已达最高值" + topprize + "不参与活动");
						}
					}else{
						logger.info("中奖金额小于" + minprize + "不参与活动");
					}
				}
			}
		}
	}

	@Transactional
	public void addPrizeDaXiaoDanShuang(Torder order, Tuserinfo orderUserInfo) {
		String userno = orderUserInfo.getUserno();
		if (userno.equals(ruyicaiUserno)) {
			logger.info("如意彩账户购买,不加奖");
			return;
		}
		Tuserinfo tuserinfo = orderUserInfo;
		Tactivity tactivity = tactivityDao.findTactivity(order.getLotno(), order.getPlaytype(),
				tuserinfo.getSubChannel(), null, ActionJmsType.Encash_DaXiaoDanShuang_AddPrize.value);
		if (tactivity != null) {
			Long orderprizeamt = order.getOrderprizeamt().longValue();
			if (orderprizeamt > 0) {
				BigDecimal prize = BigDecimal.ZERO;
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer step1min = (Integer) activity.get("step1min");
				Integer step1max = (Integer) activity.get("step1max");
				Integer step1prize = (Integer) activity.get("step1prize");
				Integer step2min = (Integer) activity.get("step2min");
				Integer step2max = (Integer) activity.get("step2max");
				Integer step2prize = (Integer) activity.get("step2prize");
				Integer step3min = (Integer) activity.get("step3min");
				Integer step3max = (Integer) activity.get("step3max");
				Integer step3prize = (Integer) activity.get("step3prize");
				Integer step4 = (Integer) activity.get("step4");
				Integer step4prize = (Integer) activity.get("step4prize");
				if (orderprizeamt >= step1min && orderprizeamt <= step1max) {
					prize = new BigDecimal(step1prize);
				} else if (orderprizeamt >= step2min && orderprizeamt <= step2max) {
					prize = new BigDecimal(step2prize);
				} else if (orderprizeamt >= step3min && orderprizeamt <= step3max) {
					prize = new BigDecimal(step3prize);
				} else if (orderprizeamt >= step4) {
					prize = new BigDecimal(step4prize);
				}
				if (prize.compareTo(BigDecimal.ZERO) > 0) {
					if (Tjmsservice.createTjmsservice(order.getId(), ActionJmsType.Encash_DaXiaoDanShuang_AddPrize)) {
						logger.info(tactivity.getMemo() + "prize:" + prize.longValue());
						sendActivityPrizeJms.sendPrize2UserJMS(userno, prize,
								ActionJmsType.Encash_DaXiaoDanShuang_AddPrize, tactivity.getMemo(), order.getId(), "",
								"");
					}
				}
			}
		}
	}

	@Transactional
	public void addPrize11Xuan5(Torder order, Tuserinfo orderUserInfo) {
		String userno = orderUserInfo.getUserno();
		if (userno.equals(ruyicaiUserno)) {
			logger.info("如意彩账户购买,不加奖");
			return;
		}
		Tuserinfo tuserinfo = orderUserInfo;
		Tactivity tactivity = tactivityDao.findTactivity(order.getLotno(), null, tuserinfo.getSubChannel(), null,
				ActionJmsType.Encash_DuoLeCai_AddPrize.value);
		if (tactivity != null) {
			Long orderprizeamt = order.getOrderprizeamt().longValue();
			if (orderprizeamt > 0) {
				BigDecimal prize = BigDecimal.ZERO;
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer step1min = (Integer) activity.get("step1min");
				Integer step1max = (Integer) activity.get("step1max");
				Integer step1prize = (Integer) activity.get("step1prize");
				Integer step2min = (Integer) activity.get("step2min");
				Integer step2max = (Integer) activity.get("step2max");
				Integer step2prize = (Integer) activity.get("step2prize");
				Integer step3min = (Integer) activity.get("step3min");
				Integer step3max = (Integer) activity.get("step3max");
				Integer step3prize = (Integer) activity.get("step3prize");
				Integer step4 = (Integer) activity.get("step4");
				Integer step4prize = (Integer) activity.get("step4prize");
				if (orderprizeamt >= step1min && orderprizeamt <= step1max) {
					prize = new BigDecimal(step1prize);
				} else if (orderprizeamt >= step2min && orderprizeamt <= step2max) {
					prize = new BigDecimal(step2prize);
				} else if (orderprizeamt >= step3min && orderprizeamt <= step3max) {
					prize = new BigDecimal(step3prize);
				} else if (orderprizeamt >= step4) {
					prize = new BigDecimal(step4prize);
				}
				// 如果用户当天赠送金额大于800元。则不再赠送
				String day = DateUtil.format("yyyy-MM-dd", new Date());
				SSCPrizedDetail detail = SSCPrizedDetail.findSSCPrizedDetail(new SSCPrizedDetailPK(userno, day));
				if (detail != null && detail.getTotalPrizeAmt() != null) {
					if (detail.getTotalPrizeAmt().compareTo(new BigDecimal(80000)) > 0) {
						logger.info("赠送超过800元，不再赠送.userno:{},day:{},totalPrizeAmt:{}", new String[] { userno, day,
								detail.getTotalPrizeAmt() + "" });
						return;
					}
				}
				if (prize.compareTo(BigDecimal.ZERO) > 0) {
					if (Tjmsservice.createTjmsservice(order.getId(), ActionJmsType.Encash_DuoLeCai_AddPrize)) {
						logger.info(tactivity.getMemo() + "重庆11选5中奖加奖prize:" + prize.longValue());
						sendActivityPrizeJms.sendPrize2UserJMS(userno, prize, ActionJmsType.Encash_DuoLeCai_AddPrize,
								tactivity.getMemo(), order.getId(), "", "");
					}
				}
			}
		}
	}
	
	@Transactional
	public void addPrizeShiyiyunDuojin(Torder order, Tuserinfo orderUserInfo) {
		String userno = orderUserInfo.getUserno();
		if (userno.equals(ruyicaiUserno)) {
			logger.info("如意彩账户购买,不加奖");
			return;
		}
		Tuserinfo tuserinfo = orderUserInfo;
		Tactivity tactivity = tactivityDao.findTactivity(order.getLotno(), null, tuserinfo.getSubChannel(), null,
				ActionJmsType.Shiyiyunduojin_AddPrize.value);
		if (tactivity != null) {
			Long orderprizeamt = order.getOrderprizeamt().longValue();
			if (orderprizeamt > 0) {
				BigDecimal prize = BigDecimal.ZERO;
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer step1min = (Integer) activity.get("step1min");
				Integer step1max = (Integer) activity.get("step1max");
				Integer step1prize = (Integer) activity.get("step1prize");
				Integer step2min = (Integer) activity.get("step2min");
				Integer step2max = (Integer) activity.get("step2max");
				Integer step2prize = (Integer) activity.get("step2prize");
				Integer step3min = (Integer) activity.get("step3min");
				Integer step3max = (Integer) activity.get("step3max");
				Integer step3prize = (Integer) activity.get("step3prize");
				Integer step4 = (Integer) activity.get("step4");
				Integer step4prize = (Integer) activity.get("step4prize");
				if (orderprizeamt >= step1min && orderprizeamt <= step1max) {
					prize = new BigDecimal(step1prize);
				} else if (orderprizeamt >= step2min && orderprizeamt <= step2max) {
					prize = new BigDecimal(step2prize);
				} else if (orderprizeamt >= step3min && orderprizeamt <= step3max) {
					prize = new BigDecimal(step3prize);
				} else if (orderprizeamt >= step4) {
					prize = new BigDecimal(step4prize);
				}
				// 如果用户当天赠送金额大于800元。则不再赠送
				String day = DateUtil.format("yyyy-MM-dd", new Date());
				SSCPrizedDetail detail = SSCPrizedDetail.findSSCPrizedDetail(new SSCPrizedDetailPK(userno, day));
				if (detail != null && detail.getTotalPrizeAmt() != null) {
					if (detail.getTotalPrizeAmt().compareTo(new BigDecimal(80000)) > 0) {
						logger.info("赠送超过800元，不再赠送.userno:{},day:{},totalPrizeAmt:{}", new String[] { userno, day,
								detail.getTotalPrizeAmt() + "" });
						return;
					}
				}
				if (prize.compareTo(BigDecimal.ZERO) > 0) {
					if (Tjmsservice.createTjmsservice(order.getId(), ActionJmsType.Shiyiyunduojin_AddPrize)) {
						logger.info(tactivity.getMemo() + "十一运夺金中奖加奖prize:" + prize.longValue());
						sendActivityPrizeJms.sendPrize2UserJMS(userno, prize, ActionJmsType.Shiyiyunduojin_AddPrize,
								tactivity.getMemo(), order.getId(), "", "");
					}
				}
			}
		}
	}

	@Transactional
	public void addPrize2Chuan1(Torder order, Tuserinfo orderUserInfo) {
		String userno = null;
		Tuserinfo tuserinfo = null;
		Long orderprizeamt = 0L;// order.getOrderprizeamt().longValue();
		if (StringUtils.isNotBlank(order.getTlotcaseid())) {
			// 合买加奖由DispatchCaseLotFinishListener处理
			return;
		}
		userno = orderUserInfo.getUserno();
		tuserinfo = orderUserInfo;
		orderprizeamt = order.getOrderprizeamt().longValue();
		if (userno.equals(ruyicaiUserno)) {
			logger.info("如意彩账户购买,不加奖");
			return;
		}
		Tactivity tactivity = tactivityDao.findTactivity(order.getLotno(), order.getPlaytype(),
				tuserinfo.getSubChannel(), null, ActionJmsType.Encash_2chuan1_AddPrize.value);
		if (tactivity != null) {
			// Long orderprizeamt = order.getOrderprizeamt().longValue();
			if (orderprizeamt > 0) {
				BigDecimal prize = BigDecimal.ZERO;
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer step = (Integer) activity.get("step");
				Integer prizeamt = (Integer) activity.get("prizeamt");
				int multiple = orderprizeamt.intValue() / step;
				if (multiple > 0) {
					prize = new BigDecimal(multiple * prizeamt);
				}
				if (prize.compareTo(BigDecimal.ZERO) > 0) {
					Tactivity wcbu = tactivityDao.findTactivity(null, null, orderUserInfo.getSubChannel(), null,
							ActionJmsType.World_Cup_BigUser.value);
					if (wcbu != null) {
						WorldCupBigUser worldCupBigUser = WorldCupBigUser.findWorldCupBigUser(userno);
						if (worldCupBigUser != null) {
							logger.info("参与世界杯大户活动，不再参加此活动userno:" + userno);
							return;
						}
					}
					if (Tjmsservice.createTjmsservice(order.getId(), ActionJmsType.Encash_2chuan1_AddPrize)) {
						logger.info(tactivity.getMemo() + "加奖prize:" + prize.longValue());
						sendActivityPrizeJms.sendPrize2UserJMS(userno, prize, ActionJmsType.Encash_2chuan1_AddPrize,
								tactivity.getMemo(), order.getId(), "", "");
					}
				}
			}
		}
	}

	@Transactional
	public void addPrizeSSC(Torder order, Tuserinfo orderUserInfo) {
		String userno = orderUserInfo.getUserno();
		if (userno.equals(ruyicaiUserno)) {
			logger.info("如意彩账户购买,不加奖");
			return;
		}
		Tuserinfo tuserinfo = orderUserInfo;
		Tactivity tactivity = tactivityDao.findTactivity(order.getLotno(), null, tuserinfo.getSubChannel(), null,
				ActionJmsType.EncashAddPrize.value);
		if (tactivity != null) {
			Long orderprizeamt = order.getOrderprizeamt().longValue();
			if (orderprizeamt > 0) {
				BigDecimal prize = BigDecimal.ZERO;
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer step1min = (Integer) activity.get("step1min");
				Integer step1max = (Integer) activity.get("step1max");
				Integer step1prize = (Integer) activity.get("step1prize");
				Integer step2min = (Integer) activity.get("step2min");
				Integer step2max = (Integer) activity.get("step2max");
				Integer step2prize = (Integer) activity.get("step2prize");
				Integer step3min = (Integer) activity.get("step3min");
				Integer step3max = (Integer) activity.get("step3max");
				Integer step3prize = (Integer) activity.get("step3prize");
				Integer step4 = (Integer) activity.get("step4");
				Integer step4prize = (Integer) activity.get("step4prize");
				if (orderprizeamt >= step1min && orderprizeamt <= step1max) {
					prize = new BigDecimal(step1prize);
				} else if (orderprizeamt >= step2min && orderprizeamt <= step2max) {
					prize = new BigDecimal(step2prize);
				} else if (orderprizeamt >= step3min && orderprizeamt <= step3max) {
					prize = new BigDecimal(step3prize);
				} else if (orderprizeamt >= step4) {
					prize = new BigDecimal(step4prize);
				}
				if (prize.compareTo(BigDecimal.ZERO) > 0) {
					if (Tjmsservice.createTjmsservice(order.getId(), ActionJmsType.EncashAddPrize)) {
						logger.info(tactivity.getMemo() + "prize:" + prize.longValue());
						sendActivityPrizeJms.sendPrize2UserJMS(userno, prize, ActionJmsType.EncashAddPrize,
								tactivity.getMemo(), order.getId(), "", "");
					}
				}
			}
		}
	}
	
	@Transactional
	public void addPrizeJingcaiDanguan(Torder order, Tuserinfo orderUserInfo) {
		String userno = orderUserInfo.getUserno();
		if (userno.equals(ruyicaiUserno)) {
			logger.info("如意彩账户购买,不加奖");
			return;
		}
		Tuserinfo tuserinfo = orderUserInfo;
		Tactivity tactivity = tactivityDao.findTactivity(order.getLotno(), order.getPlaytype(), tuserinfo.getSubChannel(), null,
				ActionJmsType.Encash_JingCaiDanGuan_AddPrize.value);
		if (tactivity != null) {
			Long orderprizeamt = order.getOrderprizeamt().longValue();
			if (orderprizeamt > 0) {
				BigDecimal prize = BigDecimal.ZERO;
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer minprize = (Integer) activity.get("minprize");
				Integer percent = (Integer) activity.get("percent");
				Integer topprize = (Integer) activity.get("topprize");
				if (orderprizeamt >= minprize) {
					prize = new BigDecimal(orderprizeamt).multiply(new BigDecimal(percent)).divide(new BigDecimal(100));
					if (prize.compareTo(new BigDecimal(topprize)) > 0) {
						prize = new BigDecimal(topprize);
					}
					if (prize.compareTo(BigDecimal.ZERO) > 0) {
						if (Tjmsservice.createTjmsservice(order.getId(), ActionJmsType.Encash_JingCaiDanGuan_AddPrize)) {
							logger.info(tactivity.getMemo() + "prize:" + prize.longValue());
							sendActivityPrizeJms.sendPrize2UserJMS(userno, prize, ActionJmsType.Encash_JingCaiDanGuan_AddPrize,
									tactivity.getMemo(), order.getId(), "", "");
						}
					}
				} else {
					logger.info("中奖金额小于" + minprize + "不参与活动");
				}
			}
		}
	}
	
	@Transactional
	public void addPrizeXingyunsaiche(Torder order, Tuserinfo orderUserInfo){
		String userno = orderUserInfo.getUserno();
		if (userno.equals(ruyicaiUserno)) {
			logger.info("如意彩账户购买,不加奖");
			return;
		}
		Tuserinfo tuserinfo = orderUserInfo;
		Tactivity tactivity = tactivityDao.findTactivity(order.getLotno(), order.getPlaytype(), tuserinfo.getSubChannel(), null,
				ActionJmsType.Xingyunsaiche_AddPrize.value);
		if (tactivity != null) {
			Long orderprizeamt = order.getOrderprizeamt().longValue();
			if (orderprizeamt > 0) {
				BigDecimal prize = BigDecimal.ZERO;
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer step1min = (Integer) activity.get("step1min");
				Integer step1max = (Integer) activity.get("step1max");
				Integer step1prize = (Integer) activity.get("step1prize");
				Integer step2min = (Integer) activity.get("step2min");
				Integer step2max = (Integer) activity.get("step2max");
				Integer step2prize = (Integer) activity.get("step2prize");
				Integer step3min = (Integer) activity.get("step3min");
				Integer step3max = (Integer) activity.get("step3max");
				Integer step3prize = (Integer) activity.get("step3prize");
				Integer step4 = (Integer) activity.get("step4");
				Integer step4prize = (Integer) activity.get("step4prize");
				if (orderprizeamt >= step1min && orderprizeamt <= step1max) {
					prize = new BigDecimal(step1prize);
				} else if (orderprizeamt >= step2min && orderprizeamt <= step2max) {
					prize = new BigDecimal(step2prize);
				} else if (orderprizeamt >= step3min && orderprizeamt <= step3max) {
					prize = new BigDecimal(step3prize);
				} else if (orderprizeamt >= step4) {
					prize = new BigDecimal(step4prize);
				}
				if (prize.compareTo(BigDecimal.ZERO) > 0) {
					if (Tjmsservice.createTjmsservice(order.getId(), ActionJmsType.Xingyunsaiche_AddPrize)) {
						logger.info(tactivity.getMemo() + "幸运赛车prize:" + prize.longValue());
						sendActivityPrizeJms.sendPrize2UserJMS(userno, prize, ActionJmsType.Xingyunsaiche_AddPrize,
								tactivity.getMemo(), order.getId(), "", "");
					}
				}
			}
		}
	}
	
}
