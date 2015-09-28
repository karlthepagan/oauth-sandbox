import bnet.BattleNet
import org.apache.oltu.oauth2.client.OAuthClient
import org.apache.oltu.oauth2.client.URLConnectionClient
import org.apache.oltu.oauth2.client.request.OAuthClientRequest
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse
import org.apache.oltu.oauth2.common.message.types.GrantType;

import static karl.codes.Groovy.*

def secret = properties('secret.properties',[
        bnet: [
                consumerKey: 'bnet API key',
                consumerSecret: 'bnet API secret',
                appName: 'make up an app name',
                scope: 'read,write,account',
        ],
])

Scanner scanner = new Scanner(System.in)

OAuthClientRequest authzRequest = OAuthClientRequest
        .authorizationLocation(BattleNet.oauthMethods[2])
        .setClientId(secret.bnet.consumerKey)
        .setScope(secret.bnet.scope)
        .setState('ABCDE')
        .setRedirectURI('https://localhost:8443/code')
        .setResponseType('code')
        .buildQueryMessage()

println authzRequest.getLocationUri()


println 'that looks like an error, but just copy the value in the url'
print 'oauth_verifier>> '
String code = scanner.nextLine()

OAuthClientRequest tokenRequest = OAuthClientRequest
        .tokenLocation(BattleNet.oauthMethods[0])
        .setClientId(secret.bnet.consumerKey)
        .setClientSecret(secret.bnet.consumerSecret)
        .setScope(secret.bnet.scope)
        .setGrantType(GrantType.AUTHORIZATION_CODE)
        .setRedirectURI('https://localhost:8443/code')
        .setCode(code)
        .buildQueryMessage()

OAuthClient oauth2 = new OAuthClient(new URLConnectionClient())

OAuthJSONAccessTokenResponse token = oauth2.accessToken(tokenRequest)

println "bnet.accessToken=${token.accessToken}"
