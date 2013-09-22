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
	
	@Column(name = "REMAININGTIMES")
	private Integer remainingtimes;
	
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
