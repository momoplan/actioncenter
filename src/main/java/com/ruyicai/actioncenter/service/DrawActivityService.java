package com.ruyicai.actioncenter.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.dao.DrawActivityDAO;
import com.ruyicai.actioncenter.domain.PrizeInfo;
import com.ruyicai.actioncenter.domain.UserDrawDetails;
import com.ruyicai.actioncenter.exception.RuyicaiException;
import com.ruyicai.actioncenter.util.ErrorCode;
import com.ruyicai.actioncenter.util.JsonUtil;
import com.ruyicai.actioncenter.util.RandomProbability;
import com.ruyicai.actioncenter.util.StringUtil;

/**
 * 抽奖活动.
 * @author hzf
 * @version 1.0v 2013-10-22
 */
@Service
public class DrawActivityService {

	private Logger logger = Logger.getLogger(DrawActivityService.class);

	private final static String TRY_DRAW_CODE = "0001";
	private final static int UPDATE_RESULT = 0;

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
		PrizeInfo returnPi = null;
		if(piList.size() > 0)
		{
			int piPosition = RandomProbability.getPrizeRandomPosition(piList);
			returnPi = piList.get(piPosition);
			try{
				processPrizeInfo(returnPi, userno, payObj, gainObj);
			}catch(Exception e)
			{
				if(TRY_DRAW_CODE.equals(e.getMessage())) // try again
				{
					logger.info("用户：" + userno + "于期次: " + activeTimes + " 重新获取奖品" );
					returnPi = getPrizeInfoByRandomProbability(activeTimes, userno, payObj, gainObj);
				}
			}
			logger.info("用户:"+ userno +"-->中奖信息为：奖品id=" + returnPi.getId() 
					+ ",奖品名称=" + returnPi.getName() + ",奖品等级=" + returnPi.getLevel()
					+ ",奖品剩余数量=" + returnPi.getRemainNum() + ",中奖时间=" + new Date());
		}else
		{
			logger.info("用户：" + userno + ", 本期:" + activeTimes + "已无奖品！");
		}

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
		if(result == UPDATE_RESULT)
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
	
	/**
	 * 编辑奖品信息
	 * @param jsonString
	 * @return
	 */
	@Transactional
	public String editPrizeInfo(String jsonString)
	{
		logger.info("update prize_info start: 得到参数：jsonString=" + jsonString);
		String errorCode = ErrorCode.OK.value;
		try {
			Map<String, Object> map = JsonUtil.transferJson2Map(jsonString);
			String id = map.containsKey("id") ? map.get("id").toString().trim() : "";
			String name = map.containsKey("name") ? map.get("name").toString().trim() : "";
			String level = map.containsKey("level") ? map.get("level").toString().trim()  : "";
			String sum = map.containsKey("sum") ? map.get("sum").toString().trim()  : "";
			String remain_num = map.containsKey("remain_num") ? map.get("remain_num").toString().trim()  : "";
			String arise_probability = map.containsKey("arise_probability") ? map.get("arise_probability").toString().trim()  : "";
			String delay_probability = map.containsKey("delay_probability") ? map.get("delay_probability").toString().trim()  : "";
			String start_date = map.containsKey("start_date") ? map.get("start_date").toString().trim()  : "";
			String end_date = map.containsKey("end_date") ? map.get("end_date").toString().trim()  : "";
			String active_times = map.containsKey("active_times") ? map.get("active_times").toString().trim()  : "";
			String valid = map.containsKey("valid") ? map.get("valid").toString().trim()  : "";
			String editType = map.containsKey("editType") ? map.get("editType").toString().trim()  : ""; // 编辑类型

			if (StringUtil.isEmpty(editType) || StringUtil.isEmpty(name) || StringUtil.isEmpty(level)) {
				errorCode = ErrorCode.PARAMTER_ERROR.value;
			} else if(!"add".equals(editType) && StringUtil.isEmpty(id))
			{
				errorCode = ErrorCode.PARAMTER_ERROR.value;
			}else {
				PrizeInfo pi = new PrizeInfo();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				pi.setName(name);
				pi.setLevel(level);
				pi.setSum(Integer.parseInt(sum));
				pi.setRemainNum(Integer.parseInt(remain_num));
				pi.setAriseProbability(Integer.parseInt(arise_probability));
				pi.setDelayProbability(delay_probability);
				if(!"".equals(start_date))
				{
					pi.setStartDate(sdf.parse(start_date));
				}
				if(!"".equals(end_date))
				{
					pi.setEndDate(sdf.parse(end_date));
				}
				pi.setActiveTimes(active_times);
				pi.setValid(valid);

				if("add".equals(editType))
				{
					drawActivityDAO.createPrizeInfo(pi);
				}else if("update".equals(editType))
				{
					pi.setId(Integer.parseInt(id));
					drawActivityDAO.merge(pi);
				}
			}
		} catch(Exception e) {
			errorCode = ErrorCode.ERROR.value;	
			logger.error("update prize_info error, ", e);
		}
		logger.info("update prize_info end");
		return errorCode;
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
