package com.ruyicai.actioncenter.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.service.MemcachedService;

/**
 * 代理用户
 */
@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField = "", table = "TAGENT", identifierField = "id", finders = { "findTagentsByUsernoEquals" })
public class Tagent implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 用户编号 */
	@Column(name = "userno", nullable = false)
	private String userno;

	/** 创建时间 */
	@Column(name = "createTime", nullable = false)
	private Date createTime;

	/** 是否有效 */
	@Column(name = "state", nullable = false)
	private Integer state = 1;

	/** 代理ID */
	@Column(name = "agentId")
	private Long agentId;

	/** 代理级别 */
	@Column(name = "level")
	private Integer level = 1;

	@Autowired
	transient MemcachedService<Tagent> memcachedService;

	/**
	 * 保存或更新代理用户
	 * 
	 * @param userno
	 *            用户编号 required
	 * @param state
	 *            是否有效(默认有效),1有效,2失效 option
	 * @param agentId
	 *            上级代理ID option
	 * @param level
	 *            代理级别(默认1，需要更改时传递) option
	 * @return
	 */
	@Transactional
	public static Tagent saveOrUpdate(String userno, Integer state, Long agentId, Integer level) {
		if (StringUtils.isBlank(userno)) {
			throw new IllegalArgumentException("The userno argument is required");
		}
		List<Tagent> resultList = Tagent.findTagentsByUsernoEquals(userno).getResultList();
		Tagent agent = null;
		if (resultList != null && resultList.size() > 0) {
			agent = resultList.get(0);
			if (state != null) {
				agent.setState(state);
			}
			if (agentId != null) {
				agent.setAgentId(agentId);
			}
			if (level != null) {
				agent.setLevel(level);
			} else {
				if (agentId != null) {
					Tagent tagent = Tagent.findTagent(agentId);
					if (tagent != null && tagent.getLevel() != null) {
						agent.setLevel(tagent.getLevel() + 1);
					}
				}
			}
			agent.merge();
		} else {
			agent = new Tagent();
			agent.setUserno(userno);
			agent.setCreateTime(new Date());
			if (state != null) {
				agent.setState(state);
			}
			if (agentId != null) {
				agent.setAgentId(agentId);
			}
			if (level != null) {
				agent.setLevel(level);
			} else {
				if (agentId != null) {
					Tagent tagent = Tagent.findTagent(agentId);
					if (tagent != null && tagent.getLevel() != null) {
						agent.setLevel(tagent.getLevel() + 1);
					}
				}
			}
			agent.persist();
		}
		new Tagent().memcachedService.set("Tagent" + agent.getUserno(), agent);
		return agent;
	}

	public static Tagent findTagentByUserno(String userno) {
		Tagent agent = null;
		agent = new Tagent().memcachedService.get("Tagent" + userno);
		if (agent != null) {
			return agent;
		} else {
			List<Tagent> resultList = Tagent.findTagentsByUsernoEquals(userno).getResultList();
			if (resultList != null && resultList.size() > 0) {
				agent = resultList.get(0);
			}
		}
		if (agent != null) {
			new Tagent().memcachedService.checkToSet("Tagent" + agent.getUserno(), agent);
		}
		return agent;
	}

	public static Tagent createIfNotExists(String userno, Integer state, Long agentId, Integer level) {
		if (StringUtils.isBlank(userno)) {
			throw new IllegalArgumentException("The userno argument is required");
		}
		Tagent tagent = null;
		tagent = findTagentByUserno(userno);
		if (tagent == null) {
			tagent = saveOrUpdate(userno, null, null, null);
		}
		return tagent;
	}

}
