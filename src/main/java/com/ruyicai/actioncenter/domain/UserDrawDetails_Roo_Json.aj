// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.actioncenter.domain;

import com.ruyicai.actioncenter.domain.UserDrawDetails;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect UserDrawDetails_Roo_Json {
    
    public String UserDrawDetails.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static UserDrawDetails UserDrawDetails.fromJsonToUserDrawDetails(String json) {
        return new JSONDeserializer<UserDrawDetails>().use(null, UserDrawDetails.class).deserialize(json);
    }
    
    public static String UserDrawDetails.toJsonArray(Collection<UserDrawDetails> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<UserDrawDetails> UserDrawDetails.fromJsonArrayToUserDrawDetailses(String json) {
        return new JSONDeserializer<List<UserDrawDetails>>().use(null, ArrayList.class).use("values", UserDrawDetails.class).deserialize(json);
    }
    
}
