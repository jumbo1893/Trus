package com.jumbo.trus;

import android.util.Log;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

public class Date {
    private static final String TAG = "Date";

    public static String DATE_PATTERN = "dd/MM/yyyy";
    public static String DATE_PATTERN_SHORT = "d/M/yyyy";
    public static String TIMESTAMP_PATTERN = "dd/MM/yyy HH:mm:ss";


    public long convertTextDateToMillis(String date) {
        try {
            LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_PATTERN));
            return localDate.toEpochDay();
        }
        catch (DateTimeException e) {
            LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(DATE_PATTERN_SHORT));
            return localDate.toEpochDay();
        }
    }

    public long convertPkflTextDateToMillis(String date) {
        try {
            LocalDateTime localDateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
        catch (DateTimeException e) {
            Log.d(TAG, "convertPkflTextDateToMillis: " + date + e);
            LocalDate localDate = LocalDate.ofEpochDay(0);
            return localDate.toEpochDay();
        }
    }

    public long getCurrentDateInMillis() {
        LocalDate localDate = LocalDate.now();
        return localDate.toEpochDay();
    }

    public String convertMillisToTextDate(long millis) {
        LocalDate localDate = LocalDate.ofEpochDay(millis);
        String date = localDate.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
        return date;
    }

    public String convertMillisSecToTextDate(long millis) {
        Instant instant = Instant.ofEpochMilli(millis);
        LocalDateTime localDate = LocalDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId());
        String date = localDate.format(DateTimeFormatter.ofPattern(DATE_PATTERN));
        return date;
    }

    public String convertMillisToStringTimestamp(long millis) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), TimeZone.getDefault().toZoneId());
        String time = localDateTime.format(DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN));
        return time;
    }

    public int calculateAge(long dateOfBirth) {
        LocalDate today = LocalDate.now();
        LocalDate birthday = LocalDate.ofEpochDay(dateOfBirth);
        Period p = Period.between(birthday, today);
        return p.getYears();
    }

    public int calculateDaysToBirthday(long dateOfBirth) {
        LocalDate today = LocalDate.now();
        LocalDate birthday = LocalDate.ofEpochDay(dateOfBirth);
        int age = calculateAge(dateOfBirth);
        LocalDate nextBirthday = birthday.plusYears(age);
        if (nextBirthday.isBefore(today)) {
            nextBirthday = birthday.plusYears(age + 1);
        }
        long daysUntilNextBirthday = ChronoUnit.DAYS.between(today, nextBirthday);
        return Math.toIntExact(daysUntilNextBirthday);
    }

    public boolean wasBirthdayInMatchday(long playerBirthday, long matchDay) {
        LocalDate player = LocalDate.ofEpochDay(playerBirthday);
        LocalDate match = LocalDate.ofEpochDay(matchDay);
        if (player.getMonthValue() == match.getMonthValue() && player.getDayOfMonth() == match.getDayOfMonth()) {
            return true;
        }
        return false;
    }
}
