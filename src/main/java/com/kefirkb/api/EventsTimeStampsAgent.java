package com.kefirkb.api;

/**
 * @author Sergey
 * @since 04.05.2018
 *
 * Main interface for usage lib.
 */
public interface EventsTimeStampsAgent {

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
}
