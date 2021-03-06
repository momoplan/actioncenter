package com.ruyicai.actioncenter.service;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.domain.UserExperience;
import com.ruyicai.actioncenter.domain.UserExperienceAvailableVoteTimes;
import com.ruyicai.actioncenter.domain.UserExperienceMobile;
import com.ruyicai.actioncenter.domain.UserExperienceVoteLog;
import com.ruyicai.actioncenter.exception.RuyicaiException;
import com.ruyicai.actioncenter.util.ErrorCode;
import com.ruyicai.actioncenter.util.HtmlFilter;
import com.ruyicai.lottery.domain.Tuserinfo;

@Service
public class UserExperienceService {
	
	private Logger logger = LoggerFactory.getLogger(UserExperienceService.class);
	
	@Autowired
	private LotteryService lotteryService;
	
	/**
	 * 用户招募方法
	 * @return	创建成功的UserExperience对象
	 * @throws	RuyicaiException
	 */
	@Transactional
	public UserExperience recruit(UserExperience userExperience) throws RuyicaiException {
		if(StringUtils.isBlank(userExperience.getUserno())) {
			throw new IllegalArgumentException("the argument userno is required");
		} else if(StringUtils.isBlank(userExperience.getName())) {
			throw new IllegalArgumentException("the argument name is required");
		} else if(userExperience.getAge() == null) {
			throw new IllegalArgumentException("the argument age is required");
		} else if(StringUtils.isBlank(userExperience.getContact())) {
			throw new IllegalArgumentException("the argument contact is required");
		} else if(userExperience.getQuestion1() == null) {
			throw new IllegalArgumentException("the argument question1 is required");
		}  else if(userExperience.getQuestion2() == null) {
			throw new IllegalArgumentException("the argument question2 is required");
		} else if(StringUtils.isBlank(userExperience.getQuestion3())) {
			throw new IllegalArgumentException("the argument question3 is required");
		} else if(StringUtils.isBlank(userExperience.getQuestion4())) {
			throw new IllegalArgumentException("the argument question4 is required");
		} else if(StringUtils.isBlank(userExperience.getQuestion5())) {
			throw new IllegalArgumentException("the argument question5 is required");
		} 
		
		UserExperience result = UserExperience.findUserExperience(userExperience.getUserno());
		if(result != null) {
			throw new RuyicaiException(ErrorCode.UserExperience_UsernoAlreadyRecruit);
		}
		
		doRecruit(userExperience);
		
		return userExperience;
	}
	
	/**
	 * 创建用户招募信息
	 */
	private void doRecruit(UserExperience userExperience) {
		userExperience.setQuestion3(HtmlFilter.Html2Text(userExperience.getQuestion3()));
		userExperience.setQuestion4(HtmlFilter.Html2Text(userExperience.getQuestion4()));
		userExperience.setQuestion5(HtmlFilter.Html2Text(userExperience.getQuestion5()));
		UserExperience.createUserExperience(userExperience);
	}
	
	/**
	 * 投票方法
	 * @param voteruserno	投票者用户id
	 * @param userno			被投票者用户id
	 * @return	UserExperienceVoteLog 创建的用户投票记录
	 * @throws RuyicaiException
	 */
	@Transactional
	public UserExperience vote(String voteruserno, String userno) throws RuyicaiException {
		if(StringUtils.isBlank(voteruserno)) {
			throw new IllegalArgumentException("the argument voteruserno is required");
		} else if(StringUtils.isBlank(userno)) {
			throw new IllegalArgumentException("the argument userno is required");
		}
		
		Tuserinfo userinfo = lotteryService.findTuserinfoByUserno(voteruserno);
		if(userinfo == null) {
			throw new RuyicaiException(ErrorCode.UserMod_UserNotExists);
		}
		
		String mobileid = userinfo.getMobileid();
		if(StringUtils.isBlank(mobileid)) {
			throw new RuyicaiException(ErrorCode.UserMod_MobileidNotBind);
		}
		
		UserExperienceMobile uem = UserExperienceMobile.findUserExperienceMobile(mobileid);
		if(uem == null) {
			logger.info("手机号：" + mobileid + " 用户编号：" + voteruserno + " 第一次投票");
			uem = UserExperienceMobile.createUserExperienceMobile(mobileid, voteruserno);
		} else {
			String logUserno = uem.getUserno();
			if(logUserno.equals(voteruserno) == false) {
				throw new RuyicaiException(ErrorCode.UserExperience_MobileidNotMathUserno);
			}
		}
		
		UserExperienceAvailableVoteTimes ueavt = UserExperienceAvailableVoteTimes.findUserExperienceAvailableVoteTimes(voteruserno, true);
		if(ueavt == null) {
			ueavt = initUserAvailableVoteTimes(voteruserno);
		}
		
		if(ueavt.getRemainingtimes() < 1) {
			throw new RuyicaiException(ErrorCode.UserExperience_UsernoNotAvailableVote);
		}
		
		UserExperience userExperience = UserExperience.findUserExperience(userno);
		if(userExperience == null) {
			throw new RuyicaiException(ErrorCode.UserExperience_UsernoNotRecruit);
		}
		if(userExperience.getSelected() != 1) {
			throw new RuyicaiException(ErrorCode.UserExperience_UsernoNoSelected);
		}
		
		UserExperience userExperienceVoted = doVote(voteruserno, userno, ueavt);
		
		
		return userExperienceVoted;
	}
	
