import karl.codes.Java
import oauth.signpost.OAuth
import oauth.signpost.basic.DefaultOAuthConsumer
import oauth.signpost.basic.DefaultOAuthProvider
import trello.Trello

import static karl.codes.Groovy.*
import static karl.codes.Java.*

// https://github.com/jgritman/httpbuilder/wiki/Authentication

ignoreSSLIssues()

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
        ],
]).trello

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
