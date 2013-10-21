/**
 *
 * LiChenxing
 * 2013-9-18 下午3:02:58
 */
package com.ruyicai.actioncenter.domain;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.LockModeType;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * 用户可投票次数
 * @author LiChenxing
 * @date 2013-9-18 下午3:02:58
 */
@RooJavaBean
@RooJson
@RooToString
@RooEntity(versionField = "", table = "USEREXPERIENCEAVAILABLEVOTETIMES", identifierField = "userno")
public class UserExperienceAvailableVoteTimes {

	/**
	 * 用户id
	 */
	@Id
	@Column(name = "USERNO", length = 20)
	private String userno;
	
	/**
	 * 可投票次数
	 */
	@Column(name = "REMAININGTIMES")
	private Integer remainingtimes;
	
	/**
	 * 是否下载过客户端
	 * 0否1是
	 */
	@Column(name = "DOWNLOADAPP")
	private Integer downloadapp;
	
	/**
	 * 是否分享过微博
	 * 0否1是
	 */
	@Column(name = "WEIBO")
	private Integer weibo;
	
	/**
	 * 查找用户可投票次数
	 * @param userno	用户id
	 * @return	UserExperienceAvailableVoteTimes 查询或创建出来的实体
	 */
	public static UserExperienceAvailableVoteTimes findUserExperienceAvailableVoteTimes(String userno, boolean lock) {
		UserExperienceAvailableVoteTimes ueavt = entityManager().find(UserExperienceAvailableVoteTimes.class, userno, lock ? LockModeType.PESSIMISTIC_WRITE : LockModeType.NONE);
		return ueavt;
	}
}
