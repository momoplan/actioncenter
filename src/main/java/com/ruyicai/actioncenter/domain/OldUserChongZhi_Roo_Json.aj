// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.actioncenter.domain;

import com.ruyicai.actioncenter.domain.OldUserChongZhi;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect OldUserChongZhi_Roo_Json {
    
    public String OldUserChongZhi.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static OldUserChongZhi OldUserChongZhi.fromJsonToOldUserChongZhi(String json) {
        return new JSONDeserializer<OldUserChongZhi>().use(null, OldUserChongZhi.class).deserialize(json);
    }
    
    public static String OldUserChongZhi.toJsonArray(Collection<OldUserChongZhi> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<OldUserChongZhi> OldUserChongZhi.fromJsonArrayToOldUserChongZhis(String json) {
        return new JSONDeserializer<List<OldUserChongZhi>>().use(null, ArrayList.class).use("values", OldUserChongZhi.class).deserialize(json);
    }
    
}