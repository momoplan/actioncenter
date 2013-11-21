package com.ruyicai.actioncenter.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.controller.dto.SportsQuizVerifyDto;
import com.ruyicai.actioncenter.domain.SportsQuizPaper;
import com.ruyicai.actioncenter.domain.SportsQuizProperties;
import com.ruyicai.actioncenter.domain.SportsQuizUserLog;
import com.ruyicai.actioncenter.exception.RuyicaiException;
import com.ruyicai.actioncenter.util.DateUtil;
import com.ruyicai.actioncenter.util.ErrorCode;
import com.ruyicai.actioncenter.util.Page;

/**
 * 体育竞猜服务类
 * @author LiChenxing
 * @date 2013年11月14日 上午9:56:02
 */
@Service
public class SportsQuizService {
	
	private Logger logger = LoggerFactory.getLogger(SportsQuizService.class);
	
	@Autowired
	private AsyncService asyncService;
	
	@Transactional
	public void participate(String mobileid, Integer answerid) {
		Date now = new Date();
		if(now.after(DateUtil.parse("2013-11-23 18:50:00"))) {
			throw new RuyicaiException(ErrorCode.SportsQuiz_ActionEnd);
		}
		if(StringUtils.isBlank(mobileid)) {
			throw new IllegalArgumentException("The argument mobileid is required.");
		}
		if(answerid == null) {
			throw new IllegalArgumentException("The argument answerid is required.");
		}
		SportsQuizPaper sqp = SportsQuizPaper.findByMobileid(mobileid);
		if(sqp != null) {
			throw new RuyicaiException(ErrorCode.SportsQuiz_AlreadyParticipate);
		} else {
			sqp = new SportsQuizPaper();
			sqp.setMobileid(mobileid);
			sqp.setAnswerid(answerid);
			sqp.setCreatetime(new Date());
			sqp.setState(0);
			sqp.persist();
		}
		
		//注册发短信
		asyncService.sportsQuizRegister(mobileid);
	}
	
	@Transactional
	public void draw(Integer answerid) {
		if(answerid == null) {
			throw new IllegalArgumentException("The argument answerid is required.");
		}
		
		this.doSetAnswerid(answerid);
		
		String winMsg = "您成功命中国安VS恒大比赛结果，如意彩5元彩金相送，邀请您参与并体验更多精彩赛事，请下载如意彩客户端领取您的彩金。";
		String loseMsg = "您未命中国安vs恒大的比赛结果，不要气馁，如意彩送您3元彩金，邀请您参与并体验更多精彩赛事，请下载如意彩客户端领取您的彩金。";
		Page<SportsQuizPaper> page = new Page<SportsQuizPaper>(0, 100);
		do {
			SportsQuizPaper.findByPage(page);
			logger.info("体育竞猜开奖 listSize:" + page.getList().size() + " pageIndex:" + page.getPageIndex() + " maxResult:" + page.getMaxResult() + " currentPageno:" + page.getCurrentPageNo() + " totalPage:" + page.getTotalPage() + " totalResult:" + page.getTotalResult());
			List<SportsQuizPaper> paperList = page.getList();
			if(paperList != null && paperList.isEmpty() == false) {
				for(SportsQuizPaper sqp : paperList) {
					if(sqp.getAnswerid().equals(answerid)) {	//答对
						asyncService.asyncSendMoneyAndSMS(sqp.getMobileid(), new BigDecimal(500), winMsg);
					} else {//答错
						asyncService.asyncSendMoneyAndSMS(sqp.getMobileid(), new BigDecimal(300), loseMsg);
					}
					sqp.setState(1);
					sqp.merge();
				}
			}
		} while(page.getCurrentPageNo() < page.getTotalPage());
		
	}
	
	@Transactional
	public SportsQuizProperties pv() {
		SportsQuizProperties sqp = SportsQuizProperties.getSportsQuizPropertiesByKeyAndLock("pv");
		if(sqp == null) {
			sqp = new SportsQuizProperties();
			sqp.setSpkey("pv");
			sqp.setSpvalue("1");
			sqp.persist();
		} else {
			Long count = Long.valueOf(sqp.getSpvalue()) + 1;
			sqp.setSpvalue(count.toString());
			sqp.merge();
		}
		return sqp;
	}
	
	@Transactional
	private SportsQuizProperties doSetAnswerid(Integer answerid) {
		SportsQuizProperties sqp = SportsQuizProperties.getSportsQuizPropertiesByKeyAndLock("answerid");
		if(sqp == null) {
			sqp = new SportsQuizProperties();
			sqp.setSpkey("answerid");
			sqp.setSpvalue(answerid.toString());
			sqp.persist();
		} else {
			throw new RuyicaiException(ErrorCode.SportsQuiz_AnswerAlreadySet);
		}
		return sqp;
	}
	
	public SportsQuizProperties getAnswerid() {
		SportsQuizProperties sqp = SportsQuizProperties.getSportsQuizPropertiesByKey("answerid");
		if(sqp == null) {
			throw new RuyicaiException(ErrorCode.SportsQuiz_AnswerNotSet);
		} else {
			return sqp;
		}
	}
	
	public SportsQuizVerifyDto verify(String mobileid) {
		if(StringUtils.isBlank(mobileid)) {
			throw new IllegalArgumentException("The argument mobileid is required.");
		}
		SportsQuizPaper sqp = SportsQuizPaper.findByMobileid(mobileid);
		if(sqp == null) {
			throw new RuyicaiException(ErrorCode.SportsQuiz_NotParticipate);
		}
		SportsQuizUserLog squl = SportsQuizUserLog.findSportsQuizUserLog(mobileid);
		if(squl == null) {
			throw new RuyicaiException(ErrorCode.ERROR);
		}
		SportsQuizProperties sqProperties = SportsQuizProperties.getSportsQuizPropertiesByKey("answerid");
		if(sqProperties == null) {
			throw new RuyicaiException(ErrorCode.SportsQuiz_AnswerNotSet);
		}
		
		SportsQuizVerifyDto sqvd = new SportsQuizVerifyDto();
		sqvd.setUsername(mobileid);
		sqvd.setPassword(squl.getPassword());
		sqvd.setUserAnswerid(sqp.getAnswerid());
		sqvd.setCorrectAnswerid(Integer.valueOf(sqProperties.getSpvalue()));
		return sqvd;
	}
}
