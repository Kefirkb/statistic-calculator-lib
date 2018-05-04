package com.kefirkb.api;

import java.time.Clock;
import java.time.temporal.ChronoUnit;

/**
 * @author Sergey
 * @since 04.05.2018.
 */
final class EventsTimeStampsAgentImpl implements EventsTimeStampsAgent {
    private final EventsTimeStampRepository repository;
    private final Clock clock;

    EventsTimeStampsAgentImpl(Clock clock, EventsTimeStampRepository repository) {
        this.clock = clock;
        this.repository = repository;
    }

    @Override
    public void considerEvent(long timeStamp) {
        repository.store(timeStamp);
    }

    @Override
    public long countEventsByLastMinute() {
        return repository.getCountOfLastByChronoUnit(ChronoUnit.MINUTES, clock);
    }

    @Override
    public long countEventsByLastHour() {
        return repository.getCountOfLastByChronoUnit(ChronoUnit.HOURS, clock);
    }

    @Override
    public long countEventsByLastDay() {
        return repository.getCountOfLastByChronoUnit(ChronoUnit.DAYS, clock);
    }
}
