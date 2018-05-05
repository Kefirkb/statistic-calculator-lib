package com.kefirkb.api;

import java.time.temporal.ChronoUnit;

/**
 * @author Sergey
 * @since 04.05.2018.
 */
final class EventsTimeStampsAgentImpl implements EventsTimeStampsAgent {
    private final EventsTimeStampRepository repository;

    EventsTimeStampsAgentImpl(EventsTimeStampRepository repository) {
        this.repository = repository;
    }

    @Override
    public void considerEvent(long timeStamp) {
        repository.store(timeStamp);
    }

    @Override
    public long countEventsByLastMinute() {
        return repository.getCountOfLastByChronoUnit(ChronoUnit.MINUTES);
    }

    @Override
    public long countEventsByLastHour() {
        return repository.getCountOfLastByChronoUnit(ChronoUnit.HOURS);
    }

    @Override
    public long countEventsByLastDay() {
        return repository.getCountOfLastByChronoUnit(ChronoUnit.DAYS);
    }

    public EventsTimeStampRepository getRepository() {
        return repository;
    }
}
