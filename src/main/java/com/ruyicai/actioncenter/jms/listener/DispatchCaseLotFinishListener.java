package com.ruyicai.actioncenter.jms.listener;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.apache.camel.Body;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.consts.ActionJmsType;
import com.ruyicai.actioncenter.dao.TactivityDao;
import com.ruyicai.actioncenter.dao.TuserPrizeDetailDao;
import com.ruyicai.actioncenter.domain.Tactivity;
import com.ruyicai.actioncenter.domain.Tjmsservice;
import com.ruyicai.actioncenter.domain.WorldCupBigUser;
import com.ruyicai.actioncenter.service.LotteryService;
import com.ruyicai.actioncenter.service.SendActivityPrizeJms;
import com.ruyicai.actioncenter.util.JsonUtil;
import com.ruyicai.lottery.domain.CaseLot;
import com.ruyicai.lottery.domain.Torder;
import com.ruyicai.lottery.domain.Tuserinfo;

@Service
public class DispatchCaseLotFinishListener {

	private Logger logger = LoggerFactory.getLogger(DispatchCaseLotFinishListener.class);

	@Autowired
	private LotteryService lotteryService;

	@Autowired
	private TactivityDao tactivityDao;
	
	@Autowired
	private TuserPrizeDetailDao tuserPrizeDetailDao;

	@Autowired
	private SendActivityPrizeJms sendActivityPrizeJms;

	public void dispatchCaseLotFinishCustomer(@Body String caseLotJson) {
		logger.info("合买派奖完成caseLotJson:" + caseLotJson);
		if (StringUtils.isBlank(caseLotJson)) {
			throw new IllegalArgumentException("the arguments caseLotJson  is require");
		}
		CaseLot caseLot = CaseLot.fromJsonToCaseLot(caseLotJson);
		if (caseLot == null) {
			return;
		}
		String starter = caseLot.getStarter();
		if (StringUtils.isBlank(starter)) {
			return;
		}
		Tuserinfo userInfo = lotteryService.findTuserinfoByUserno(starter);
		if (userInfo == null) {
			return;
		}
		if (userInfo != null && userInfo.getChannel() != null) {
			if (userInfo.getChannel().equals("991")) {
				logger.info("如意彩大户渠道不参加活动userno:" + starter);
				return;
			}
		}
		Torder torder = lotteryService.findTorderById(caseLot.getOrderid());
		if (torder == null) {
			return;
		}
		try {
			addPrize2Chuan1(caseLot, torder, userInfo);
			addPrizeLanQiu(caseLot, torder, userInfo);
			addPrizeJingcai2chuan1(caseLot, torder, userInfo);
			addPrize3D(caseLot, torder, userInfo);
			addWorldCupBigUser(caseLot, torder, userInfo);
			addPrizeJingcai(caseLot,torder, userInfo);
			addPrizeJingcaiDanguan(caseLot,torder, userInfo);
		} catch (Exception e) {
			logger.error("合买加奖异常", e);
		}
	}

