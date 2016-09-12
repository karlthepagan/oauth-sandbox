package twitch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by karl on 9/4/16.
 */
@JsonIgnoreProperties({"type","links"})
public class ChatEvent {
    public String id;
    public ChatAttributes attributes;
}
