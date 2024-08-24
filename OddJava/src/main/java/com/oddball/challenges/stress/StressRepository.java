package com.oddball.challenges.stress;

import java.util.Date;
import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StressRepository extends CrudRepository<Stress, Long> {
    
    @Query(value = """
            select s.*
            from stress s
            join moods m on m.user_id = s.user_id and m.date = s.date
            where m.date between ?1 and ?2
            and m.mood = 3
            and s.stress in (4,5)
            order by m.user_id, m.date
          """, nativeQuery = true)
    Collection<Stress> findBadStressDays(Date begin, Date end);
    
    @Query(value = """
            select s.*
            from stress s
            join moods m on m.user_id = s.user_id and m.date = s.date
            where m.mood = 3
            and s.stress in (4,5)
            order by m.user_id, m.date
          """, nativeQuery = true)
    Collection<Stress> findBadStressDays();
}

