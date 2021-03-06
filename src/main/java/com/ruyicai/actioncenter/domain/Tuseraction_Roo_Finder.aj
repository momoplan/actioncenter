// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.actioncenter.domain;

import com.ruyicai.actioncenter.domain.Tuseraction;
import java.lang.String;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

privileged aspect Tuseraction_Roo_Finder {
    
    public static TypedQuery<Tuseraction> Tuseraction.findTuseractionsByUsernoEquals(String userno) {
        if (userno == null || userno.length() == 0) throw new IllegalArgumentException("The userno argument is required");
        EntityManager em = Tuseraction.entityManager();
        TypedQuery<Tuseraction> q = em.createQuery("SELECT o FROM Tuseraction AS o WHERE o.userno = :userno", Tuseraction.class);
        q.setParameter("userno", userno);
        return q;
    }
    
}
