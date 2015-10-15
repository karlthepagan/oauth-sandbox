import org.apache.oltu.oauth2.common.message.types.ResponseType
import twitch.Twitch
import org.apache.oltu.oauth2.client.OAuthClient
import org.apache.oltu.oauth2.client.URLConnectionClient
import org.apache.oltu.oauth2.client.request.OAuthClientRequest
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse
import org.apache.oltu.oauth2.common.message.types.GrantType

import static karl.codes.Groovy.*

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

Scanner scanner = new Scanner(System.in)

OAuthClientRequest authzRequest = OAuthClientRequest
        .authorizationLocation(Twitch.oauthMethods[2])
        .setClientId(secret.consumerKey)
        .setScope(secret.scope)
        .setState('ABCDE')
        .setRedirectURI(secret.redirect)
        .setResponseType(ResponseType.CODE.toString())
        .buildQueryMessage()

println authzRequest.getLocationUri()


println 'that looks like an error, but just copy the value in the url'
print 'oauth_verifier>> '
String code = scanner.nextLine()

OAuthClientRequest tokenRequest = OAuthClientRequest
        .tokenLocation(Twitch.oauthMethods[0])
        .setClientId(secret.consumerKey)
        .setClientSecret(secret.consumerSecret)
        .setScope(secret.scope)
        .setGrantType(GrantType.AUTHORIZATION_CODE)
        .setRedirectURI(secret.redirect)
        .setCode(code)
        .buildQueryMessage()

OAuthClient oauth2 = new OAuthClient(new URLConnectionClient())

OAuthJSONAccessTokenResponse token = oauth2.accessToken(tokenRequest)




// auth for IRC connection
authzRequest = OAuthClientRequest
        .authorizationLocation(Twitch.oauthMethods[2])
        .setClientId(secret.consumerKey)
        .setScope('chat_login')
        .setState('ABCDE')
        .setRedirectURI(secret.redirect)
        .setResponseType(ResponseType.TOKEN.toString()) // token in url fragment (implicit token grant)
        .buildQueryMessage()

println authzRequest.getLocationUri()

println 'get access_token from url fragment!'
print 'access_token>> '
String chatToken = scanner.nextLine()

println "twitch.accessToken=${token.accessToken}"
// don't forget application/vnd.twitchtv[.version]+json
println "twitch.chatToken=oauth:${chatToken}"
