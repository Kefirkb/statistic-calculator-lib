## **What is this?**
This is the simple lib for collecting some statistics.

## **How to use:**
The main interface `EventsTimeStampsAgent`.

#### Example of usage:
`Clock clock = Clock.systemUTC();
EventsTimeStampsAgent agent = EventsTimeStampsAgent.instance(clock);
agent.considerEvent(instant.toEpochMilli());
long count = agent.countEventsByLastMinute()`

## **How to build:**
Run in project folder `./gradlew build`

## **Future improvements**
* externalize values like period of cleaning queue
* add jcstress
* open api for EventsTimeStampRepository (hidden according requirements)