package com.oddball.challenges;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class DateUtil {

    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    private DateUtil() {}
	
    public static List<LocalDate> toLocalDates(List<Date> dates) {
        List<LocalDate> localDates = new ArrayList<>();
        for (Date date : dates) {
            localDates.add(toLocalDate(date));
        }
        return localDates;
    }   
    
    public static LocalDate toLocalDate(Date date) {
        return LocalDate.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
    
    public static Date toDate(String date) {
    	try {
			return formatter.parse(date);
		} 
    	catch (ParseException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
    }
    
    public static Date getEndOfWeekDate(Date beginDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(beginDate);
        cal.add(Calendar.DAY_OF_MONTH, 6);  // end of week is 6 days later

        Date endDate = cal.getTime();
        return endDate;
    }
}
