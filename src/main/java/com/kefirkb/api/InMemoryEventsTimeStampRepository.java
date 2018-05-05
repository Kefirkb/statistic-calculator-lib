package com.kefirkb.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Sergey
 * @since 04.05.2018.
 */
final class InMemoryEventsTimeStampRepository implements EventsTimeStampRepository {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryEventsTimeStampRepository.class);
    private static final long DEFAULT_CLEANING_PERIOD = 5;

    private final PriorityBlockingQueue<Long> timeStampStorage = new PriorityBlockingQueue<>(50, Comparator.reverseOrder());
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private final Clock clock;

    private volatile CountDownLatch latchForDelete = new CountDownLatch(0);

    InMemoryEventsTimeStampRepository(Clock clock, long clearingPeriod) {
        assert clearingPeriod > 0;
        this.clock = clock;
        scheduledExecutorService.scheduleAtFixedRate(getDeleteTask(clock), clearingPeriod, clearingPeriod, TimeUnit.SECONDS);
    }

    InMemoryEventsTimeStampRepository(Clock clock, boolean withScheduledClearing) {
        this.clock = clock;
        if (withScheduledClearing) {
            scheduledExecutorService.scheduleAtFixedRate(getDeleteTask(clock), DEFAULT_CLEANING_PERIOD, DEFAULT_CLEANING_PERIOD, TimeUnit.SECONDS);
        }
    }

    @Override
    public void store(Long timeStamp) {
        try {
            latchForDelete.await();
            timeStampStorage.add(timeStamp);
        } catch (InterruptedException e) {
            logger.warn("Did not store. Thread was interrupted");
        }
    }

    @Override
    public long getCountOfLastByChronoUnit(ChronoUnit unit) {
        if (unit != ChronoUnit.MINUTES && unit != ChronoUnit.DAYS && unit != ChronoUnit.HOURS) {
            RuntimeException exception = new UnsupportedOperationException("Only last minute, hour and day is supported");
            logger.error("Not supported", exception);
        }

        return countBy(unit, clock);
    }

    @Override
    public void close() {
        logger.info("Try to shutdown scheduler...");
        scheduledExecutorService.shutdown();
        logger.info("Scheduler for cleaning queue is shutdown");
    }

    long getContainerCount() {
        return timeStampStorage.size();
    }

    private Runnable getDeleteTask(Clock clock) {
        return () -> {
            logger.info("Start delete old time stamps...");
            long count = deleteIfBefore(clock.instant().minus(1, ChronoUnit.DAYS));
            logger.info("Count of deleted timeStamps {}", count);
        };
    }

    private synchronized long deleteIfBefore(Instant instant) {
        // need to block access to timeStampStorage for store(Long timeStamp) method
        latchForDelete = new CountDownLatch(1);
        Long lastTimeStamp = timeStampStorage.peek();

        if (lastTimeStamp == null) {
            return 0;
        }

        if (lastTimeStamp != null && Instant.ofEpochMilli(lastTimeStamp).isBefore(instant)) {
            long count = timeStampStorage.size();
            timeStampStorage.clear();
            return count;
        }

        List<Long> savedBuffer = new ArrayList<>();
        do {
            savedBuffer.add(timeStampStorage.poll());
            lastTimeStamp = timeStampStorage.peek();
        }
        while (lastTimeStamp != null && Instant.ofEpochMilli(lastTimeStamp).isAfter(instant));

        long countOfDeleted = timeStampStorage.size();
        timeStampStorage.clear();
        timeStampStorage.addAll(savedBuffer);
        // and now we can already store into queue
        latchForDelete.countDown();
        return countOfDeleted;
    }

    private synchronized long countBy(ChronoUnit unit, Clock clock) {
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
