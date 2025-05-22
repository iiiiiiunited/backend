package com.inity.tickenity.domain.seat.repository;

import com.inity.tickenity.domain.common.repository.BaseRepository;
import com.inity.tickenity.domain.seat.entity.SeatInformation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatInformationRepository extends BaseRepository<SeatInformation, Long> {

    @Query(value = """
SELECT si.*
FROM seat_information si
JOIN venues v ON si.venue_id = v.id
JOIN concert_venue cv ON cv.venue_id = v.id
JOIN concerts c ON c.id = cv.concert_id
WHERE c.id = :concertId
""", nativeQuery = true)
    List<SeatInformation> findByConcertId(@Param("concertId") Long concertId);

}
