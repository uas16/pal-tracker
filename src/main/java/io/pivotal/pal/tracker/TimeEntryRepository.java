package io.pivotal.pal.tracker;

import java.util.List;

public interface TimeEntryRepository  {
    public TimeEntry create(TimeEntry timeEntry) ;

    public TimeEntry find(long id) ;

    public List<TimeEntry> list() ;

    public void delete(long id) ;

    TimeEntry update(long id, TimeEntry timeEntry);
}
