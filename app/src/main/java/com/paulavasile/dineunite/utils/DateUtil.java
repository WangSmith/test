package com.paulavasile.dineunite.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static final String DATE_FORMAT_11 = "MMM dd, yyyy"; 		// Jan 5,2016
    public static final String DATE_FORMAT_12 = "dd-MM-yyyy"; 		// Jan 5,2016
    /**
	 * @param date
	 * @param format
	 * @return
	 */
	public static String dateToString(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}


	/**
	 * toString for format 11.
	 * @param date
	 * @return
	 */
	public static String toStringFormat_11(Date date) {
		if (date == null)
			return "";
		return dateToString(date, DATE_FORMAT_11);
	}
	
	/**
	 * toString for format 12.
	 * @param date
	 * @return
	 */
	public static String toStringFormat_12(Date date) {
		if (date == null)
			return "";
		return dateToString(date, DATE_FORMAT_12);
	}
    
    public static Date parseDataFromFormat12(String dateString) {
    	Date retDate = new Date();
    	SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_12);  
    	try {  
    	    retDate = format.parse(dateString);  
    	} catch (ParseException e) {  
    	    e.printStackTrace();  
    	}
    	return retDate;
    }
}
