package app.insti.notifications;

import java.util.concurrent.atomic.AtomicInteger;

public class NotificationId {
    private final static AtomicInteger c = new AtomicInteger(0);
    public static int getID() {
        return c.incrementAndGet();
    }

    private final static AtomicInteger current_count = new AtomicInteger(0);
    public static int getCurrentCount() {
        return c.get();
    }

    public static int decrementAndGetCurrentCount() {
        if (c.get() > 0)
            return c.decrementAndGet();
        else
            return 0;
    }

    public static void setCurrentCount(int count) {
        c.set(count);
    }
}
