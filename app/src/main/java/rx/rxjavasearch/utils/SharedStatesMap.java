package rx.rxjavasearch.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 */
//singleton to store (key,value) during application session
public class SharedStatesMap {

    private static SharedStatesMap instance;
    private LinkedHashMap<String, Object> map;
    private LinkedHashMap<String, Integer> mapInt;


    private SharedStatesMap() {
        map = new LinkedHashMap<String, Object>();
        mapInt = new LinkedHashMap<String, Integer>();
    }

    public static SharedStatesMap getInstance() {
        if (instance == null) {
            instance = new SharedStatesMap();
        }
        return instance;
    }


    public void setKey(String key, Object value) {
        map.put(key, value);
    }
    public String getKey(String key) {
        String name = "";
        if (map.get(key) != null) {
            if (!map.isEmpty()) {
                name = map.get(key).toString();
            }
        }
        return name;
    }


    public void setKeyInt(String key, int value) {
        mapInt.put(key, value);
    }
    public int getKeyInt(String key) {
        int name=0;
        if (mapInt.get(key) != null) {
            if (!mapInt.isEmpty()) {
                name = mapInt.get(key);
            }
        }
        return name;
    }

}