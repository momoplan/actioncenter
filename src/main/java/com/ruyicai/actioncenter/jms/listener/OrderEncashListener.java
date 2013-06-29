package com.ruyicai.actioncenter.jms.listener;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Body;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.consts.ActionJmsType;
import com.ruyicai.actioncenter.dao.TuserPrizeDetailDao;
import com.ruyicai.actioncenter.domain.SSCPrizedDetail;
import com.ruyicai.actioncenter.domain.SSCPrizedDetailPK;
import com.ruyicai.actioncenter.domain.Tactivity;
import com.ruyicai.actioncenter.domain.Tjmsservice;
import com.ruyicai.actioncenter.domain.TuserPrizeDetail;
import com.ruyicai.actioncenter.service.LotteryService;
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
	private TuserPrizeDetailDao tuserPrizeDetailDao;

	@Produce(uri = "jms:topic:sendActivityPrize")
	private ProducerTemplate sendActivityPrizeProducer;

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
		addPrizeSSC(order, orderUserInfo);
		addPrize2Chuan1(order, orderUserInfo);
		addPrize11Xuan5(order, orderUserInfo);
		addPrizeDaXiaoDanShuang(order, orderUserInfo);
		addPrizeZuCai(order, orderUserInfo);
		addPrizeLanQiu(order, orderUserInfo);
		addPrizeJingcai2chuan1(order, orderUserInfo);
		addPrizeKuai3(order, orderUserInfo);
		addPrize3D(order, orderUserInfo);
	}

	@Transactional
	public void addPrize3D(Torder order, Tuserinfo orderUserInfo) {
		String userno = orderUserInfo.getUserno();
		if (userno.equals(ruyicaiUserno)) {
			logger.info("如意彩账户购买,不加奖");
			return;
		}
		Tuserinfo tuserinfo = orderUserInfo;
		Tactivity tactivity = Tactivity.findTactivity(order.getLotno(), order.getPlaytype(), tuserinfo.getSubChannel(),
				null, ActionJmsType.FuCai3D_JiaJiang.value);
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
						sendPrize2UserJMS(userno, prize, ActionJmsType.FuCai3D_JiaJiang, order.getId(),
								tactivity.getMemo());
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
		Tactivity tactivity = Tactivity.findTactivity(order.getLotno(), null, tuserinfo.getSubChannel(), null,
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
							sendPrize2UserJMS(userno, prize, ActionJmsType.Kuai3_JiaJiang, order.getId(),
									tactivity.getMemo());
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
		Long orderprizeamt = 0L;// order.getOrderprizeamt().longValue();
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
		Tactivity tactivity = Tactivity.findTactivity(null, order.getPlaytype(), tuserinfo.getSubChannel(), null,
				ActionJmsType.Encash_Jingcai_2Chan1.value);
		if (tactivity != null) {
			if (orderprizeamt > 0) {
				BigDecimal prize = BigDecimal.ZERO;
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer step1 = (Integer) activity.get("step1");
				Integer step1prize = (Integer) activity.get("step1prize");
				Integer step2 = (Integer) activity.get("step2");
				Integer step2prize = (Integer) activity.get("step2prize");
				Integer step3 = (Integer) activity.get("step3");
				Integer step3prize = (Integer) activity.get("step3prize");
				Integer step4 = (Integer) activity.get("step4");
				Integer step4prize = (Integer) activity.get("step4prize");
				Integer step5 = (Integer) activity.get("step5");
				Integer step5prize = (Integer) activity.get("step5prize");
				Integer step6 = (Integer) activity.get("step6");
				Integer step6prize = (Integer) activity.get("step6prize");
				if (orderprizeamt >= step1 && orderprizeamt < step2) {
					prize = new BigDecimal(step1prize);
				} else if (orderprizeamt >= step2 && orderprizeamt < step3) {
					prize = new BigDecimal(step2prize);
				} else if (orderprizeamt >= step3 && orderprizeamt < step4) {
					prize = new BigDecimal(step3prize);
				} else if (orderprizeamt >= step4 && orderprizeamt < step5) {
					prize = new BigDecimal(step4prize);
				} else if (orderprizeamt >= step5 && orderprizeamt < step6) {
					prize = new BigDecimal(step5prize);
				} else if (orderprizeamt >= step6) {
					prize = new BigDecimal(step6prize);
				}
				if (prize.compareTo(BigDecimal.ZERO) > 0) {
					if (Tjmsservice.createTjmsservice(order.getId(), ActionJmsType.Encash_Jingcai_2Chan1)) {
						logger.info(tactivity.getMemo() + prize);
						sendPrize2UserJMS(userno, prize, ActionJmsType.Encash_Jingcai_2Chan1, order.getId(),
								tactivity.getMemo());
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
		Tactivity tactivity = Tactivity.findTactivity(order.getLotno(), null, tuserinfo.getSubChannel(), null,
				ActionJmsType.Encash_LanQiu_AddPrize.value);
		if (tactivity != null) {
			// Long orderprizeamt = order.getOrderprizeamt().longValue();
			if (orderprizeamt > 0) {
				BigDecimal prize = BigDecimal.ZERO;
				String express = tactivity.getExpress();
				Map<String, Object> activity = JsonUtil.transferJson2Map(express);
				Integer step1min = (Integer) activity.get("step1min");
				Integer step1max = (Integer) activity.get("step1max");
				Integer step1prizeamt = (Integer) activity.get("step1prizeamt");
				if (orderprizeamt >= step1min && orderprizeamt <= step1max) {
					prize = new BigDecimal(step1prizeamt);
				} else {
					Integer step = (Integer) activity.get("step");
					Integer prizeamt = (Integer) activity.get("prizeamt");
					int multiple = orderprizeamt.intValue() / step;
					if (multiple > 0) {
						prize = new BigDecimal(multiple * prizeamt);
					}
				}
				if (prize.compareTo(BigDecimal.ZERO) > 0) {
					if (Tjmsservice.createTjmsservice(order.getId(), ActionJmsType.Encash_LanQiu_AddPrize)) {
						logger.info(tactivity.getMemo() + "prize:" + prize.longValue());
						sendPrize2UserJMS(userno, prize, ActionJmsType.Encash_LanQiu_AddPrize, order.getId(),
								tactivity.getMemo());
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
		Tactivity tactivity = Tactivity.findTactivity(order.getLotno(), null, tuserinfo.getSubChannel(), null,
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
							sendPrize2UserJMS(userno, prize, ActionJmsType.Encash_ZuCai_AddPrize, order.getId(),
									tactivity.getMemo());
						}
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
		Tactivity tactivity = Tactivity.findTactivity(order.getLotno(), order.getPlaytype(), tuserinfo.getSubChannel(),
				null, ActionJmsType.Encash_DaXiaoDanShuang_AddPrize.value);
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
						sendPrize2UserJMS(userno, prize, ActionJmsType.Encash_DaXiaoDanShuang_AddPrize, order.getId(),
								tactivity.getMemo());
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
		Tactivity tactivity = Tactivity.findTactivity(order.getLotno(), null, tuserinfo.getSubChannel(), null,
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
				// 如果用户当天赠送金额大于600元。则不再赠送
				String day = DateUtil.format("yyyy-MM-dd", new Date());
				SSCPrizedDetail detail = SSCPrizedDetail.findSSCPrizedDetail(new SSCPrizedDetailPK(userno, day));
				if (detail != null && detail.getTotalPrizeAmt() != null) {
					if (detail.getTotalPrizeAmt().compareTo(new BigDecimal(60000)) > 0) {
						logger.info("赠送超过600元，不再赠送.userno:{},day:{},totalPrizeAmt:{}", new String[] { userno, day,
								detail.getTotalPrizeAmt() + "" });
						return;
					}
				}
				if (prize.compareTo(BigDecimal.ZERO) > 0) {
					if (Tjmsservice.createTjmsservice(order.getId(), ActionJmsType.Encash_DuoLeCai_AddPrize)) {
						logger.info(tactivity.getMemo() + "中奖加奖prize:" + prize.longValue());
						sendPrize2UserJMS(userno, prize, ActionJmsType.Encash_DuoLeCai_AddPrize, order.getId(),
								tactivity.getMemo());
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
		Tactivity tactivity = Tactivity.findTactivity(order.getLotno(), order.getPlaytype(), tuserinfo.getSubChannel(),
				null, ActionJmsType.Encash_2chuan1_AddPrize.value);
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
					if (Tjmsservice.createTjmsservice(order.getId(), ActionJmsType.Encash_2chuan1_AddPrize)) {
						logger.info(tactivity.getMemo() + "加奖prize:" + prize.longValue());
						sendPrize2UserJMS(userno, prize, ActionJmsType.Encash_2chuan1_AddPrize, order.getId(),
								tactivity.getMemo());
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
		Tactivity tactivity = Tactivity.findTactivity(order.getLotno(), null, tuserinfo.getSubChannel(), null,
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
						sendPrize2UserJMS(userno, prize, ActionJmsType.EncashAddPrize, order.getId(),
								tactivity.getMemo());
					}
				}
			}
		}
	}

	@Transactional
	public void sendPrize2UserJMS(String userno, BigDecimal amt, ActionJmsType actionJmsType, String businessId,
			String memo) {
		TuserPrizeDetail userPrizeDetail = tuserPrizeDetailDao.createTprizeUserBuyLog(userno, amt, actionJmsType,
				businessId);
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("prizeDetailId", userPrizeDetail.getId());
		headers.put("actionJmsType", actionJmsType.value);
		headers.put("memo", memo);
		logger.info("发送派奖JMS.TuserPrizeDetailId:" + userPrizeDetail.getId());
		sendActivityPrizeProducer.sendBodyAndHeaders(null, headers);
	}

}
