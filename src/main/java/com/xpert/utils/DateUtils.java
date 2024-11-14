package com.xpert.utils;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Utility class to use with java.util.Date and java.util.Calendar
 *
 * @author ayslan
 */
public class DateUtils implements Serializable {

    private static final long serialVersionUID = 5968975012762743779L;

    /**
     * Get Date with only day, month and year (removing hour, minute, second and
     * milisecond)
     *
     * @param date
     * @return
     */
    public static Date removeTime(Date date) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * Return a age (based on year)
     *
     * @param date
     * @return
     */
    public static int getAge(Date date) {
        Calendar dateOfBirth = new GregorianCalendar();
        dateOfBirth.setTime(date);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
        dateOfBirth.add(Calendar.YEAR, age);
        if (today.before(dateOfBirth)) {
            age--;
        }
        return age;

    }

    /**
     * Return the first day in month. Example: year 2013, month 0 return
     * '2013-01-01' (yyyy-MM-dd)
     *
     * @param month
     * @param year
     * @return
     */
    public static Date getFirstDayInMonth(int month, int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    /**
     * Return the first day in month. Example: year 2013, month 0 return
     * '2013-01-31' (yyyy-MM-dd)
     *
     * @param month
     * @param year
     * @return
     */
    public static Date getLastDayInMonth(int month, int year) {
        Calendar calendar = Calendar.getInstance();
        //set day 1 before month
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    /**
     * Return the first day in year. Example: year 2013 return '2013-01-01'
     * (yyyy-MM-dd)
     *
     * @param year
     * @return
     */
    public static Date getFirstDayInYear(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 1);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    /**
     * Return the last day in year. Example: year 2013 return '2013-12-31'
     * (yyyy-MM-dd)
     *
     * @param year
     * @return
     */
    public static Date getLastDayInYear(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DATE, 31);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    /**
     * Return the year of a date. Example: '2014-01-02' (yyyy-MM-dd) returns
     * '2014'
     *
     * @param date
     * @return
     */
    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * Return the month of a date. Example: '2014-01-02' (yyyy-MM-dd) returns 0
     * (january = 0)
     *
     *
     * @param date
     * @return
     */
    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }

    /**
     * Return the day of a date. Example: '2014-01-02' (yyyy-MM-dd) returns '02'
     *
     * @param date
     * @return
     */
    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DATE);
    }

    /**
     * Format a date from the parameter pattern
     *
     * @param date
     * @param pattern Date pattern. Example: dd/MM/yyyy
     * @return
     */
    public static String dateToString(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * Parse a string from the parameter pattern
     *
     * @param date
     * @param pattern Date pattern. Example: dd/MM/yyyy
     * @return
     */
    public static String dateToString(Calendar date, String pattern) {
        return dateToString(date.getTime(), pattern);
    }

    /**
     * Parse a string with the parameter pattern
     *
     * @param dateString
     * @param pattern Date pattern. Example: dd/MM/yyyy
     * @return
     * @throws java.text.ParseException
     */
    public static Date stringToDate(String dateString, String pattern) throws ParseException {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.parse(dateString);
    }

    /**
     * Parse a string with the parameter pattern
     *
     * @param dateString
     * @param pattern Date pattern. Example: dd/MM/yyyy
     * @return
     * @throws java.text.ParseException
     */
    public static Calendar stringToCalendar(String dateString, String pattern) throws ParseException {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date date = format.parse(dateString);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static boolean isValid(String dateString, String pattern) {

        SimpleDateFormat form = new SimpleDateFormat(pattern);
        form.setLenient(false);
        try {
            form.parse(dateString);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public static LocalDate dateToLocalDate(Date date) {
        return LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    public static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
    
}
