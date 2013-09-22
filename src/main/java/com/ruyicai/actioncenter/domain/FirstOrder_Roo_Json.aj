// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.actioncenter.domain;

import com.ruyicai.actioncenter.domain.FirstOrder;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect FirstOrder_Roo_Json {
    
    public String FirstOrder.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static FirstOrder FirstOrder.fromJsonToFirstOrder(String json) {
        return new JSONDeserializer<FirstOrder>().use(null, FirstOrder.class).deserialize(json);
    }
    
    public static String FirstOrder.toJsonArray(Collection<FirstOrder> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<FirstOrder> FirstOrder.fromJsonArrayToFirstOrders(String json) {
        return new JSONDeserializer<List<FirstOrder>>().use(null, ArrayList.class).use("values", FirstOrder.class).deserialize(json);
    }
    
}