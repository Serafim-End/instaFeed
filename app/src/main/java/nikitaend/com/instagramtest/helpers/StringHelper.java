package nikitaend.com.instagramtest.helpers;

import android.content.Context;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Endaltsev Nikita
 *         start at 26.09.15.
 */
public class StringHelper {

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MM/dd");//dd/MM/yyyy
        Date now = new Date();
        return sdfDate.format(now);
    }

    /**
     * @param time input in seconds
     */
    public static String getDateStamp(long time) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MM/dd");
        sdfDate.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date now = new Date(time * 1000L);
        return sdfDate.format(now);
    }

    /**
     * @param time input in seconds and already in proper timezone
     * @return time in GMT0 timezone
     */
    public static String getTimeStamp(Context context, long time) {
        java.text.DateFormat df = DateFormat.getTimeFormat(context);
        df.setTimeZone(TimeZone.getTimeZone("GMT"));
        return df.format(new Date(time * 1000L));
    }

    public static String getTimeAgo(String timeString) {
        long wasIn = Long.parseLong(timeString);
        long time = System.currentTimeMillis() / 1000 - wasIn;
        long hours = (time / 60) / 60;
        long minutes = (time / 60) - (hours * 60);
        long seconds = time - (hours * 3600) - (minutes * 60);
        long days = hours / 24;

        if (days > 0) return days + " d";
        if (hours > 0) return hours + " h";
        if (minutes > 0) return minutes + " m";
        else { return seconds + " s"; }
    }
}
