package com.kefirkb.api;

import org.junit.Test;

import java.time.*;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;

/**
 * @author Sergey
 * @since 04.05.2018.
 */
public class InMemoryEventsTimeStampRepositoryTest {

    @Test
    public void getCountOfLastByLastMinute() {
        LocalDateTime now = LocalDateTime.of(2018, Month.APRIL, 25, 16,0,0);
        Clock clock = Clock.fixed(now.toInstant(ZoneOffset.UTC),ZoneId.systemDefault());
        InMemoryEventsTimeStampRepository  repository = (InMemoryEventsTimeStampRepository) EventsTimeStampRepository.inMemoryRepository(clock);
        Instant instant = now.toInstant(ZoneOffset.UTC);

        for(int i = 0; i < 8; i++) {
            repository.store(instant.minus(i, ChronoUnit.SECONDS).toEpochMilli());
        }
        assertEquals(8, repository.getContainerCount());

        instant = instant.minus(5, ChronoUnit.MINUTES);

        for(int i = 0; i < 10; i++) {
            repository.store(instant.minus(i, ChronoUnit.SECONDS).toEpochMilli());
        }
        assertEquals(18, repository.getContainerCount());
        assertEquals(8, repository.getCountOfLastByChronoUnit(ChronoUnit.MINUTES));
        assertEquals(18, repository.getContainerCount());
        repository.close();
    }

    @Test
    public void getCountOfLastByLastHour() {
        LocalDateTime now = LocalDateTime.of(2018, Month.APRIL, 25, 16,0,0);
        Clock clock = Clock.fixed(now.toInstant(ZoneOffset.UTC),ZoneId.systemDefault());
        InMemoryEventsTimeStampRepository  repository = (InMemoryEventsTimeStampRepository) EventsTimeStampRepository.inMemoryRepository(clock);
        Instant instant = now.toInstant(ZoneOffset.UTC);

        for(int i = 0; i < 8; i++) {
            repository.store(instant.minus(i, ChronoUnit.MINUTES).toEpochMilli());
        }
        assertEquals(8, repository.getContainerCount());

        instant = instant.minus(2, ChronoUnit.HOURS);

        for(int i = 0; i < 10; i++) {
            repository.store(instant.minus(i, ChronoUnit.MINUTES).toEpochMilli());
        }
        assertEquals(18, repository.getContainerCount());
        assertEquals(8, repository.getCountOfLastByChronoUnit(ChronoUnit.HOURS));
        assertEquals(18, repository.getContainerCount());
        repository.close();
    }

    @Test
    public void getCountOfLastByLastDay() {
        LocalDateTime now = LocalDateTime.of(2018, Month.APRIL, 25, 16,0,0);
        Clock clock = Clock.fixed(now.toInstant(ZoneOffset.UTC),ZoneId.systemDefault());
        InMemoryEventsTimeStampRepository  repository = (InMemoryEventsTimeStampRepository) EventsTimeStampRepository.inMemoryRepository(clock);
        Instant instant = now.toInstant(ZoneOffset.UTC);

        for(int i = 0; i < 8; i++) {
            repository.store(instant.minus(i, ChronoUnit.HOURS).toEpochMilli());
        }
        assertEquals(8, repository.getContainerCount());

        instant = instant.minus(1, ChronoUnit.DAYS);

        for(int i = 0; i < 10; i++) {
            repository.store(instant.minus(i, ChronoUnit.HOURS).toEpochMilli());
        }
        assertEquals(18, repository.getContainerCount());
        assertEquals(8, repository.getCountOfLastByChronoUnit(ChronoUnit.DAYS));
        assertEquals(18, repository.getContainerCount());
        repository.close();
    }

    @Test
    public void getCountAndSizeIfEmpty() {
        Clock clock = Clock.systemUTC();
        InMemoryEventsTimeStampRepository  repository = (InMemoryEventsTimeStampRepository) EventsTimeStampRepository.inMemoryRepository(clock);
        assertEquals(0, repository.getContainerCount());
        assertEquals(0, repository.getCountOfLastByChronoUnit(ChronoUnit.DAYS));
        assertEquals(0, repository.getContainerCount());
        repository.close();
    }

    @Test
    public void testWithCleaningSchedulerJob() throws InterruptedException {
        LocalDateTime now = LocalDateTime.of(2018, Month.APRIL, 25, 16,0,0);
        Clock clock = Clock.fixed(now.toInstant(ZoneOffset.UTC),ZoneId.systemDefault());
        Instant instant = now.toInstant(ZoneOffset.UTC);
        InMemoryEventsTimeStampRepository  repository = (InMemoryEventsTimeStampRepository) EventsTimeStampRepository.inMemoryRepository(clock, 5);

        for(int i = 0; i < 8; i++) {
            repository.store(instant.minus(i, ChronoUnit.HOURS).toEpochMilli());
        }
        assertEquals(8, repository.getContainerCount());

        instant = instant.minus(2, ChronoUnit.DAYS);

        for(int i = 0; i < 10; i++) {
            repository.store(instant.minus(i, ChronoUnit.HOURS).toEpochMilli());
        }
        assertEquals(18, repository.getContainerCount());
        Thread.sleep(12000L);
        assertEquals(8, repository.getContainerCount());
        repository.close();
    }
}