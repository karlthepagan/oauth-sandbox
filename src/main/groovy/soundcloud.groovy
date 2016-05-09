import org.apache.oltu.oauth2.common.message.types.ResponseType
import soundcloud.Soundcloud
import org.apache.oltu.oauth2.client.OAuthClient
import org.apache.oltu.oauth2.client.URLConnectionClient
import org.apache.oltu.oauth2.client.request.OAuthClientRequest
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse
import org.apache.oltu.oauth2.common.message.types.GrantType

import static karl.codes.Groovy.*

def secret = properties('secret.properties',[
        soundcloud: [
                consumerKey: 'soundcloud API key from http://soundcloud.com/you/apps',
                consumerSecret: 'soundcloud API secret from http://soundcloud.com/you/apps',
                appName: 'make up an app name',
                redirect: 'specify your redirect (must match!)',
                scope: [].join(' '),
        ],
]).soundcloud

Scanner scanner = new Scanner(System.in)

OAuthClientRequest authzRequest = OAuthClientRequest
        .authorizationLocation(Soundcloud.oauthMethods[2])
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
        .tokenLocation(Soundcloud.oauthMethods[0])
        .setClientId(secret.consumerKey)
        .setClientSecret(secret.consumerSecret)
        .setScope(secret.scope)
        .setGrantType(GrantType.AUTHORIZATION_CODE)
        .setRedirectURI(secret.redirect)
        .setCode(code)
        .buildQueryMessage()

OAuthClient oauth2 = new OAuthClient(new URLConnectionClient())

OAuthJSONAccessTokenResponse token = oauth2.accessToken(tokenRequest)

println "soundcloud.accessToken=${token.accessToken}"
