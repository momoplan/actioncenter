// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.actioncenter.domain;

import com.ruyicai.actioncenter.domain.Fund2Draw;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect Fund2Draw_Roo_Json {
    
    public String Fund2Draw.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static Fund2Draw Fund2Draw.fromJsonToFund2Draw(String json) {
        return new JSONDeserializer<Fund2Draw>().use(null, Fund2Draw.class).deserialize(json);
    }
    
    public static String Fund2Draw.toJsonArray(Collection<Fund2Draw> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<Fund2Draw> Fund2Draw.fromJsonArrayToFund2Draws(String json) {
        return new JSONDeserializer<List<Fund2Draw>>().use(null, ArrayList.class).use("values", Fund2Draw.class).deserialize(json);
    }
    
}
