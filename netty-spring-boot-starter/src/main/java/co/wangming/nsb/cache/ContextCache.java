package co.wangming.nsb.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created By WangMing On 2019-12-20
 **/
public class ContextCache {

    public static final Map<String, Object> cache = new ConcurrentHashMap<>();

    public static void put(String key, Object value) {
        cache.put(key, value);
    }

    public static Object get(String key) {
        return cache.get(key);
    }
}
