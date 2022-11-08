package github.zimoyin.mtool.dao;

import java.util.HashMap;

public class Cache<T> extends HashMap<Class<?>,T> {
    public Cache<T> add(T t){
        super.put(t.getClass(),t);
        return this;
    }
}
