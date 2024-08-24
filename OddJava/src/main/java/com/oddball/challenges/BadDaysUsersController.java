package com.oddball.challenges;

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
public class BadDaysUsersController {

    private static final Logger logger = LoggerFactory.getLogger(BadDaysUsersController.class);
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MoodRepository moodRepository;

    @Autowired
    private StressRepository stressRepository;

	@GetMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}

	/*
	 1. Find which users that had two or more bad days in the week of 2017-05-07 and return
	   - Username
	   - Number of bad days each patient had for the week
	 */
    @GetMapping("/baddayusers")
    public String getBadDayUsers() {
    	
    	Date beginDate = DateUtil.toDate( "2017-05-07");  // move to param
    	Date endDate = DateUtil.getEndOfWeekDate(beginDate);
    	logger.info("beginDate: {}, endDate: {}", beginDate, endDate);
    	        
    	logger.info("findBadMoodDays");
    	Stream<Mood> moods = moodRepository.findBadMoodDays(beginDate, endDate).stream();
    	logger.info("findBadStressDays");
    	Stream<Stress> stresses = stressRepository.findBadStressDays(beginDate, endDate).stream();   
    	
    	logger.info("group data");
        Map<Long, List<Mood>> moodData = moods.collect(Collectors.groupingBy(Mood::getUserId));
        Map<Long, List<Stress>> stressData = stresses.collect(Collectors.groupingBy(Stress::getUserId));
                
    	logger.info("process data");

        record Result (String userName, long userId, long badDaysCount) {}
        
        final List<Result> results = new ArrayList<>();
        
        moodData.forEach((userId, userMoods) -> {
        	long count = userMoods.size() + stressData.getOrDefault(userId, Collections.emptyList()).size();
        	if (count > 2) {
        		Optional<User> usero = userRepository.findById(userId);
        		if (usero.isPresent()) {
        			User u = usero.get();
        			results.add(new Result(u.getUserName(), u.getId(), count));
        		}
        	}
        });
        
        List<Result> sortedResults = results.stream()
                .sorted(Comparator.comparing(Result::badDaysCount).reversed())
                .collect(Collectors.toList());
        
        StringBuilder buf = new StringBuilder();
        buf.append("user name (userId) -- # bad days \n");
        buf.append("--------- -------- -- ---------- \n");
        
        sortedResults.forEach(r -> {
        	buf.append(String.format("%s (%s) -- %d \n", r.userName, r.userId, r.badDaysCount));
        });
        buf.append("--------- -------- -- ---------- \n");
        buf.append(String.format(" - results: %s \n", sortedResults.size()));
        
        return buf.toString();
    }
}
