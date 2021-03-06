// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.actioncenter.domain;

import com.ruyicai.actioncenter.domain.UserExperienceAvailableVoteTimes;
import java.lang.String;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import org.springframework.transaction.annotation.Transactional;

privileged aspect UserExperienceAvailableVoteTimes_Roo_Entity {
    
    declare @type: UserExperienceAvailableVoteTimes: @Entity;
    
    declare @type: UserExperienceAvailableVoteTimes: @Table(name = "USEREXPERIENCEAVAILABLEVOTETIMES");
    
    @PersistenceContext
    transient EntityManager UserExperienceAvailableVoteTimes.entityManager;
    
    @Transactional
    public void UserExperienceAvailableVoteTimes.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional
    public void UserExperienceAvailableVoteTimes.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            UserExperienceAvailableVoteTimes attached = UserExperienceAvailableVoteTimes.findUserExperienceAvailableVoteTimes(this.userno);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional
    public void UserExperienceAvailableVoteTimes.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional
    public void UserExperienceAvailableVoteTimes.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional
    public UserExperienceAvailableVoteTimes UserExperienceAvailableVoteTimes.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        UserExperienceAvailableVoteTimes merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
    public static final EntityManager UserExperienceAvailableVoteTimes.entityManager() {
        EntityManager em = new UserExperienceAvailableVoteTimes().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long UserExperienceAvailableVoteTimes.countUserExperienceAvailableVoteTimeses() {
        return entityManager().createQuery("SELECT COUNT(o) FROM UserExperienceAvailableVoteTimes o", Long.class).getSingleResult();
    }
    
    public static List<UserExperienceAvailableVoteTimes> UserExperienceAvailableVoteTimes.findAllUserExperienceAvailableVoteTimeses() {
        return entityManager().createQuery("SELECT o FROM UserExperienceAvailableVoteTimes o", UserExperienceAvailableVoteTimes.class).getResultList();
    }
    
    public static UserExperienceAvailableVoteTimes UserExperienceAvailableVoteTimes.findUserExperienceAvailableVoteTimes(String userno) {
        if (userno == null || userno.length() == 0) return null;
        return entityManager().find(UserExperienceAvailableVoteTimes.class, userno);
    }
    
    public static List<UserExperienceAvailableVoteTimes> UserExperienceAvailableVoteTimes.findUserExperienceAvailableVoteTimesEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM UserExperienceAvailableVoteTimes o", UserExperienceAvailableVoteTimes.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
