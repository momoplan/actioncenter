// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.actioncenter.domain;

import java.lang.String;

privileged aspect UserDrawDetails_Roo_ToString {
    
    public String UserDrawDetails.toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DrawDate: ").append(getDrawDate()).append(", ");
        sb.append("GainObject: ").append(getGainObject()).append(", ");
        sb.append("Id: ").append(getId()).append(", ");
        sb.append("PayObject: ").append(getPayObject()).append(", ");
        sb.append("PrizeId: ").append(getPrizeId()).append(", ");
        sb.append("Userno: ").append(getUserno());
        return sb.toString();
    }
    
}