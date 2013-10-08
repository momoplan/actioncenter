package com.ruyicai.actioncenter.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.domain.PrizeInfo;
import com.ruyicai.actioncenter.domain.UserDrawDetails;

@Component
public class DrawActivityDAO {

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * 获取奖品列表信息
	 * @param activeTimes 活动期次
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<PrizeInfo> findPrizeInfoList(String activeTimes) {
		String sql = "from PrizeInfo o where o.remainNum != '0' and o.activeTimes = ? order by o.ariseProbability asc";
		Query q = entityManager.createQuery(sql, PrizeInfo.class);
		q.setParameter(1, activeTimes);
		List<PrizeInfo> returnList = q.getResultList();
		return returnList;
	}

	/**
	 * 获取奖品信息
	 * @param id 奖品id
	 * @return
	 */
	public PrizeInfo findPrizeInfoById(Integer id) {
		PrizeInfo prizeInfo = entityManager.find(PrizeInfo.class, id);
		return prizeInfo;
	}

	/**
	 * 更新奖品信息.
	 * @param pi
	 * @return
	 */
	@Transactional
	public PrizeInfo merge(PrizeInfo pi) {
		PrizeInfo merged = this.entityManager.merge(pi);
		this.entityManager.flush();
		return merged;
	}

	/**
	 * 更新奖品信息.
	 * @param id
	 * @return
	 */
	@Transactional
	public int updatePrizeInfo(int id)
	{
		String sql = "update prize_info p set p.remain_num = (p.remain_num - 1)  where (p.remain_num -1)  >= 0 and p.id = ?";
		Query q = this.entityManager.createNativeQuery(sql);
		q.setParameter(1, id);
		return q.executeUpdate();
	}

	// ----------------------------user draw details
	/**
	 * 记录用户抽奖信息
	 * @param userMap
	 * @return
	 */
	@Transactional
	public UserDrawDetails createUserDraw(UserDrawDetails userDraw) {
		entityManager.persist(userDraw);
		return userDraw;
	}

	/**
	 * 获取用户抽奖信息列表
	 * @param userno
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<UserDrawDetails> findUserDrawList(String userno) {
		String sql = "from UserDrawDetails o where o.userno = ? order by o.id desc";
		Query q = entityManager.createQuery(sql, UserDrawDetails.class);
		q.setParameter(1, userno);
		List<UserDrawDetails> returnList = q.getResultList();
		return returnList;
	}

}
