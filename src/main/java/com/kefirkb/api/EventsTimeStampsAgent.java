package com.kefirkb.api;

import javax.validation.constraints.NotNull;
import java.io.Closeable;
import java.time.Clock;

/**
 * @author Sergey
 * @since 04.05.2018
 *
 * Main interface for usage lib.
 */
public interface EventsTimeStampsAgent extends Closeable{

    /**
     * Method to consider event
     * @param timeStamp timeStamp in milliseconds since January 1, 1970, 00:00:00 GMT
     */
    void considerEvent(long timeStamp);

    /**
     * Get count of considering events of last minute
     * @return count of considering events
     */
    long countEventsByLastMinute();

    /**
     * Get count of considering events of last hour
     * @return count of considering events
     */
    long countEventsByLastHour();

    /**
     * Get count of considering events of last day
     * @return count of considering events
     */
    long countEventsByLastDay();

    @NotNull
    static EventsTimeStampsAgent instance() {
        return new EventsTimeStampsAgentImpl(EventsTimeStampRepository.inMemoryRepository(Clock.systemUTC(), 5));
    }

    /**
     * Method for get instance
     * @param clock use clock for calculate now timestamps
     * @return instance of agent
     */
    @NotNull
    static EventsTimeStampsAgent instance(@NotNull Clock clock) {
        return new EventsTimeStampsAgentImpl(EventsTimeStampRepository.inMemoryRepository(clock, 5));
    }

    @Override
    void close();
}
