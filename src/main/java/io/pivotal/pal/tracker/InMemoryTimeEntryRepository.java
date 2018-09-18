package io.pivotal.pal.tracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTimeEntryRepository implements TimeEntryRepository {
    long nextId = 0;

    private Map<Long, TimeEntry> map = new HashMap<>();

    public TimeEntry create(TimeEntry timeEntryToCreate) {
        nextId++;
        map.put(nextId, timeEntryToCreate);
        timeEntryToCreate.setId(nextId);
        return timeEntryToCreate;
    }

    public TimeEntry find(long id) {
        return map.get(id);
    }

    public List<TimeEntry> list() {
        return new ArrayList<TimeEntry>(map.values());
    }

    public TimeEntry update(long id, TimeEntry timeEntry) {
        if (map.replace(id, timeEntry) != null) {
            timeEntry.setId(id);
            return timeEntry;
        }
        else {
            return null;
        }
    }

    public TimeEntry delete(long id) {
        return map.remove(id);
    }
}
