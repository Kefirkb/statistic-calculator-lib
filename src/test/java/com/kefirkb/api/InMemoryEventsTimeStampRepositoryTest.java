package com.kefirkb.api;

import org.junit.Before;
import org.junit.Test;

import java.time.*;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;

/**
 * @author Sergey
 * @since 04.05.2018.
 */
public class InMemoryEventsTimeStampRepositoryTest {
    private InMemoryEventsTimeStampRepository repository;

    @Before
    public void setUp() {
        repository = (InMemoryEventsTimeStampRepository) EventsTimeStampRepository.inMemoryRepository();
    }

    @Test
    public void getCountOfLastByLastMinute() {
        LocalDateTime now = LocalDateTime.of(2018, Month.APRIL, 25, 16,0,0);
        Clock clock = Clock.fixed(now.toInstant(ZoneOffset.UTC),ZoneId.systemDefault());
        Instant instant = now.toInstant(ZoneOffset.UTC);

        for(int i = 0; i < 8; i++) {
            repository.store(instant.minus(i, ChronoUnit.SECONDS).toEpochMilli());
        }
        assertEquals(8, repository.getContainerCount());

        instant = instant.minus(1, ChronoUnit.HOURS);

        for(int i = 0; i < 10; i++) {
            repository.store(instant.minus(i, ChronoUnit.SECONDS).toEpochMilli());
        }
        assertEquals(18, repository.getContainerCount());
        assertEquals(8, repository.getCountOfLastByChronoUnit(ChronoUnit.MINUTES, clock));
        assertEquals(18, repository.getContainerCount());
    }

    @Test
    public void getCountOfLastByLastHour() {
        LocalDateTime now = LocalDateTime.of(2018, Month.APRIL, 25, 16,0,0);
        Clock clock = Clock.fixed(now.toInstant(ZoneOffset.UTC),ZoneId.systemDefault());
        Instant instant = now.toInstant(ZoneOffset.UTC);

        for(int i = 0; i < 8; i++) {
            repository.store(instant.minus(i, ChronoUnit.MINUTES).toEpochMilli());
        }
        assertEquals(8, repository.getContainerCount());

        instant = instant.minus(1, ChronoUnit.DAYS);

        for(int i = 0; i < 10; i++) {
            repository.store(instant.minus(i, ChronoUnit.MINUTES).toEpochMilli());
        }
        assertEquals(18, repository.getContainerCount());
        assertEquals(8, repository.getCountOfLastByChronoUnit(ChronoUnit.HOURS, clock));
        assertEquals(18, repository.getContainerCount());
    }

    @Test
    public void getCountOfLastByLastDay() {
        LocalDateTime now = LocalDateTime.of(2018, Month.APRIL, 25, 16,0,0);
        Clock clock = Clock.fixed(now.toInstant(ZoneOffset.UTC),ZoneId.systemDefault());
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
        assertEquals(8, repository.getCountOfLastByChronoUnit(ChronoUnit.DAYS, clock));
        assertEquals(18, repository.getContainerCount());
    }

    @Test
    public void getCountAndSizeIfEmpty() {
        Clock clock = Clock.systemUTC();
        assertEquals(0, repository.getContainerCount());
        assertEquals(0, repository.getCountOfLastByChronoUnit(ChronoUnit.DAYS, clock));
        assertEquals(0, repository.getContainerCount());
    }

    @Test
    public void close() {
    }
}