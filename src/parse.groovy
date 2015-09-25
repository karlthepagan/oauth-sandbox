import com.fasterxml.jackson.core.type.TypeReference
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import org.joda.time.*
import trello.Action

import java.util.concurrent.TimeUnit

import static karl.codes.Groovy.*
import static karl.codes.Jackson.*
import static karl.codes.Joda.*
import static karl.codes.OAuth.*
import static trello.Action.Type.*
import static trello.Trello.*;

def secret = properties('secret.properties')

RESTClient trello = new RESTClient(baseURI)
trello.auth.oauth(*signer(secret.trello));

String since = DateTime.now()
        .withDayOfWeek(DateTimeConstants.MONDAY)
        .withTimeAtStartOfDay()
        .with { it.isAfter( DateTime.now() ) ? it.minusWeeks(1) : it }
        .withZone(DateTimeZone.UTC) // local start of last Monday, in UTC
        .with trelloFormat.&print

List<Action> actions = trello.get(
        path: "boards/${secret.board}/actions",
        query: [
                filter: 'updateCard:idList',
                limit: 1000,
                since: since
                ])
{ HttpResponseDecorator resp ->
    return json.readValue(resp.entity.content, new TypeReference<List<Action>>() {})
}

// map events into timelines for each card
// join move events to create date ranges
def timelines = actions
        .findAll {it.type == updateCard && it.data.listAfter} // repeat of query filter updateCard:idList
        .reverse().inject([:].withDefault{[]}) { result, event ->
            def events = result[event.data.card.name]

            if(events.size) {
                assert events[-1].name == event.data.listBefore.name
                events[-1].date = new Interval(events[-1].date,event.date)
            }

            events << [name: event.data.listAfter.name, date: event.date]

            result}

// endcap all open date ranges
timelines.each { project, events ->
    events[-1].date = new Interval(events[-1].date,DateTime.now())
}

def totals = timelines.collect() { project, it ->
    def timeline = it.inject([:].withDefault{ new Duration(0) }) { totals, event ->
        print "$project: $event.name $event.date "

        if (event.date instanceof Interval) {
            Duration duration = event.date.toDuration()
            print durationFormat.print(duration.toPeriod())
            if (event.name == secret.listName) {
                LocalDate day = event.date.start.toLocalDate()
                totals[day] += duration
                totals.week += duration
            }
        }

        println()
        totals
    }

    [ project, timeline ]
}

// http://www.lni.wa.gov/WorkplaceRights/Wages/HoursBreaks/Breaks/
// duration > 5hr -> 30 min lunch
// duration / 4h -> 10 minute break periods

long lunchPeriodMillis = TimeUnit.HOURS.toMillis(5)
long breakPeriodMillis = TimeUnit.HOURS.toMillis(4)

totals.each { project, it ->
    it.each { k, Duration v ->
        print "$project: $k ${durationFormat.print(v.toPeriod())} "

        if (v.getMillis() > lunchPeriodMillis) {
            print 'lunch '
        }

        int breaks = v.getMillis() / breakPeriodMillis;
        switch(breaks) {
            case 0: break
            case 1: print '1 break '; break
            default: print "$breaks breaks "; break
        }

        println()
    }
}