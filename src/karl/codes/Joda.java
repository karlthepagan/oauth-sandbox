package karl.codes;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

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
}