	/**
	 * 进行投票
	 * @param voteruserno	投票者用户id
	 * @param userno			被投票者用户id
	 * @return
	 */
	private UserExperience doVote(String voteruserno, String userno, UserExperienceAvailableVoteTimes ueavt) {
		//创建用户投票记录
		UserExperienceVoteLog.create(voteruserno, userno);
		//更新投票数
		UserExperience userExperience = UserExperience.findUserExperience(userno, true);
		userExperience.setVotes(userExperience.getVotes() + 1);
		userExperience.persist();
		//减少用户可投票次数
		ueavt.setRemainingtimes(ueavt.getRemainingtimes() - 1);
		ueavt.persist();
		return userExperience;
	}
	
	/**
	 * 初始化用户的可投票次数
	 * @param userno
	 * @return
	 */
	private UserExperienceAvailableVoteTimes initUserAvailableVoteTimes(String userno) {
		UserExperienceAvailableVoteTimes uevat = new UserExperienceAvailableVoteTimes();
		uevat.setUserno(userno);
		uevat.setRemainingtimes(1);
		uevat.setDownloadapp(0);
		uevat.setWeibo(0);
		uevat.persist();
		return uevat;
	}
	
	/**
	 * 增加用户可投票次数
	 * @param userno	用户id
	 * @param type		类型 0普通，1下载客户端，2微博分享
	 * @return UserExperienceAvailableVoteTimes
	 */
	@Transactional
	public UserExperienceAvailableVoteTimes addAvailableVoteTimes(String userno, Integer type) {
		if(StringUtils.isEmpty(userno)) {
			throw new IllegalArgumentException("the argument userno is required");
		} else if(type == null) {
			throw new IllegalArgumentException("the arguemtn type is required.");
		}
		
		UserExperienceAvailableVoteTimes ueavt = UserExperienceAvailableVoteTimes.findUserExperienceAvailableVoteTimes(userno, true);
		if(ueavt == null) {
			ueavt =initUserAvailableVoteTimes(userno);
			if(type.equals(1)) {
				ueavt.setDownloadapp(1);
			} else if(type.equals(2)) {
				ueavt.setWeibo(1);
			}
			ueavt.setRemainingtimes(ueavt.getRemainingtimes() + 1);
		} else {
			if(type.equals(1)) {
				if(ueavt.getDownloadapp().equals(1)) {
					throw new RuyicaiException(ErrorCode.UserExperience_AlreadyDownloadApp);
				} else {
					ueavt.setDownloadapp(1);
				}
			} else if(type.equals(2)) {
				if(ueavt.getWeibo().equals(1)) {
					throw new RuyicaiException(ErrorCode.UserExperience_ArreadyWeibo);
				} else {
					ueavt.setWeibo(1);
				}
			}
			ueavt.setRemainingtimes(ueavt.getRemainingtimes() + 1);
		}
		
		ueavt.merge();
		
		return ueavt;
	}
	
	@Transactional
	public void addAvailableVoteTimesByBuyAMT(String userno, Integer times) {
		if(StringUtils.isBlank(userno)) {
			throw new IllegalArgumentException("The argument userno is required.");
		}
		if(times == null) {
			throw new IllegalArgumentException("The argument times is required.");
		}
		logger.info("用户体验官增加投票次数 userno:{} times:{}", new String[] {userno, times+""});
		UserExperienceAvailableVoteTimes ueavt = UserExperienceAvailableVoteTimes.findUserExperienceAvailableVoteTimes(userno, true);
		if(ueavt == null) {
			ueavt =initUserAvailableVoteTimes(userno);
		}
		ueavt.setRemainingtimes(ueavt.getRemainingtimes() + times);
		ueavt.merge();
	}
	
	
}
