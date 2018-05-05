package com.kefirkb.api;

import org.junit.Test;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

/**
 * @author Sergey
 * @since 05.05.2018.
 */
public class EventsTimeStampsAgentImplSimpleStressTest {

    @Test
    public void countEventsByLastDay() throws InterruptedException {
        ExecutorService service = Executors.newFixedThreadPool(20);

        LocalDateTime now = LocalDateTime.of(2018, Month.APRIL, 25, 16,0,0);
        Clock clock = Clock.fixed(now.toInstant(ZoneOffset.UTC), ZoneId.systemDefault());
        Instant instantNow = now.toInstant(ZoneOffset.UTC);
        CountDownLatch timeStampsCounterLatch = new CountDownLatch(10000);

        EventsTimeStampsAgent agent = EventsTimeStampsAgent.instance(clock);
        for(int i = 0; i < 3000; i++) {
            service.execute(() -> {
                Instant instant = instantNow.minus(5, ChronoUnit.MILLIS);
                agent.considerEvent(instant.toEpochMilli());
                timeStampsCounterLatch.countDown();
            });
        }
        for(int i = 0; i < 5; i++) {
            service.execute(agent::countEventsByLastHour);
        }

        Instant instantDayBefore = instantNow.minus(1, ChronoUnit.DAYS);
        for(int i = 0; i < 7000; i++) {
            service.execute(() -> {
                Instant instant = instantDayBefore.minus(1, ChronoUnit.SECONDS);
                agent.considerEvent(instant.toEpochMilli());
                timeStampsCounterLatch.countDown();
            });
        }

        timeStampsCounterLatch.await();
        assertEquals(3000, agent.countEventsByLastDay());
        Thread.sleep(10000);
        assertEquals(3000, ((InMemoryEventsTimeStampRepository)((EventsTimeStampsAgentImpl)agent).getRepository()).getContainerCount());
    }
}