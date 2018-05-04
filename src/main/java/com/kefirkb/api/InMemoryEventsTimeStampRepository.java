package com.kefirkb.api;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author Sergey
 * @since 04.05.2018.
 */
final class InMemoryEventsTimeStampRepository implements EventsTimeStampRepository {
    private final PriorityBlockingQueue<Long> timeStampStorage = new PriorityBlockingQueue<>(11, Comparator.reverseOrder());

    InMemoryEventsTimeStampRepository() {
    }

    @Override
    public void store(Long timeStamp) {
        timeStampStorage.add(timeStamp);
    }

    @Override
    public synchronized long getCountOfLastByChronoUnit(ChronoUnit unit, Clock clock) {
        if (unit != ChronoUnit.MINUTES && unit != ChronoUnit.DAYS && unit != ChronoUnit.HOURS) {
            throw new UnsupportedOperationException("Only last minute, hour and day is supported");
        }

        return countBy(unit, clock);
    }

    @Override
    public void close() {

    }

    long getContainerCount() {
        return timeStampStorage.size();
    }

    private long countBy(ChronoUnit unit, Clock clock) {
        Instant instantNow = clock.instant();
        Instant instantChronoBefore = instantNow.minus(1, unit);
        List<Long> buffer = new ArrayList<>();
        Long lastTimeStamp = timeStampStorage.peek();

        if (lastTimeStamp != null && Instant.ofEpochMilli(lastTimeStamp).isAfter(instantChronoBefore)) {
            do {
                buffer.add(timeStampStorage.poll());
                lastTimeStamp = timeStampStorage.peek();
            }
            while (lastTimeStamp != null && Instant.ofEpochMilli(lastTimeStamp).isAfter(instantChronoBefore));
        }
        timeStampStorage.addAll(buffer);
        return buffer.size();
    }
}
