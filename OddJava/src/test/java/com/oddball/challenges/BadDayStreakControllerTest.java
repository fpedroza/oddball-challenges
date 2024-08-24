package com.oddball.challenges;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

import org.junit.jupiter.api.Test;

import com.oddball.challenges.BadDayStreakController.BadDayStreak;

class BadDayStreakControllerTest {
	
	@Test
	void test_findDateStreak_one() throws Exception {
		
		String date = "2017-01-03";
		
		List<String> dateStrings = List.of(date);

		test(dateStrings, date, 1);
	}

	@Test
	void test_findDateStreak_two() throws Exception {
		
		String date = "2017-01-03";
		
		List<String> dateStrings = List.of(date, "2017-02-03");

		test(dateStrings, date, 1);
	}
	
	@Test
	void test_findDateStreak_twob() throws Exception {
		
		String date = "2017-01-03";
		
		List<String> dateStrings = List.of(date, "2017-01-04");

		test(dateStrings, date, 2);
	}
	
	@Test
	void test_findDateStreak_three() throws Exception {
		
		String date = "2017-01-03";
		
		List<String> dateStrings = List.of(date, "2017-02-03", "2017-03-03");

		test(dateStrings, date, 1);
	}
	
	@Test
	void test_findDateStreak_three2() throws Exception {
		
		String date = "2017-01-03";
		
		List<String> dateStrings = List.of(date, "2017-01-04", "2017-03-03");

		test(dateStrings, date, 2);
	}
	
	@Test
	void test_findDateStreak_three3() throws Exception {
		
		String date = "2017-01-03";
		
		List<String> dateStrings = List.of(date, "2017-01-04", "2017-01-05");

		test(dateStrings, date, 3);
	}
	
	@Test
	void test_findDateStreak_span_month() throws Exception {
		
		String date = "2017-01-31";
		
		List<String> dateStrings = List.of(date, "2017-02-01", "2017-03-03");

		test(dateStrings, date, 2);
	}
	
	@Test
	void test_findDateStreak_span_year() throws Exception {
		
		String date = "2016-12-31";
		
		List<String> dateStrings = List.of(date, "2017-01-01", "2017-03-03", "2017-03-04");

		test(dateStrings, date, 2);
	}
	
	@Test
	void test_findDateStreak_threeb() throws Exception {
		
		String date = "2017-03-02";
		
		List<String> dateStrings = List.of("2017-02-03", date, "2017-03-03");

		test(dateStrings, date, 2);
	}
	
	@Test
	void test_findDateStreak_2_streaks_same_length() throws Exception {
		
		String date = "2017-01-03";
		
		List<String> dateStrings = List.of(date, "2017-01-04", "2017-03-02", "2017-03-03");

        test(dateStrings, date, 2);
	}
	
	@Test
	void test_findDateStreak_2_streaks_diff_length() throws Exception {
		
		String date = "2017-01-03";
		
		List<String> dateStrings = List.of(date, "2017-01-04", "2017-01-05", "2017-03-02", "2017-03-03");

        test(dateStrings, date, 3);
	}
	
	@Test
	void test_findDateStreak_2_streaks_diff_length2() throws Exception {
		
		String date = "2017-01-03";
		
		List<String> dateStrings = List.of("2016-03-02", "2016-03-03", date, "2017-01-04", "2017-01-05");

        test(dateStrings, date, 3);
	}
		
	@Test
	void test_findDateStreak() throws Exception {
		
		List<String> dateStrings = List.of(
                "2017-01-03", 
                "2017-01-05", 
                "2017-01-06", 
                "2017-01-07", 
                "2017-01-08",
                "2017-01-10", 
                "2017-01-11", 
                "2017-01-12", 
                "2017-01-14", 
                "2017-01-15",
                "2017-01-16", 
                "2017-01-18", 
                "2017-01-19", 
                "2017-01-21", 
                "2017-01-23",
                "2017-01-24", 
                "2017-01-25", 
                "2017-01-28", 
                "2017-02-02", 
                "2017-02-03",
                "2017-02-04", 
                "2017-02-06", 
                "2017-02-09", 
                "2017-02-11", 
                "2017-02-12",
                "2017-02-13", 
                "2017-02-14", 
                "2017-02-16", 
                "2017-02-18", 
                "2017-02-21",
                "2017-02-23", 
                "2017-02-24", 
                "2017-02-25", 
                "2017-02-26", 
                "2017-03-03",
                "2017-03-06",  // start streak
                "2017-03-07", 
                "2017-03-08", 
                "2017-03-09", 
                "2017-03-10",  // end streak (5 days)
                "2017-03-12", 
                "2017-03-15", 
                "2017-03-21", 
                "2017-03-24", 
                "2017-03-26",
                "2017-03-27", 
                "2017-03-28", 
                "2017-04-06", 
                "2017-04-07", 
                "2017-04-08",
                "2017-04-09", 
                "2017-04-10", 
                "2017-04-12", 
                "2017-04-19", 
                "2017-04-21",
                "2017-04-22", 
                "2017-04-26", 
                "2017-04-28", 
                "2017-04-29", 
                "2017-05-04",
                "2017-05-05", 
                "2017-05-09", 
                "2017-05-14", 
                "2017-05-17", 
                "2017-05-18",
                "2017-05-25", 
                "2017-05-27", 
                "2017-05-31"
        );

		test(dateStrings, "2017-03-06", 5);
	}
	
	private void test(List<String> dateStrings, String date, int streakLength) throws Exception {
		
        List<Date> dates = convertDates(dateStrings);
        
		BadDayStreak streak = BadDayStreakController.findDateStreak(1, dates);
		System.out.println(streak);
		
		assertEquals(DateUtil.toLocalDate(DateUtil.toDate(date)), streak.startDate());
		assertEquals(streakLength, streak.numDays());
	}
	
	
	private List<Date> convertDates(List<String> dateStrings) {
        List<Date> dates = new ArrayList<>();

        for (String dateString : dateStrings) {
            dates.add(DateUtil.toDate(dateString));
        }

        return dates;
    }
}
