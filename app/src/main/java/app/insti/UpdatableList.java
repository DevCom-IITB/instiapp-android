package app.insti;

import java.util.ArrayList;
import java.util.List;

public class UpdatableList<T> extends ArrayList<T> {
    private List<T> cache = new ArrayList<>();

    public List<T> getCache() {
        return cache;
    }

    public void setCache(List<T> mCache) {
        cache = mCache;
    }

    /** Update existing or add */
    public void updateCache(T t) {
        for (int i = 0; i < cache.size(); i++) {
            T cachedEvent = cache.get(i);
            if (cachedEvent.equals(t)) {
                cache.set(i, t);
                return;
            }
        }
        cache.add(t);
    }
}
