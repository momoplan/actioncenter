// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.actioncenter.domain;

import java.lang.String;

privileged aspect TcommisionRatio_Roo_ToString {
    
    public String TcommisionRatio.toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CommisionRatio: ").append(getCommisionRatio()).append(", ");
        sb.append("CommisionType: ").append(getCommisionType()).append(", ");
        sb.append("Id: ").append(getId()).append(", ");
        sb.append("Lotno: ").append(getLotno()).append(", ");
        sb.append("Userno: ").append(getUserno());
        return sb.toString();
    }
    
}
