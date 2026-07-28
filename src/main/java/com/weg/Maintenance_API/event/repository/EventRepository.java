package com.weg.Maintenance_API.event.repository;


import java.util.List;
import java.util.UUID;

import com.weg.Maintenance_API.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    @Query("""
            select event
              from Event event
             order by event.scheduledFor asc
            """)
    List<Event> findAllForCalendar();
}
