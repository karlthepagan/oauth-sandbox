package karl.codes;

import org.joda.time.format.*;

/**
 * Created by karl on 9/19/15.
 */
public interface Joda {
    PeriodFormatter durationFormat = new PeriodFormatterBuilder()
            .appendDays()
            .appendSuffix("d")
            .appendHours()
            .appendSuffix("h")
            .appendMinutes()
            .appendSuffix("m")
            .appendSeconds()
            .appendSuffix("s")
            .toFormatter();

    DateTimeFormatter workTimeFormat = new DateTimeFormatterBuilder()
            .appendDayOfWeekText()
            .appendLiteral(' ')
            .appendHourOfHalfday(1)
            .appendLiteral(':')
            .appendMinuteOfHour(2)
            .appendLiteral(' ')
            .appendHalfdayOfDayText()
            .toFormatter();

    DateTimeFormatter timeFormat = new DateTimeFormatterBuilder()
            .appendHourOfDay(1)
            .appendLiteral(':')
            .appendMinuteOfHour(2)
            .appendLiteral(':')
            .appendSecondOfMinute(2)
            .toFormatter();

    DateTimeFormatter dateFormat = new DateTimeFormatterBuilder()
            .appendYear(1,99)
            .appendLiteral('-')
            .appendMonthOfYear(2)
            .appendLiteral('-')
            .appendDayOfMonth(2)
            .toFormatter();

    DateTimeFormatter trelloFormat = ISODateTimeFormat.dateTime();

}
