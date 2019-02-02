package app.insti.notifications;

import java.util.concurrent.atomic.AtomicInteger;

public class NotificationId {
    private final static AtomicInteger c = new AtomicInteger(0);
    private final static AtomicInteger current_count = new AtomicInteger(0);

    public static int getID() {
        return c.incrementAndGet();
    }

    public static int getCurrentCount() {
        return current_count.get();
    }

    public static int decrementAndGetCurrentCount() {
        if (current_count.get() > 0)
            return current_count.decrementAndGet();
        else
            return 0;
    }

    public static void setCurrentCount(int count) {
        current_count.set(count);
    }
}
