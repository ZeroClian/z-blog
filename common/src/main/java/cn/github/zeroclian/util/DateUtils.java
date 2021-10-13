package cn.github.zeroclian.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;

/**
 * @Desciption 日期工具类
 * @Author: ZeroClian
 * @Date: 2021-09-15 6:50 下午
 */
public class DateUtils {

    /**
     * 获取月初
     */
    public static LocalDate getMonthStart(LocalDate date) {
        return LocalDate.of(date.getYear(), date.getMonth(), 1);
    }

    /**
     * 获取月底
     */
    public static LocalDate getMonthEnd(LocalDate date) {
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 获取周的第一天
     */
    public static LocalDate getWeekStart(LocalDate date) {
        return date.with(WeekFields.of(Locale.FRANCE).dayOfWeek(), 1);
    }

    /**
     * 获取周的最后一天
     */
    public static LocalDate getWeekEnd(LocalDate date) {
        return date.with(WeekFields.of(Locale.FRANCE).dayOfWeek(), 7L);
    }

    /**
     * 得到该时间段的日期
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return
     */
    public static List<LocalDate> getBetweenLocalDate(LocalDate start, LocalDate end) {
        // 得到本周日期
        List<LocalDate> localDateList = new ArrayList<>();
        while (!start.isAfter(end)) {
            localDateList.add(start);
            start = start.plusDays(1);
        }
        return localDateList;
    }

    /**
     * 根据日期获取 星期 （2019-05-06 ——> 星期一）
     *
     * @param date
     * @return
     */
    public static String dateToWeek(Date date) {

        if (date == null) {
            return null;
        }
        String[] weekDays = {"SUN", "MON", "TUES", "WED", "THUR", "FRI", "SAT"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //一周的第几天
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * LocalDate转Date
     *
     * @param localDate
     * @return
     */
    public static Date localDate2Date(LocalDate localDate) {
        if (null == localDate) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
