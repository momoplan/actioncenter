package com.ruyicai.actioncenter.dao;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.actioncenter.domain.Fund2Draw;

@Component
public class Fund2DrawDao {

	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public void persist(Fund2Draw draw) {
		this.entityManager.persist(draw);
	}

	@Transactional
	public void remove(Fund2Draw draw) {
		if (this.entityManager.contains(draw)) {
			this.entityManager.remove(draw);
		} else {
			Fund2Draw attached = this.findFund2Draw(draw.getTtransactionid());
			this.entityManager.remove(attached);
		}
	}

	@Transactional
	public void flush() {
		this.entityManager.flush();
	}

	@Transactional
	public void clear() {
		this.entityManager.clear();
	}

	@Transactional
	public Fund2Draw merge(Fund2Draw draw) {
		Fund2Draw merged = this.entityManager.merge(draw);
		this.entityManager.flush();
		return merged;
	}

	public Fund2Draw createFund2Draw(String ttransactionid, String userno, BigDecimal amt) {
		Fund2Draw draw = new Fund2Draw();
		draw.setTtransactionid(ttransactionid);
		draw.setUserno(userno);
		draw.setAmt(amt);
		draw.setCreateTime(new Date());
		draw.setState(0);
		this.persist(draw);
		return draw;
	}

	public Fund2Draw findFund2Draw(String ttransactionid, boolean lock) {
		Fund2Draw draw = this.entityManager.find(Fund2Draw.class, ttransactionid, lock ? LockModeType.PESSIMISTIC_WRITE
				: LockModeType.NONE);
		return draw;
	}

	public Fund2Draw findFund2Draw(String ttransactionid) {
		return findFund2Draw(ttransactionid, false);
	}

	@Transactional
	public Fund2Draw updateState(String ttransactionid, int state) {
		Fund2Draw draw = findFund2Draw(ttransactionid, true);
		if (draw != null) {
			draw.setState(state);
			if (state == 1) {
				draw.setDrawTime(new Date());
			}
			this.merge(draw);
		}
		return draw;
	}
}
