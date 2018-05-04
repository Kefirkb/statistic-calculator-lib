package com.kefirkb.api;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * @author Sergey
 * @since 04.05.2018.
 */
final class InMemoryEventsTimeStampRepository implements EventsTimeStampRepository {
    private final PriorityBlockingQueue<Long> timeStampStorage = new PriorityBlockingQueue<>();

    InMemoryEventsTimeStampRepository() {
    }

    @Override
    public void store(Long timeStamp) {
        timeStampStorage.put(timeStamp);
    }

    @Override
    public synchronized long getCountOfLastByChronoUnit(ChronoUnit unit, Clock clock) {
        if(unit != ChronoUnit.MINUTES && unit != ChronoUnit.DAYS && unit != ChronoUnit.HOURS) {
            throw new UnsupportedOperationException("Only last minute, hour and day is supported");
        }

        return countBy(unit, clock);
    }

    @Override
    public void close() {

    }

    private long countBy(ChronoUnit unit, Clock clock) {
        LocalDateTime now = LocalDateTime.now(clock);
        long lastByChronoTimeStamp = now.toInstant(ZoneOffset.UTC).minus(1, unit).toEpochMilli();
        List<Long> buffer = new ArrayList<>();
        Long lastTimeStamp = timeStampStorage.peek();

        if(lastTimeStamp > lastByChronoTimeStamp) {
            while(lastTimeStamp != null && lastTimeStamp > lastByChronoTimeStamp) {
                lastTimeStamp = timeStampStorage.poll();
                buffer.add(lastTimeStamp);
            }
        }

        timeStampStorage.addAll(buffer);
        return buffer.size();
    }
}
