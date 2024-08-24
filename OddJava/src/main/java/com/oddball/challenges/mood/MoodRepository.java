package com.oddball.challenges.mood;

import java.util.Date;
import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MoodRepository extends CrudRepository<Mood, Long> {
    
    @Query(value = """
            select m.*
            from moods m  
            where m.date between ?1 and ?2
            and m.mood in (1,2)
            order by m.user_id, m.date
          """, nativeQuery = true)
    Collection<Mood> findBadMoodDays(Date begin, Date end);
}