	@Transactional
	public void addWorldCupBigUser(CaseLot caseLot, Torder order, Tuserinfo userInfo) {
		Tactivity tactivity = tactivityDao.findTactivity(null, null, userInfo.getSubChannel(), null,
				ActionJmsType.World_Cup_BigUser.value);
		if (order.getLotno().startsWith("J")) {
			if (tactivity != null) {
				String userno = userInfo.getUserno();
				String caselotId = caseLot.getId();
				Integer caselotprize = lotteryService.findCaseLotBuyAllPrizeamtById(caselotId, userno);
				if (caselotprize > 0) {
					WorldCupBigUser worldCupBigUser = WorldCupBigUser.findWorldCupBigUser(userno);
					if (worldCupBigUser != null) {
						BigDecimal prize = BigDecimal.ZERO;
						String express = tactivity.getExpress();
						Map<String, Object> activity = JsonUtil.transferJson2Map(express);
						Integer percent = (Integer) activity.get("percent");
						prize = new BigDecimal(caselotprize).multiply(new BigDecimal(percent)).divide(
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
	public void addPrize3D(CaseLot caseLot, Torder order, Tuserinfo tuserinfo) {
		Tactivity tactivity = tactivityDao.findTactivity(order.getLotno(), order.getPlaytype(),
				tuserinfo.getSubChannel(), null, ActionJmsType.FuCai3D_JiaJiang.value);
		if (tactivity != null) {
			int caselotprize = caseLot.getWinPreAmt().intValue();
			if (caselotprize > 0) {
				BigDecimal prize = BigDecimal.ZERO;
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer step = (Integer) activity.get("step");
				Integer prizeamt = (Integer) activity.get("prizeamt");
				Integer topprize = (Integer) activity.get("topprize");
				int multiple = caselotprize / step;
				if (multiple > 0) {
					prize = new BigDecimal(multiple * prizeamt);
				}
				if (prize.compareTo(new BigDecimal(topprize)) > 0) {
					prize = new BigDecimal(topprize);
				}
				if (prize.compareTo(BigDecimal.ZERO) > 0) {
					if (Tjmsservice.createTjmsservice(order.getId(), ActionJmsType.FuCai3D_JiaJiang)) {
						logger.info(tactivity.getMemo() + "prize:" + prize.longValue());
						sendActivityPrizeJms.sendPrize2UserJMS(tuserinfo.getUserno(), prize,
								ActionJmsType.FuCai3D_JiaJiang, tactivity.getMemo(), order.getId(), "", "");
					}
				}
			}
		}
	}

	@Transactional
	public void addPrizeJingcai(CaseLot caseLot, Torder torder, Tuserinfo userinfo){
		String userno = userinfo.getUserno();
		String lotno = torder.getLotno();
		if (!lotno.startsWith("J")) {
			return;
		}
		if(lotno.equals("J00001") || lotno.equals("J00002") || lotno.equals("J00003")
				|| lotno.equals("J00004") || lotno.equals("J00011") || lotno.equals("J00013")){
			logger.info("五大联赛新赛季竞彩足球合买加奖活动...."+lotno);
			Tactivity tactivity = tactivityDao.findTactivity(torder.getLotno(), null, userinfo.getSubChannel(), null,
					ActionJmsType.Encash_JingCai_AddPrize.value);
			if (tactivity != null) {
				String caselotId = caseLot.getId();
				Integer caselotprize = lotteryService.findCaseLotBuyAllPrizeamtById(caselotId, userinfo.getUserno());
				if (caselotprize > 0) {
					BigDecimal prize = BigDecimal.ZERO;
					String express = tactivity.getExpress();
					Map<String, Object> activity = JsonUtil.transferJson2Map(express);
					Integer minprize = (Integer) activity.get("minprize");
					Integer percent = (Integer) activity.get("percent");
					Integer topprize = (Integer) activity.get("topprize");
					
					if(caselotprize >= minprize){
						BigDecimal amtTotal = tuserPrizeDetailDao.statisticPrizeDetail(torder.getUserno(), ActionJmsType.Encash_JingCai_AddPrize.value, new Date());
						if(amtTotal.compareTo(new BigDecimal(topprize))<0){
							prize = new BigDecimal(caselotprize).divide(new BigDecimal(minprize)).setScale(0,BigDecimal.ROUND_DOWN)
									.multiply(new BigDecimal(minprize)).multiply(new BigDecimal(percent)).divide(new BigDecimal(100));
							if(prize.compareTo(new BigDecimal(topprize))>=0){
								prize = new BigDecimal(topprize);
							}else{
								if((prize.add(amtTotal)).compareTo(new BigDecimal(topprize))>0){
									prize = new BigDecimal(topprize).subtract(amtTotal);
								}
							}
							if (Tjmsservice.createTjmsservice(torder.getId(), ActionJmsType.Encash_JingCai_AddPrize)) {
								logger.info(tactivity.getMemo() + "prize:" + prize.longValue());
								sendActivityPrizeJms.sendPrize2UserJMS(userno, prize, ActionJmsType.Encash_JingCai_AddPrize,
										tactivity.getMemo(), torder.getId(), "", "");
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
	public void addPrizeJingcai2chuan1(CaseLot caseLot, Torder torder, Tuserinfo userinfo) {
		String userno = userinfo.getUserno();
		String lotno = torder.getLotno();
		if (lotno.equals("J00001") || lotno.equals("J00002") || lotno.equals("J00003") || lotno.equals("J00004")
				|| lotno.equals("J00011") || lotno.equals("J00013")) {
			Tactivity tactivity = tactivityDao.findTactivity(null, torder.getPlaytype(), userinfo.getSubChannel(), null,
					ActionJmsType.Encash_Jingcai_2Chan1.value);
			if (tactivity != null) {
				String caselotId = caseLot.getId();
				Integer caselotprize = lotteryService.findCaseLotBuyAllPrizeamtById(caselotId, userinfo.getUserno());
				if (caselotprize > 0) {
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
					if (caselotprize >= step1min && caselotprize <= step1max) {
						prize = new BigDecimal(step1prize);
					} else if (caselotprize >= step2min && caselotprize <= step2max) {
						prize = new BigDecimal(step2prize);
					} else if (caselotprize >= step3min && caselotprize <= step3max) {
						prize = new BigDecimal(step3prize);
					} else if (caselotprize >= step4min && caselotprize <= step4max) {
						prize = new BigDecimal(step4prize);
					} else if (caselotprize >= step5min && caselotprize <= step5max) {
						prize = new BigDecimal(step5prize);
					} else if (caselotprize >= step6min && caselotprize <= step6max) {
						prize = new BigDecimal(step6prize);
					} else if (caselotprize >= step7) {
						prize = new BigDecimal(step7prize);
					}
					if (prize.compareTo(BigDecimal.ZERO) > 0) {
						Tactivity wcbu = tactivityDao.findTactivity(null, null, userinfo.getSubChannel(), null,
								ActionJmsType.World_Cup_BigUser.value);
						if (wcbu != null) {
							WorldCupBigUser worldCupBigUser = WorldCupBigUser.findWorldCupBigUser(userno);
							if (worldCupBigUser != null) {
								logger.info("参与世界杯大户活动，不再参加此活动userno:" + userno);
								return;
							}
						}
						if (Tjmsservice.createTjmsservice(caselotId, ActionJmsType.Encash_Jingcai_2Chan1)) {
							logger.info(tactivity.getMemo() + prize);
							sendActivityPrizeJms.sendPrize2UserJMS(userno, prize, ActionJmsType.Encash_Jingcai_2Chan1,
									tactivity.getMemo(), caselotId, "", "");
						}
					}
				}
			}
		}
	}

	@Transactional
	public void addPrize2Chuan1(CaseLot caseLot, Torder torder, Tuserinfo userinfo) {
		Tactivity tactivity = tactivityDao.findTactivity(torder.getLotno(), torder.getPlaytype(),
				userinfo.getSubChannel(), null, ActionJmsType.Encash_2chuan1_AddPrize.value);
		if (tactivity != null) {
			String caselotId = caseLot.getId();
			Integer caselotprize = lotteryService.findCaseLotBuyAllPrizeamtById(caselotId, userinfo.getUserno());
			if (caselotprize > 0) {
				BigDecimal prize = BigDecimal.ZERO;
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer step = (Integer) activity.get("step");
				Integer prizeamt = (Integer) activity.get("prizeamt");
				int multiple = caselotprize / step;
				if (multiple > 0) {
					prize = new BigDecimal(multiple * prizeamt);
				}
				if (prize.compareTo(BigDecimal.ZERO) > 0) {
					Tactivity wcbu = tactivityDao.findTactivity(null, null, userinfo.getSubChannel(), null,
							ActionJmsType.World_Cup_BigUser.value);
					if (wcbu != null) {
						WorldCupBigUser worldCupBigUser = WorldCupBigUser.findWorldCupBigUser(userinfo.getUserno());
						if (worldCupBigUser != null) {
							logger.info("参与世界杯大户活动，不再参加此活动userno:" + userinfo.getUserno());
							return;
						}
					}
					if (Tjmsservice.createTjmsservice(caselotId, ActionJmsType.Encash_2chuan1_AddPrize)) {
						logger.info(tactivity.getMemo() + "合买中奖加奖prize:" + prize.longValue());
						sendActivityPrizeJms.sendPrize2UserJMS(userinfo.getUserno(), prize,
								ActionJmsType.Encash_2chuan1_AddPrize, tactivity.getMemo(), caselotId, "", "");
					}
				}
			}
		}
	}

	@Transactional
	public void addPrizeLanQiu(CaseLot caseLot, Torder torder, Tuserinfo userinfo) {
		if (!torder.getLotno().startsWith("J")) {
			return;
		}
		Tactivity tactivity = tactivityDao.findTactivity(torder.getLotno(), torder.getPlaytype(), userinfo.getSubChannel(), null,
				ActionJmsType.Encash_LanQiu_AddPrize.value);
		if (tactivity != null) {
			String caselotId = caseLot.getId();
			Integer caselotprize = lotteryService.findCaseLotBuyAllPrizeamtById(caselotId, userinfo.getUserno());
			if (caselotprize > 0) {
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
				if (caselotprize >= step1min && caselotprize <= step1max) {
					prize = new BigDecimal(step1prize);
				} else if (caselotprize >= step2min && caselotprize <= step2max) {
					prize = new BigDecimal(step2prize);
				} else if (caselotprize >= step3min && caselotprize <= step3max) {
					prize = new BigDecimal(step3prize);
				} else if (caselotprize >= step4min && caselotprize <= step4max) {
					prize = new BigDecimal(step4prize);
				} else if (caselotprize >= step5min && caselotprize <= step5max) {
					prize = new BigDecimal(step5prize);
				} else if (caselotprize >= step6min && caselotprize <= step6max) {
					prize = new BigDecimal(step6prize);
				} else if (caselotprize >= step7) {
					prize = new BigDecimal(step7prize);
				}
				if (prize.compareTo(BigDecimal.ZERO) > 0) {
					Tactivity wcbu = tactivityDao.findTactivity(null, null, userinfo.getSubChannel(), null,
							ActionJmsType.World_Cup_BigUser.value);
					if (wcbu != null) {
						WorldCupBigUser worldCupBigUser = WorldCupBigUser.findWorldCupBigUser(userinfo.getUserno());
						if (worldCupBigUser != null) {
							logger.info("参与世界杯大户活动，不再参加此活动userno:" + userinfo.getUserno());
							return;
						}
					}
					if (Tjmsservice.createTjmsservice(caselotId, ActionJmsType.Encash_LanQiu_AddPrize)) {
						logger.info(tactivity.getMemo() + "合买中奖加奖prize:" + prize.longValue());
						sendActivityPrizeJms.sendPrize2UserJMS(userinfo.getUserno(), prize,
								ActionJmsType.Encash_LanQiu_AddPrize, tactivity.getMemo(), caselotId, "", "");
					}
				}
			}
		}
	}
	
	@Transactional
	public void addPrizeJingcaiDanguan(CaseLot caseLot, Torder torder, Tuserinfo userinfo){
		if (!torder.getLotno().startsWith("J")) {
			return;
		}
		Tactivity tactivity = tactivityDao.findTactivity(torder.getLotno(), torder.getPlaytype(), userinfo.getSubChannel(), null,
				ActionJmsType.Encash_JingCaiDanGuan_AddPrize.value);
		if (tactivity != null) {
			String caselotId = caseLot.getId();
			Integer caselotprize = lotteryService.findCaseLotBuyAllPrizeamtById(caselotId, userinfo.getUserno());
			if (caselotprize > 0) {
				BigDecimal prize = BigDecimal.ZERO;
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer minprize = (Integer) activity.get("minprize");
				Integer percent = (Integer) activity.get("percent");
				Integer topprize = (Integer) activity.get("topprize");
				if (caselotprize >= minprize) {
					prize = new BigDecimal(caselotprize).multiply(new BigDecimal(percent)).divide(new BigDecimal(100));
					if (prize.compareTo(new BigDecimal(topprize)) > 0) {
						prize = new BigDecimal(topprize);
					}
					if (prize.compareTo(BigDecimal.ZERO) > 0) {
						if (Tjmsservice.createTjmsservice(caselotId, ActionJmsType.Encash_JingCaiDanGuan_AddPrize)) {
							logger.info(tactivity.getMemo() + "prize:" + prize.longValue());
							sendActivityPrizeJms.sendPrize2UserJMS(userinfo.getUserno(), prize, ActionJmsType.Encash_JingCaiDanGuan_AddPrize,
									tactivity.getMemo(), caselotId, "", "");
						}
					}
				} else {
					logger.info("中奖金额小于" + minprize + "不参与活动");
				}
			}
		}
	}
	
}
