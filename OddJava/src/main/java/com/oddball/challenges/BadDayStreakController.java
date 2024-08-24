package com.oddball.challenges;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oddball.challenges.mood.*;
import com.oddball.challenges.stress.*;
import com.oddball.challenges.user.*;


@RestController
public class BadDayStreakController {

    private static final Logger logger = LoggerFactory.getLogger(BadDayStreakController.class);
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MoodRepository moodRepository;

    @Autowired
    private StressRepository stressRepository;

    /*
     * 2. Find the five longest consecutive Bad Day streaks and return for each
		   - Username
		   - Number of bad days
		   - Starting date of that streak
     */
    @GetMapping("/baddaystreak")
    public String getBadDayStreak() {
    	    	        
    	logger.info("findBadMoodDays");
    	Stream<Mood> moods = moodRepository.findBadMoodDays().stream();
    	
    	logger.info("findBadStressDays");
    	Stream<Stress> stresses = stressRepository.findBadStressDays().stream();   
    	
    	logger.info("group data");
        Map<Long, List<Mood>> moodData = moods.collect(Collectors.groupingBy(Mood::getUserId));
        Map<Long, List<Stress>> stressData = stresses.collect(Collectors.groupingBy(Stress::getUserId));
        
        logger.info("find streaks");
        Map<Long, UserRecord> userRecords = new HashMap<>();
        
        moodData.forEach((userId, userMoods) -> {
        	UserRecord ur = new UserRecord(userId, userMoods, null);
        	userRecords.put(userId, ur);
        });
        
        stressData.forEach((userId, userStresses) -> {
        	UserRecord ur = userRecords.get(userId);
        	if (ur == null) {
        		ur = new UserRecord(userId, null, userStresses);
        	}
        	else {      
        		ur = new UserRecord(userId, ur.moods, userStresses);
        	}
        	userRecords.put(userId, ur);
        });
        
        List<BadDayStreak> streaks = new ArrayList<>();
        userRecords.forEach((k,v) -> {
        	streaks.add(v.findBadDayStreak());
        });
        
        Collections.sort(streaks, Comparator.comparing(BadDayStreak::numDays).reversed());  // reverse order sort        
        
        StringBuilder buf = new StringBuilder();
        buf.append("user name (userId) -- # bad days -- start date \n");
        buf.append("--------- -------- -- ---------- -- ---------- \n");
        
        for (int i = 0; i < 5; i++) {
        	BadDayStreak streak = streaks.get(i);
        	Optional<User> usero = userRepository.findById(streak.userId());

    		if (usero.isPresent()) {
    			User u = usero.get();
    			buf.append(String.format("%s (%s) -- %d -- %s \n", u.getUserName(), u.getId(), streak.numDays(), streak.startDate()));
    		}
        }
        
        /*
user name (userId) -- # bad days -- start date 
--------- -------- -- ---------- -- ---------- 
Trent Mertz (398) -- 15 -- 2017-03-11 
Clinton Adams (225) -- 14 -- 2017-04-12 
Elouise Kozey (402) -- 13 -- 2017-04-20 
Miss Rodrigo McLaughlin (13) -- 12 -- 2017-01-19 
Nella Conn (227) -- 12 -- 2017-01-14 
--------- -------- -- ---------- -- ---------- 
         */
                
        buf.append("--------- -------- -- ---------- -- ---------- \n");
        
        return buf.toString();
    }	

	//----------------------------------------------------------------------------------------------
	
    record BadDayStreak (long userId, LocalDate startDate, int numDays) {}    
    
    record UserRecord(long userId, List<Mood> moods, List<Stress> stresses) {
    	BadDayStreak findBadDayStreak() {
    		List<Date> dates = moods.stream()
                    .map(Mood::getDate)
                    .collect(Collectors.toList());
    		
    		dates.addAll(stresses.stream()
                    .map(Stress::getDate)
                    .collect(Collectors.toList()));
    		
    		return findDateStreak(userId, dates);
    	}
    }
    
	/*
	select date
	from (
	select user_id, date
	            from moods m  
	            where m.mood in (1,2)
	union all
	select m.user_id, m.date
	            from stress s
	            join moods m on m.user_id = s.user_id and m.date = s.date
	            where m.mood = 3
	            and s.stress in (4,5)
	) t
	where user_id = 1
	order by user_id, date;
	*/
	static BadDayStreak findDateStreak(long userId, List<Date> dates) {
        List<LocalDate> localDates = DateUtil.toLocalDates(dates);
        
        Collections.sort(localDates);

        int maxStreakDays = 0;
        LocalDate maxStreakStartDate = null;

        int currStreakDays = 0;
        LocalDate currStreakStartDate = null;

        for (int i = 1; i < localDates.size(); i++) {
        	
        	LocalDate currDate = localDates.get(i - 1);
        	LocalDate nextDate = localDates.get(i);
        	
        	// one day apart?
		    if (ChronoUnit.DAYS.between(currDate, nextDate) == 1) {
		        currStreakDays++;
		        if (currStreakDays == 1) {
		        	currStreakStartDate = currDate;
		        }
		        
		        if (currStreakDays > maxStreakDays) {
		        	maxStreakDays = currStreakDays;
		        	maxStreakStartDate = currStreakStartDate;
		        }
		    }
		    else {
		    	// more than 1 day apart
		    	currStreakDays = 0;
		    }
        }

        if (maxStreakStartDate == null) {
        	maxStreakStartDate = localDates.get(0);
        }
        
        return new BadDayStreak(userId, maxStreakStartDate, ++maxStreakDays);
    }
}
