package com.kefirkb.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.temporal.ChronoUnit;

/**
 * @author Sergey
 * @since 04.05.2018.
 */
final class EventsTimeStampsAgentImpl implements EventsTimeStampsAgent {
    private static final Logger logger = LoggerFactory.getLogger(EventsTimeStampsAgentImpl.class);
    private final EventsTimeStampRepository repository;

    EventsTimeStampsAgentImpl(EventsTimeStampRepository repository) {
        this.repository = repository;
    }

    @Override
    public void considerEvent(long timeStamp) {
        logger.debug("Considering timeStamp : {}", timeStamp);
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

    @Override
    public void close() {
        repository.close();
    }

    EventsTimeStampRepository getRepository() {
        return repository;
    }
}
