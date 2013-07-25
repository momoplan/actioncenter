package com.ruyicai.actioncenter.dao;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.domain.VipUser;
import com.ruyicai.actioncenter.domain.VipUserPK;

@Component
public class VipUserDao {

	@PersistenceContext
	private EntityManager entityManager;

	public VipUser findVipUser(String userno, String yearAndMonth, boolean lock) {
		VipUser vipUser = entityManager.find(VipUser.class, new VipUserPK(userno, yearAndMonth),
				lock ? LockModeType.PESSIMISTIC_WRITE : LockModeType.NONE);
		return vipUser;
	}

	@Transactional
	public VipUser createVipUser(String userno, String yearAndMonth) {
		VipUser vipUser = new VipUser();
		vipUser.setId(new VipUserPK(userno, yearAndMonth));
		vipUser.setBuyamt(BigDecimal.ZERO);
		vipUser.setModifyTime(new Date());
		entityManager.persist(vipUser);
		return vipUser;
	}

	@Transactional
	public VipUser merge(VipUser vipUser) {
		VipUser merged = this.entityManager.merge(vipUser);
		this.entityManager.flush();
		return merged;
	}
}
