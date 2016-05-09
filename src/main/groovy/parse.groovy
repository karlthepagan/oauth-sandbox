import com.fasterxml.jackson.core.type.TypeReference
import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import oauth.signpost.OAuth
import oauth.signpost.basic.DefaultOAuthConsumer
import oauth.signpost.basic.DefaultOAuthProvider
import oauth.signpost.http.HttpResponse
import org.joda.time.*
import trello.Action
import trello.Trello

import java.util.concurrent.TimeUnit

import static karl.codes.Groovy.*
import static karl.codes.Jackson.*
import static karl.codes.Joda.*
import static karl.codes.Java.*
import static trello.Trello.*;

def secret = properties('secret.properties',[
        trello: [
                consumerKey: 'trello API key here: https://trello.com/app-key',
                consumerSecret: 'trello API secret here: https://trello.com/app-key',
                appName: 'make up an app name',
                scope: [
                        'read',
                        'write',
                        'account',
                ].join(','),
                accessToken: 'authentication is required',
                secretToken: 'authentication is required',
                board: 'the board id to parse',
                listName: 'Working',
                listGravity: 'Lunch',
        ],
]).trello

boolean lastWeek = true;

RESTClient trello = new RESTClient(baseURI)
trello.auth.oauth(*signer(secret));
trello.ignoreSSLIssues()

DateTimeZone localZone = DateTimeZone.getDefault()

String since = DateTime.now()
        .withDayOfWeek(DateTimeConstants.MONDAY)
        .withTimeAtStartOfDay()
        .with { (lastWeek || it.isAfter( DateTime.now() )) ? it.minusWeeks(1) : it }
        .withZone(DateTimeZone.UTC) // local start of last Monday, in UTC
        .with trelloFormat.&print

println 'requesting...'
List<Action> actions
try {
    actions = trello.get(
            path: "boards/${secret.board}/actions",
            query: [
                    filter: 'updateCard:idList',
                    limit : 1000,
                    since : since
            ])
            { HttpResponseDecorator resp ->
                return json.readValue(resp.entity.content, new TypeReference<List<Action>>() {})
            }
} catch (HttpResponseException e) {
    if(e.statusCode == 401) {
        ignoreSSLIssues()

        Scanner scanner = new Scanner(System.in)

        DefaultOAuthConsumer consumer = new DefaultOAuthConsumer(*signer(secret)[0..1])
        DefaultOAuthProvider provider = new DefaultOAuthProvider(*Trello.oauthMethods)
        provider.retrieveRequestToken(consumer, OAuth.OUT_OF_BAND)
        println "${Trello.oauthMethods[2]}?oauth_token=${consumer.token}&callback_url=oob&name=${secret.appName}&scope=${secret.scope}"
        println 'that looks like an error, but just copy the value in the url'
        print 'oauth_verifier>> '
        provider.retrieveAccessToken(consumer, scanner.nextLine())

        println "trello.accessToken=${consumer.token}"
        println "trello.secretToken=${consumer.tokenSecret}"
    }
    return
}

class ParseEvent {
    String name
    MutableInterval date
}

// map events into timelines for each card
// join move events to create date ranges
Map<String,List<ParseEvent>> timelines = actions
        .findAll {it.type == 'updateCard' && it.data.listAfter} // repeat of query filter updateCard:idList
        .reverse().inject([:].withDefault{[]}) { result, action ->
            def events = result[action.data.card.name]

            if(events.size) {
                assert events[-1].name == action.data.listBefore.name
                events[-1].date.setEnd(action.date.withZone(localZone))
            }

            events << ([name: action.data.listAfter.name,
                       date: new MutableInterval(action.date.withZone(localZone),DateTime.now())] as ParseEvent)

            result}

LocalDate lastDay

timelines.each { project, events ->
//    int[] f = events.inject([:]) { days, event ->
//
//    }

//    return;
    List<ParseEvent> gravityFluid = []

    ParseEvent gravity

    // collapse breaks before & after gravity into gravity
    events.addAll events.collectMany { ParseEvent event ->
        LocalDate day = event.date.start.toLocalDate()

        if(event.name == secret.listName) {
            // target list, immutable day start & end
            if (!lastDay || lastDay.isBefore(day)) {
                lastDay = day
                // reset massless objects (breaks)
                gravityFluid = []
                // reset gravity object (start of day, end of lunch)
                // this is our start anchor
                gravity = event
            } else if(gravity != null) {
                // collapse towards gravity
                long shift = event.date.toDurationMillis()
                gravity.date.with {
                    setDurationAfterStart(shift + toDurationMillis())
                }
                event.date.setDurationAfterStart(0)

                // shift massless events
                gravityFluid.each {
                    long x = it.date.toDurationMillis()
                    it.date.with {
                        setInterval(getStartMillis() + shift, getStartMillis() + shift + x)
                    }
                }
            }
        } else if(event.name == secret.listGravity) {
            long time = event.date.endMillis
            // target gravity, collapse mass to abut this event
            gravity = [
                    name: secret.listName,
                    date: new MutableInterval(time, time)
            ] as ParseEvent
            gravityFluid = []
            return [gravity]
        } else {
            // collect events with mass
            gravityFluid << event
        }
        return []
    }

    events.sort { it.date.startMillis }
}

def totals = timelines.collect() { project, it ->
    def timeline = it.inject([:].withDefault{ new Duration(0) }) { totals, event ->
        if(!event.date.toDurationMillis()) return totals

        Duration duration = event.date.toDuration()

        print "$project: "
        print durationFormat.print(duration.toPeriod())
        print "\n\t\t$event.name at "
        println workTimeFormat.print(event.date.start)

        if (event.name == secret.listName) {
            LocalDate day = event.date.start.toLocalDate()
            totals[day] += duration
            totals.week += duration
        }

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