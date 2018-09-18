package io.pivotal.pal.tracker;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TimeEntryRepository {
    TimeEntry create(TimeEntry timeEntryToCreate);

    TimeEntry find(long l);

    List<TimeEntry> list();

    TimeEntry update(long eq, TimeEntry any);

    TimeEntry delete(long l);
}
