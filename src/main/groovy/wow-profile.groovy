import groovyx.net.http.HttpResponseDecorator
import groovyx.net.http.RESTClient
import bnet.BattleNet

import static karl.codes.Groovy.properties

def secret = properties('secret.properties',[
        bnet: [
                accessToken: 'authentication is required',
        ],
]).bnet

RESTClient bnet = new RESTClient(BattleNet.baseURI)
bnet.headers = [
        Authorization: "Bearer ${secret.accessToken}"
]

String body = bnet.get(
        path: "profile/user",
//        path: "wow/user/characters",
//        query: [
//                locale: 'en_US',
//                apikey: secret.bnet.consumerKey,
//        ]
) { HttpResponseDecorator resp, data ->
    return data.toString()
}

println body