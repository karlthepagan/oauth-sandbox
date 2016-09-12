import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import twitch.ChatBody

import java.util.concurrent.ConcurrentHashMap
import java.util.zip.GZIPOutputStream

import static karl.codes.Jackson.*
import static karl.codes.Joda.*
import static karl.codes.Java.*
import static karl.codes.Groovy.*



def video = 'https://www.twitch.tv/towelliee/v/73399050'
//'https://www.twitch.tv/pax/v/87670508' (triforce quartet)

boolean safe = true // safe mode tries extra hard to eliminate gaps in the data



def secret = properties('secret.properties',[
        twitch: [
                consumerKey: 'twitch API key from http://www.twitch.tv/kraken/oauth2/clients/new',
                consumerSecret: 'twitch API secret from http://www.twitch.tv/kraken/oauth2/clients/new',
                appName: 'make up an app name',
                redirect: 'specify your redirect (must match!)',
                scope: ['user_blocks_edit',
                        'user_blocks_read',
                        'channel_editor',
                        'channel_commercial',
                        'channel_subscriptions',
                        'channel_check_subscription',
                        'chat_login',
                ].join(' '),
        ],
]).twitch


RESTClient rechat = new RESTClient('https://rechat.twitch.tv/')
rechat.handler.failure = { HttpResponseDecorator resp, Object data ->
    resp.setData(data)
    return resp
}

// rosenbrl
// TODO get video id for quartet

int delta = -100
long offset = 0
long limit = Long.MAX_VALUE
long storeLimit = Long.MAX_VALUE
long start = 0

def vid = ~'https://www.twitch.tv/([^/]+)/v/(.*)'
def range = ~/(-?\d+) is not between (-?\d+) and (-?\d+)/

def video_id = 'v' + vid.matcher(video)[0][2]

def hits = new ConcurrentHashMap<String,Boolean>()

def out = new PrintStream(new GZIPOutputStream(new FileOutputStream('chatlog.txt.gz')))

def stats = true?null:new PrintStream(new FileOutputStream('stats.csv')).with {
    it.println('min, max')
    it
}

def prev = [:]

def duration = 0

def ranges = []

def remainder = 0
def frameSize = safe?0:30

while(offset < limit) {
    def rand = Random.newInstance()

    def resp = rechat.get(
            path: 'rechat-messages',
            query: [
                    start: offset,
                    video_id: video_id
            ]) { HttpResponseDecorator resp ->

        return json.readValue(resp.entity.content, ChatBody.class)
    }

    if(resp instanceof HttpResponseDecorator && resp.status == 400) {
        def m = range.matcher(resp.data.errors[0].detail.toString())
        if(!m.find()) {
            println(resp)
        }
        remainder = m.group(1) as int // remainder?
        offset = m.group(2) as int
        limit = m.group(3) as int
        storeLimit = (limit + 1) * 1000

        start = offset
        duration = limit - offset

        delta = -1

        continue;
    }

    def chat = resp as ChatBody
    def dupes = 0

    chat.data.eachWithIndex { it, index ->
        if(safe && hits.putIfAbsent(it.id,Boolean.TRUE)) {
            if(index == 0) {
                ranges[-1].add((offset - start) * 1000)
            }

            dupes++

            // TODO why did we repeat?
            return;
        }

        if(frameSize == 0 && start != offset) {
            // found end of frame
            frameSize = offset - start
        }

        if(index == 0) {
            ranges[ranges.size] = [(offset - start) * 1000]
        }

        ranges[-1].add(it.attributes.timestamp - (start * 1000))

        if(it.attributes.deleted) {
            return;
        }

        if(it.attributes.timestamp > storeLimit) {
            // TODO end is actually significantly earlier :/
            return;
        }

        out.print(it.attributes.timestamp)
        out.print(' ')
        out.print(it.attributes.from)
        out.print(': ')
        out.println(it.attributes.message)
    }

    def first = chat.data.min { it.attributes.timestamp }
    def last = chat.data.max { it.attributes.timestamp }

    prev = [
            first: first.attributes.timestamp,
            last: last.attributes.timestamp,
            offset: offset,
            prev: prev.with { it.prev = null; it; },
    ]

    if(prev.offset == prev.prev.offset || prev.offset < prev.prev.offset) {
        println('got stuck! whyyyy' + prev)
        break; // derps
    }

    long min = stats?ranges[-1].min():0
    long max = stats?ranges[-1].max():0

    print((100 - (limit - offset) * 100 / duration as double).round(2))
    print(' ')
    print(chat.data.size())
    if(dupes) {
        print(' (')
        print(dupes)
        print(')')
    }
    print(' at ')
    print(offset - start)
    print(' - ')
    [System.out,stats].findAll{it}.each {
        it.print(min)
        it.print(', ')
        it.println(max)
    }

    if(frameSize) {
        offset += frameSize
    } else {
        offset = Math.max(offset - delta, last?.attributes?.timestamp ?
                Math.ceil(last.attributes.timestamp * 0.001) as long : 0l)
    }

    sleep(rand.nextInt(400) + 200)
}

print('end at ')
println(offset)

out.close()