package volteem.com.volteem.util;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarUtils {
    public static String getStringDateFromMM(long millis) {
        Date date = new Date(millis);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }

    public static String getNewsStringDateFromMM(long millis) {
        Date date = new Date(millis);
        Date currentDate = new Date(getCurrentTimeInMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
        if (TextUtils.equals(dateFormat.format(date), dateFormat.format(currentDate))) {
            return ("Today at " + hourFormat.format(date));
        } else {
            return dateFormat.format(date);
        }
    }

    public static String getHourFromLong(long milis) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milis);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        String clock = null;

        if (hour < 10) {
            clock = 0 + hour + ":";
        } else {
            clock = hour + ":";
        }

        if (minute < 10) {
            clock = clock + 0 + minute;
        } else {
            clock = clock + minute;
        }

        return clock;
    }

    public static long getCurrentTimeInMillis() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }

    public static int getAgeFromBirthdate(long birthdateInMillis) {
        Calendar birthCalendar = Calendar.getInstance();
        Calendar nowCalendar = Calendar.getInstance();
        birthCalendar.setTimeInMillis(birthdateInMillis);
        nowCalendar.setTimeInMillis(getCurrentTimeInMillis());
        int age = nowCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);
        if (nowCalendar.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            --age;
        }
        return age;
    }
}
