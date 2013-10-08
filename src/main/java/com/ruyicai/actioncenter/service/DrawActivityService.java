package com.ruyicai.actioncenter.service;

import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.dao.DrawActivityDAO;
import com.ruyicai.actioncenter.domain.PrizeInfo;
import com.ruyicai.actioncenter.domain.UserDrawDetails;
import com.ruyicai.actioncenter.exception.RuyicaiException;
import com.ruyicai.actioncenter.util.RandomProbability;

/**
 * 抽奖活动.
 * @author ryc
 *
 */
@Service
public class DrawActivityService {

	private Logger logger = Logger.getLogger(DrawActivityService.class);

	private final static String TRY_DRAW_CODE = "0001";

	@Autowired
	DrawActivityDAO drawActivityDAO;

	/**
	 * 根据概率随机获取奖品信息.
	 * 
	 * @param activeTimes 活动期次
	 * @return
	 */
	public PrizeInfo getPrizeInfoByRandomProbability(String activeTimes, String userno, String payObj, String gainObj)
	{
		List<PrizeInfo> piList = queryPrizeConfigList(activeTimes);
		int piPosition = RandomProbability.getPrizeRandomPosition(piList);
		PrizeInfo returnPi = piList.get(piPosition);
		try{
			processPrizeInfo(returnPi, userno, payObj, gainObj);
		}catch(Exception e)
		{
			if(TRY_DRAW_CODE.equals(e.getMessage())) // try again
			{
				returnPi = getPrizeInfoByRandomProbability(activeTimes, userno, payObj, gainObj);
			}
		}
		logger.info("用户:"+ userno +"-->中奖信息为：奖品id=" + returnPi.getId() 
				+ ",奖品名称=" + returnPi.getName() + ",奖品等级=" + returnPi.getLevel()
				+ ",奖品剩余数量=" + returnPi.getRemainNum() + ",中奖时间=" + new Date());

		return returnPi;
	}

	/**
	 * 获取奖品列表信息.
	 * 
	 * @param activeTimes 活动期次
	 * @return
	 */
	public List<PrizeInfo> queryPrizeConfigList(String activeTimes)
	{	
		return drawActivityDAO.findPrizeInfoList(activeTimes);
	}

	/**
	 * 更新奖品信息</br>
	 * 记录用户中奖信息.
	 * 
	 * @param pi
	 * @param userno
	 * @param payObj
	 * @param gainObj
	 */
	@Transactional
	public void processPrizeInfo(PrizeInfo pi, String userno, String payObj, String gainObj){

		// 更新奖品信息
		int result = drawActivityDAO.updatePrizeInfo(pi.getId());
		if(result == 0)
		{
			throw new RuyicaiException(TRY_DRAW_CODE);
		}

		// 记录用户中奖信息
		addUserDrawInfo(pi, userno, payObj, gainObj);

		// ------------------
		// 相关业务逻辑
		// 异步处理
		// ------------------

	}
	
	// ---------user draw details service
	/**
	 * 增加用户中奖信息
	 * @param pi
	 * @param userno
	 * @param payObj
	 * @param gainObj
	 */
	@Transactional
	public void addUserDrawInfo(PrizeInfo pi, String userno, String payObj, String gainObj)
	{
		UserDrawDetails ud = new UserDrawDetails();
		ud.setUserno(userno);
		ud.setPrizeId(pi.getId());
		ud.setPayObject(payObj);
		ud.setGainObject(gainObj);
		ud.setDrawDate(new Date());
		drawActivityDAO.createUserDraw(ud);
	}

	/**
	 * 获取用户抽奖信息列表.
	 * @param userno
	 * @return
	 */
	public List<UserDrawDetails> queryUserDrawList(String userno)
	{
		return drawActivityDAO.findUserDrawList(userno);
	}

}
