import org.kitteh.irc.client.library.Client
import org.kitteh.irc.client.library.element.ServerMessage
import org.kitteh.irc.client.library.event.channel.ChannelJoinEvent
import org.kitteh.irc.client.library.event.helper.ServerMessageEvent
import org.kitteh.irc.lib.net.engio.mbassy.listener.Handler

import static karl.codes.Groovy.properties

/**
 * Created by karl on 10/24/15.
 */


class Listener {
    @Handler
    public void onUserJoinChannel(ChannelJoinEvent event) {
        if (event.getClient().isUser(event.getUser())) { // It's me!
            event.getChannel().sendMessage("Hello world! Kitteh's here to demand cuddles.");
            return;
        }
        // It's not me!
        event.getChannel().sendMessage("Welcome, " + event.getUser().getNick() + "! :3");
    }

    @Handler
    public void onServer(ServerMessageEvent event) {
        for(ServerMessage m : event.originalMessages) {
            println m.message
        }
    }
}


def secret = properties('secret.properties',[
        twitch: [
                chatToken: 'authentication required',
        ],
]).twitch

// Calling build() starts connecting.
Client client = Client.builder()
        .serverHost('irc.twitch.tv').serverPort(6667)
        .nick('karlthepagan').serverPassword(secret.chatToken)
        .build();
client.getEventManager().registerEventListener(new Listener());
client.addChannel('#karlthepagan');

Thread.sleep(1000);