import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import soundcloud.*

import static karl.codes.Groovy.*
import static karl.codes.Jackson.*
import static karl.codes.Joda.*
import static karl.codes.Java.*

def secret = properties('secret.properties',[
        soundcloud: [
                consumerKey: 'used for client_id',
                accessToken: 'authentication is required',
        ],
]).soundcloud

RESTClient client = new RESTClient(Soundcloud.baseURI)
client.headers = [
        Accept: 'application/json'
]

Me me = client.get(
        path: "me",
        query: [
                oauth_token: secret.accessToken,
        ]
) { HttpResponseDecorator resp ->
    return json.readValue(resp.entity.content, Me.class)
}

println me.id