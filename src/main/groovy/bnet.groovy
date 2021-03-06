import bnet.BattleNet
import org.apache.oltu.oauth2.client.OAuthClient
import org.apache.oltu.oauth2.client.URLConnectionClient
import org.apache.oltu.oauth2.client.request.OAuthClientRequest
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse
import org.apache.oltu.oauth2.common.message.types.GrantType
import org.apache.oltu.oauth2.common.message.types.ResponseType;

import static karl.codes.Groovy.*

def secret = properties('secret.properties',[
        bnet: [
                consumerKey: 'bnet API key from https://dev.battle.net/apps/mykeys',
                consumerSecret: 'bnet API secret from https://dev.battle.net/apps/mykeys',
                redirect: 'callback URL from https://dev.battle.net/apps/myapps -> EDIT',
                appName: 'make up an app name',
                scope: '',
        ],
]).bnet

Scanner scanner = new Scanner(System.in)

OAuthClientRequest authzRequest = OAuthClientRequest
        .authorizationLocation(BattleNet.oauthMethods[2])
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
        .tokenLocation(BattleNet.oauthMethods[0])
        .setClientId(secret.consumerKey)
        .setClientSecret(secret.consumerSecret)
        .setScope(secret.scope)
        .setGrantType(GrantType.AUTHORIZATION_CODE)
        .setRedirectURI(secret.redirect)
        .setCode(code)
        .buildQueryMessage()

OAuthClient oauth2 = new OAuthClient(new URLConnectionClient())

OAuthJSONAccessTokenResponse token = oauth2.accessToken(tokenRequest)

println "bnet.accessToken=${token.accessToken}"
