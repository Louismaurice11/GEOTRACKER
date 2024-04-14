package com.example.geotracker.helper_classes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// Helper class to convert date to string - no longer used
public class DateConvertor {
    public static final String LOG_TAG = DateConvertor.class.getSimpleName(); // Log tag

    // Private constructor to prevent instantiation
    private DateConvertor() {
        // Empty constructor
    }

    // Convert year, month, day to a string in the format yyyy-MM-dd
    public static String convertToDateString(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);

        // Using SimpleDateFormat to format the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    // Convert year, month to a string in the format yyyy-MM
    public static String convertToMonthString(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);

        // Using SimpleDateFormat to format the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    // Convert year to a string in the format yyyy
    public static String convertToYearString(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, 1, 1);

        // Using SimpleDateFormat to format the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    // Convert year, month, day to a string in the format yyyy-MM-dd
    public static int getSixMonthsAgo(int month) {
        if (month > 6) {
            return month - 6;
        } else {
            return 12 - (6 - month);
        }
    }

    // Convert year, month, day to a string in the format yyyy-MM-dd
    public static int getTwelveMonthsAgo(int month) {
        if (month > 12) {
            return month - 12;
        } else {
            return 12 - (12 - month);
        }
    }

    // Parse a date string in the format dd/MM/yyyy
    public static int[] parseDate(String dateString) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = dateFormat.parse(dateString);
            if (date != null) {
                String[] components = dateString.split("/");
                int day = Integer.parseInt(components[0]);
                int month = Integer.parseInt(components[1]);
                int year = Integer.parseInt(components[2]);
                int[] dateComponents = {day, month, year};
                return dateComponents;
            }
        } catch (NumberFormatException | ParseException e) {
            return null; // Return null for invalid date strings
        }
        return null; // Return null for invalid date strings
    }

    // Get current date
    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }
}
