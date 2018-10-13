package app.insti.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateTimeUtil {

    public static String getDate(String dtStart) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'+05:30'");
        try {
            Date date = format.parse(dtStart);
            Date now = new Date();
            long diff = System.currentTimeMillis() - date.getTime();
            long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - date.getTime());
            long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - date.getTime());
            long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - date.getTime());
            if (seconds <= 0) {
                return "now";
            } else if (seconds < 60 && seconds > 1) {
                return seconds + " seconds ago";
            } else if (minutes == 1) {
                return " 1 minute ago";
            } else if (minutes < 60 && minutes > 1) {
                return minutes + " minutes ago";
            } else if (hours == 1) {
                return "an hour ago";
            } else if (hours < 24 && hours > 1) {
                return hours + " hours ago";
            } else {
                long days = Math.round(diff / (24.0 * 60 * 60 * 1000));
                if (days == 0)
                    return "today";
                else if (days == 1)
                    return "yesterday";
                else if (days == 1)
                    return "a day ago";
                else if (days < 14)
                    return days + " days ago";
                else if (days < 30)
                    if ((int) (days / 7) == 1)
                        return "a week ago";
                    else
                        return ((int) (days / 7)) + " weeks ago";
                else if (days < 365)
                    if ((int) (days / 30) == 1)
                        return ((int) (days / 30)) + " month ago";
                    else
                        return ((int) (days / 30)) + " months ago";
                else
                    if ((int) (days / 365) == 1)
                        return "a year ago";
                    else
                        return ((int) (days / 365)) + " years ago";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}