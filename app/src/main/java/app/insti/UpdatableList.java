package app.insti;

import java.util.ArrayList;
import java.util.List;

public class UpdatableList<T> extends ArrayList<T> {
    /** Convert a list to updatable list */
    public void setList(List<T> list) {
        this.clear();
        this.addAll(list);
    }

    /** Update existing or add */
    public void updateCache(T t) {
        for (int i = 0; i < this.size(); i++) {
            T cachedT = this.get(i);
            if (cachedT.equals(t)) {
                this.set(i, t);
                return;
            }
        }
        this.add(t);
    }
}
