package utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtils {
    public static boolean checkMonth(int month) {
        return month > 0 && month < 13;
    }

    public static boolean checkYear(int year) {
        return year > 1000 && year < 3000;
    }

    public static boolean checkDay(int year, int month, int day) {
        if (day < 1) {
            return false;
        }
        Calendar calendar = createDate(year, month, 1);
        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        return day <= maxDay;
    }

    public static Calendar createDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        return calendar;
    }

    public static String month(int month) throws ParseException {
        return new SimpleDateFormat("MMMM").format(new SimpleDateFormat("MM").parse(String.valueOf(month)));
    }

    public static String shortMonth(int month) throws ParseException {
        return new SimpleDateFormat("MMM").format(new SimpleDateFormat("MM").parse(String.valueOf(month)));
    }
}
