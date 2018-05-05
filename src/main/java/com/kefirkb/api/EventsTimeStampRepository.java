package com.kefirkb.api;

import java.io.Closeable;
import java.time.Clock;
import java.time.temporal.ChronoUnit;

/**
 * @author Sergey
 * @since 04.05.2018.
 * <p>
 * Basic interface events repository
 * Currently package access
 */
interface EventsTimeStampRepository extends Closeable {
    /**
     * Store timeStamp of event
     *
     * @param timeStamp timeStamp in milliseconds since January 1, 1970, 00:00:00 GMT
     */
    void store(Long timeStamp);

    /**
     * Get count of events by chrono unit
     *
     * @param unit chrono unit of period
     * @return count of events
     */
    long getCountOfLastByChronoUnit(ChronoUnit unit);

    @Override
    void close();

    /**
     * Return instance of repository
     *
     * @param clock use clock for calculate now timestamps
     * @return instance of repository
     */
    static EventsTimeStampRepository inMemoryRepository(Clock clock) {
        return new InMemoryEventsTimeStampRepository(clock, false);
    }

    /**
     * Return instance of repository
     *
     * @param clock          use clock for calculate now timestamps
     * @param cleaningPeriod period for cleaning queue job. Every this time job will be executing and all timeStamps of previous day will be deleted
     * @return instance of repository
     */
    static EventsTimeStampRepository inMemoryRepository(Clock clock, int cleaningPeriod) {
        return new InMemoryEventsTimeStampRepository(clock, cleaningPeriod);
    }
}
