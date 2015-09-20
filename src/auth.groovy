import oauth.signpost.OAuth
import oauth.signpost.basic.DefaultOAuthConsumer
import oauth.signpost.basic.DefaultOAuthProvider
import trello.Trello

import static karl.codes.Groovy.properties
import static karl.codes.OAuth.*

// https://github.com/jgritman/httpbuilder/wiki/Authentication

def secret = properties('secret.properties')
Scanner scanner = new Scanner(System.in)

DefaultOAuthConsumer consumer = new DefaultOAuthConsumer(*signer(secret.trello)[0..1])
DefaultOAuthProvider provider = new DefaultOAuthProvider(*Trello.oauthMethods)
provider.retrieveRequestToken(consumer, OAuth.OUT_OF_BAND)
println "${Trello.oauthMethods[2]}?oauth_token=${consumer.token}&callback_url=oob&name=${secret.appName}&scope=read"
println 'that looks like an error, but just copy the value in the url'
print 'oauth_verifier>> '
provider.retrieveAccessToken(consumer, scanner.nextLine())

println "trello.accessToken=${consumer.token}"
println "trello.secretToken=${consumer.tokenSecret}"
