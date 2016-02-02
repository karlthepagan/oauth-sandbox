//import com.mojang.authlib.Agent
//import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
//import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication
//
//import static karl.codes.Groovy.properties
//
//def secret = properties('secret.properties',[
//        minecraft: [
//                account: 'your minecraft.net username or mojang.com email address',
//                password: 'your minecraft password',
//        ],
//]).minecraft
//
//def auth = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "1")
//        .createUserAuthentication(Agent.MINECRAFT) as YggdrasilUserAuthentication
//
//auth.username = secret.account
//auth.password = secret.password
//
//auth.logIn()
//// exceptions are flow control
//
//println "minecraft.accessToken=${auth.authenticatedToken}"
//println "minecraft.uuid=${auth.selectedProfile.id.toString().replace('-','')}"
//println "minecraft.username=${auth.selectedProfile.name}"
//println "minecraft.userType=${auth.userType.name}"
//// also see auth.userProperties?
