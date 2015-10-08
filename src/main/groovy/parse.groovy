import com.fasterxml.jackson.core.type.TypeReference
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import org.joda.time.*
import trello.Action

import java.util.concurrent.TimeUnit

import static karl.codes.Groovy.*
import static karl.codes.Jackson.*
import static karl.codes.Joda.*
import static karl.codes.Java.*
import static trello.Trello.*;

def secret = properties('secret.properties',[
        trello: [
                accessToken: 'authentication is required',
                secretToken: 'authentication is required',
                board: 'the board id to parse',
                listName: 'Working',
                listGravity: 'Lunch',
        ],
]).trello

RESTClient trello = new RESTClient(baseURI)
trello.auth.oauth(*signer(secret));
trello.ignoreSSLIssues()

DateTimeZone localZone = DateTimeZone.getDefault()

String since = DateTime.now()
        .withDayOfWeek(DateTimeConstants.MONDAY)
        .withTimeAtStartOfDay()
        .with { it.isAfter( DateTime.now() ) ? it.minusWeeks(1) : it }
        .withZone(DateTimeZone.UTC) // local start of last Monday, in UTC
        .with trelloFormat.&print

println 'requesting...'
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
        .findAll {it.type == 'updateCard' && it.data.listAfter} // repeat of query filter updateCard:idList
        .reverse().inject([:].withDefault{[]}) { result, event ->
            def events = result[event.data.card.name]

            if(events.size) {
                assert events[-1].name == event.data.listBefore.name
                events[-1].date = new Interval(events[-1].date,event.date.withZone(localZone))
            }

            events << [name: event.data.listAfter.name, date: event.date.withZone(localZone)]

            result}

def eventBeforeGravity, gravity, eventAfterGravity
LocalDate lastDay

// endcap all open date ranges
timelines.each { project, List events ->
    events[-1].date = new Interval(events[-1].date,DateTime.now())

//    int[] f = events.inject([:]) { days, event ->
//
//    }

    // collapse breaks before & after gravity into gravity
    events.each { event ->
        LocalDate day = event.date.start.toLocalDate()

        if(event.name == secret.listName) {
            // target list, immutable day start & end
            if(!lastDay || lastDay.isBefore(day)) {
                lastDay = day
                eventBeforeGravity = gravity = eventAfterGravity = null
            }

            if(!eventBeforeGravity) {
                eventBeforeGravity = event
            }


        } else {
//            if(!gravity || event.name == )
        }
    }
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