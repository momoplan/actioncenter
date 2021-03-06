// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.actioncenter.domain;

import com.ruyicai.actioncenter.domain.YinLianNewUser;
import java.lang.String;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import org.springframework.transaction.annotation.Transactional;

privileged aspect YinLianNewUser_Roo_Entity {
    
    declare @type: YinLianNewUser: @Entity;
    
    declare @type: YinLianNewUser: @Table(name = "YinLianNewUser");
    
    @PersistenceContext
    transient EntityManager YinLianNewUser.entityManager;
    
    @Transactional
    public void YinLianNewUser.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void YinLianNewUser.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            YinLianNewUser attached = YinLianNewUser.findYinLianNewUser(this.userno);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void YinLianNewUser.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void YinLianNewUser.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public YinLianNewUser YinLianNewUser.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        YinLianNewUser merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
    public static final EntityManager YinLianNewUser.entityManager() {
        EntityManager em = new YinLianNewUser().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long YinLianNewUser.countYinLianNewUsers() {
        return entityManager().createQuery("SELECT COUNT(o) FROM YinLianNewUser o", Long.class).getSingleResult();
    }
    
    public static List<YinLianNewUser> YinLianNewUser.findAllYinLianNewUsers() {
        return entityManager().createQuery("SELECT o FROM YinLianNewUser o", YinLianNewUser.class).getResultList();
    }
    
    public static YinLianNewUser YinLianNewUser.findYinLianNewUser(String userno) {
        if (userno == null || userno.length() == 0) return null;
        return entityManager().find(YinLianNewUser.class, userno);
    }
    
    public static List<YinLianNewUser> YinLianNewUser.findYinLianNewUserEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM YinLianNewUser o", YinLianNewUser.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
