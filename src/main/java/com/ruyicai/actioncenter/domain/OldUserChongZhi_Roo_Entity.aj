// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.actioncenter.domain;

import com.ruyicai.actioncenter.domain.OldUserChongZhi;
import java.lang.String;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import org.springframework.transaction.annotation.Transactional;

privileged aspect OldUserChongZhi_Roo_Entity {
    
    declare @type: OldUserChongZhi: @Entity;
    
    declare @type: OldUserChongZhi: @Table(name = "OldUserChongZhi");
    
    @PersistenceContext
    transient EntityManager OldUserChongZhi.entityManager;
    
    @Transactional
    public void OldUserChongZhi.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void OldUserChongZhi.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            OldUserChongZhi attached = OldUserChongZhi.findOldUserChongZhi(this.userno);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void OldUserChongZhi.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void OldUserChongZhi.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public OldUserChongZhi OldUserChongZhi.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        OldUserChongZhi merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
    public static final EntityManager OldUserChongZhi.entityManager() {
        EntityManager em = new OldUserChongZhi().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long OldUserChongZhi.countOldUserChongZhis() {
        return entityManager().createQuery("SELECT COUNT(o) FROM OldUserChongZhi o", Long.class).getSingleResult();
    }
    
    public static List<OldUserChongZhi> OldUserChongZhi.findAllOldUserChongZhis() {
        return entityManager().createQuery("SELECT o FROM OldUserChongZhi o", OldUserChongZhi.class).getResultList();
    }
    
    public static OldUserChongZhi OldUserChongZhi.findOldUserChongZhi(String userno) {
        if (userno == null || userno.length() == 0) return null;
        return entityManager().find(OldUserChongZhi.class, userno);
    }
    
    public static List<OldUserChongZhi> OldUserChongZhi.findOldUserChongZhiEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM OldUserChongZhi o", OldUserChongZhi.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
