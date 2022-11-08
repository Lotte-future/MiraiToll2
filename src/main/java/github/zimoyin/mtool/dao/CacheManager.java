package github.zimoyin.mtool.dao;

import lombok.ToString;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
@ToString
public class CacheManager {
    private volatile static CacheManager INSTANCE;
    private final HashMap<Long,Cache> map = new HashMap<Long,Cache>();
    private CacheManager() {
    }

    public static CacheManager getInstance() {
        if (INSTANCE == null) synchronized (CacheManager.class) {
            if (INSTANCE == null) INSTANCE = new CacheManager();
        }
        return INSTANCE;
    }

    public void put(Long key, Cache cache) {
        Cache cache1 = map.get(key);
        if (cache1 == null) map.put(key, cache);
        else cache.putAll(cache1);
    }

    public void get(Long key){
        map.get(key);
    }

    public void remove(Long key){
        map.remove(key);
    }

    public Set<Long> keySet(){
        return map.keySet();
    }

    public Collection<Cache> value(){
        return map.values();
    }
}
