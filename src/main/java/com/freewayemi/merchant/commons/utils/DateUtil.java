package com.freewayemi.merchant.commons.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    private static final String ZONE_ASIA = "Asia/Kolkata";

    private static final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);

    public static String getDateWithPattern(String pattern, LocalDate localDate) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        return dtf.format(localDate);
    }

    public static String getDateTimeWithPattern(String pattern, LocalDateTime localDateTime) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        return dtf.format(localDateTime);
    }

    public static String getDateInISTWithPattern(String pattern, LocalDateTime localDateTime) {
        ZoneId fromZone = ZoneId.systemDefault();
        ZoneId toZone = ZoneId.of(ZONE_ASIA);
        ZonedDateTime currentISTTime = localDateTime.atZone(fromZone);
        ZonedDateTime zdt = currentISTTime.withZoneSameInstant(toZone);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        return dtf.format(zdt);
    }

    public static String getInstantDateInISTWithPattern(String pattern, Instant instant) {
        ZoneId fromZone = ZoneId.systemDefault();
        ZoneId toZone = ZoneId.of(ZONE_ASIA);
        ZonedDateTime currentISTTime = instant.atZone(fromZone);
        ZonedDateTime zdt = currentISTTime.withZoneSameInstant(toZone);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        return dtf.format(zdt);
    }

    public static LocalDate convertStringToLocalDate(String currentPattern, String date) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(currentPattern);
        return LocalDate.parse(date, dtf);
    }

    public static LocalDateTime convertStringToLocalDateTime(String currentPattern, String date) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(currentPattern);
        return LocalDateTime.parse(date, dtf);
    }

    public static Instant convertStringToInstantInIst(String currentPattern, String date) {
        try {
            if (StringUtils.isBlank(date))
                return null;
            LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(currentPattern));
            return localDate.atStartOfDay(ZoneId.of(ZONE_ASIA)).toInstant();
        } catch (Exception e) {
            LOGGER.error("Exception occurred while converting date: {} with pattern: {} to instant ist", date,
                    currentPattern);
            return null;
        }
    }

    public static Instant convertStringToInstantInUtc(String currentPattern, String date) {
        try {
            if (StringUtils.isBlank(date))
                return null;
            LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(currentPattern));
            return localDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        } catch (Exception e) {
            LOGGER.error("Exception occurred while converting date: {} with pattern: {} to instant utc", date,
                    currentPattern);
            return null;
        }
    }

    public static Instant getLastDayOfNextMonth() {
        return Instant.now().atZone(ZoneOffset.UTC).withDayOfMonth(1).plusMonths(2).minusDays(1).toInstant();
    }

    public static Instant addMonthsAndGetLastDay(int months) {
        return Instant.now().atZone(ZoneOffset.UTC).withDayOfMonth(1).plusMonths(months).minusDays(1).toInstant();
    }

    public static Instant getFifteenthOfNextMonth() {
        LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth());
        return Instant.now().atZone(ZoneOffset.UTC).withDayOfMonth(15).plusMonths(1).toInstant();
    }

    public static String getFirstDayOfCurrentMonth() {
        LocalDate todayDate = LocalDate.now();
        return todayDate.withDayOfMonth(1).toString();
    }

    public static String getToday() {
        LocalDate todayDate = LocalDate.now();
        return todayDate.toString();
    }

    public static Instant convertDate(String date) {
        LocalDate localDate;
        try {
            localDate = LocalDate.parse(date);
        } catch (Exception e) {
            localDate = LocalDate.now();
        }
        return localDate.atStartOfDay(ZoneId.of(ZONE_ASIA)).toInstant();
    }

    public static Instant convertUtcDateWithPattern (String date, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime utcTime = LocalDateTime.parse(date, formatter);
        LocalDateTime localDateTime = utcTime.atOffset(ZoneOffset.UTC).toLocalDateTime();
        return localDateTime.toInstant(ZoneOffset.UTC);
    }

    public static Instant convertDate(String date, int addDay) {
        try {
            return LocalDate.parse(date).plusDays(addDay).atStartOfDay(ZoneId.of(ZONE_ASIA)).toInstant();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return getDate(addDay);
    }

    public static Instant getDate(int addDay) {
        return LocalDate.now().plusDays(addDay).atStartOfDay(ZoneId.of(ZONE_ASIA)).toInstant();
    }

    public static LocalDate convertStringToLocalDateWithAddDays(String currentPattern, String date, int addDay) {
        return DateUtil.convertStringToLocalDate(currentPattern, date).plusDays(addDay);
    }

    public static String convertDatePattern(String currentPattern, String date, String newPattern) {
        LocalDate localDate = DateUtil.convertStringToLocalDate(currentPattern, date);
        return localDate.format(DateTimeFormatter.ofPattern(newPattern));
    }

    public static Instant addDays(Instant date, int day) {
        return date.atZone(ZoneId.of(ZONE_ASIA)).plusDays(day).toInstant();
    }


    public static Instant getCurrentUtcTime() {
        return Instant.now();
    }

    public static String getToday(String dateFormat) {
        LocalDate todayDate = LocalDate.now();
        DateTimeFormatter formatters = DateTimeFormatter.ofPattern(dateFormat);
        return todayDate.format(formatters);
    }

    public static Instant getYesterday(Instant txnCreatedDate) {
        return txnCreatedDate.atZone(ZoneOffset.UTC).minusDays(1).toInstant();
    }

    public static Instant getTodayInInstant() {
        return Instant.now().atZone(ZoneOffset.UTC).toInstant();
    }

    public static LocalDateTime getLocalDateTimeInIst() {
        return LocalDateTime.now().atZone(ZoneId.of("Asia/Kolkata")).toLocalDateTime();
    }

    public static String getDateInGenericFormat(String date, String newDatePattern) {
        if (org.springframework.util.StringUtils.hasText(date)) {
            if (date.contains("-")) {
                if (date.substring(0, 3).contains("-")) {
                    return convertDate(date, "dd-MM-yyyy", newDatePattern);
                } else {
                    return convertDate(date, "yyyy-MM-dd", newDatePattern);
                }
            } else if (date.contains("/")) {
                if (date.substring(0, 3).contains("/")) {
                    return convertDate(date, "dd/MM/yyyy", newDatePattern);
                }
            } else {
                return date;
            }
        }
        return date;
    }


    public static String convertDate(String dateStr, String from, String to) {
        try {
            return dateFormatter(dateStr, from, to);
        } catch (ParseException e) {
            LOGGER.error("Exception occurred while parsing date : {} from : {} pattern to : {} pattern", dateStr, from,
                    to);
        }
        return dateStr;
    }

    public static String dateFormatter(String dateStr, String from, String to) throws ParseException {
        Date date = getDate(dateStr, from);
        return getFormattedDate(date, to);
    }

    public static Date getDate(String dateStr, String from) throws ParseException {
        SimpleDateFormat fromFormatter = new SimpleDateFormat(from);
        Date date = fromFormatter.parse(dateStr);
        return date;
    }

    public static String getFormattedDate(Date date, String to) {
        SimpleDateFormat toFormatter = new SimpleDateFormat(to);
        return toFormatter.format(date);
    }

    public static boolean validateDateFormat(String value, String format) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred while validating date format using ", e);
        }
        return date != null;
    }

    public static Integer getDays(Date d1, Date d2) {
        return (int) ((d1.getTime() - d2.getTime()) / (1000 * 60 * 60 * 24));
    }

    public static Integer getSeconds(Date d1, Date d2) {
        return (int) ((d1.getTime() - d2.getTime()) / (1000) % 60);
    }

    public static Integer getMinutes(Date d1, Date d2) {
        return (int) ((d1.getTime() - d2.getTime()) / (60 * 1000) % 60);
    }

    public static Instant startTimeCurrentDay(Instant date) {
        return date.atZone(ZoneOffset.systemDefault())
                .truncatedTo(ChronoUnit.DAYS).toInstant();
    }
    public static Instant getFirstDayOfMonth(Instant date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(Date.from(date));
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.toInstant();
    }

    public static Instant getLastDayOfMonth(Instant date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(Date.from(date));
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        return cal.toInstant();
    }
    public static Instant endTimeOfTheDay(Instant date) {
        return LocalDateTime.ofInstant(date.plus(1, ChronoUnit.DAYS), ZoneId.systemDefault())
                .atZone(ZoneId.systemDefault())
                .toInstant();
    }

    public static Instant getFirstDayOfLastMonth(Instant date) {
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.add(Calendar.MONTH, -1);
        aCalendar.set(Calendar.DATE, 1);
        Date firstDateOfPreviousMonth = aCalendar.getTime();
        return firstDateOfPreviousMonth.toInstant();
    }

    public static Instant getFirstDayOfPreviousMonth(Instant date){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.toEpochMilli());
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DATE, 1);
        return cal.getTime().toInstant();
    }
    public static Instant getLastDayOfPreviousMonth(Instant date){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.toEpochMilli());
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DATE, 1);
        Date firstDateOfPreviousMonth = cal.getTime();
        cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
        return cal.getTime().toInstant();
    }

    public static Long getDaysBetweenTwoDates(Instant first, Instant second){
        Duration duration = Duration.between(first, second);
        return duration.toDays();
    }
}
