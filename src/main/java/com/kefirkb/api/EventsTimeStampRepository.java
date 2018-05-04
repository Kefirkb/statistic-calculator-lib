package com.kefirkb.api;

import java.io.Closeable;
import java.time.temporal.ChronoUnit;

/**
 * @author Sergey
 * @since 04.05.2018.
 *
 * Basic interface events repository
 */
public interface EventsTimeStampRepository extends Closeable {
    /**
     * Store timeStamp of event
     * @param timeStamp timeStamp in milliseconds since January 1, 1970, 00:00:00 GMT
     */
    void store(Long timeStamp);

    /**
     * Get count of events by chrono unit
     * @param unit chrono unit of period
     * @return count of events
     */
    long getCountByChronoUnit(ChronoUnit unit);
}
