package nl.tudelft.simulation.supplychain.content.database;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * CalendarUtility<br>
 * <br>
 * Copyright (c) 2003-2018 Delft University of Technology, Jaffalaan 5, 2628 BX Delft, the Netherlands. All rights reserved. See
 * for project information <a href="https://www.simulation.tudelft.nl/" target="_blank">www.simulation.tudelft.nl</a>. The
 * source code and binary code of this software is proprietary information of Delft University of Technology.
 * @author <a href="https://www.tudelft.nl/averbraeck" target="_blank">Alexander Verbraeck</a>
 */
public final class CalendarUtility implements Serializable
{
    /** the serial version uid */
    private static final long serialVersionUID = 12L;

    /**
     * Private constructor.
     */
    private CalendarUtility()
    {
        super();
    }

    /**
     * Converts millis to formatted date string.
     * @param millis time in millis
     * @return formatted date (dd-mm-yyyy)
     */
    public static String formatDate(final long millis)
    {
        if (millis > 1)
        {
            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(millis);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            return day + "-" + month + "-" + year;
        }
        return "";
    }

    /**
     * Get zeroes before the number to fill to a certain number of digits
     * @param number the number ot format
     * @param digits the number of digits
     * @return the formatted string
     */
    protected static String formatZeroes(final int number, final int digits)
    {
        String retValue = "" + number;
        while (retValue.length() < digits)
        {
            retValue = "0" + retValue;
        }
        return retValue;
    }

    /**
     * Converts millis to formatted date and time string.
     * @param millis time in millis
     * @return formatted date (dd-mm-yyyy hh:mm:ss)
     */
    public static String formatDateTime(final long millis)
    {
        if (millis > 1)
        {
            Calendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(millis);
            String year = "" + calendar.get(Calendar.YEAR);
            String month = formatZeroes(calendar.get(Calendar.MONTH) + 1, 2);
            String day = formatZeroes(calendar.get(Calendar.DAY_OF_MONTH), 2);
            String hour = formatZeroes(calendar.get(Calendar.HOUR), 2);
            String minute = formatZeroes(calendar.get(Calendar.MINUTE), 2);
            String second = formatZeroes(calendar.get(Calendar.SECOND), 2);
            return day + "-" + month + "-" + year + " " + hour + ":" + minute + ":" + second;
        }
        return "00-00-0000 00:00:00";
    }

    /**
     * Get millis from formatted date string
     * @param date date string (dd-mm-yyyy)
     * @return millis
     */
    public static long getMillis(final String date)
    {
        long millis = 0;
        if (date.length() > 1)
        {
            Calendar calendar = new GregorianCalendar();
            String[] hlp = date.split("-");
            int day = Integer.parseInt(hlp[0]);
            int month = Integer.parseInt(hlp[1]) - 1;
            int year = Integer.parseInt(hlp[2]);
            calendar.set(year, month, day);
            millis = calendar.getTimeInMillis();
            return millis;
        }
        return millis;
    }

    /**
     * Get a formatted representation of today's date
     * @return formatted date
     */
    public static String getToday()
    {
        Calendar calendar = new GregorianCalendar();
        Date trialTime = new Date();
        calendar.setTime(trialTime);
        return CalendarUtility.formatDate(calendar.getTimeInMillis());
    }

    /**
     * Get a long representation of today's date
     * @return date
     */
    public static long getTodayMillis()
    {
        Calendar calendar = new GregorianCalendar();
        Date trialTime = new Date();
        calendar.setTime(trialTime);
        return calendar.getTimeInMillis();
    }
}
