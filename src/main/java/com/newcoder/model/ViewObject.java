package com.newcoder.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rainday on 16/6/30.
 */
//方便把所有数据打包在一起
public class ViewObject {
    private Map<String, Object> objs = new HashMap<String, Object>();
    public void set(String key, Object value) {
        objs.put(key, value);
    }

    public Object get(String key) {
        return objs.get(key);
    }
}
