package twitch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by karl on 9/4/16.
 */
@JsonIgnoreProperties({"command","room", "color"})
public class ChatAttributes {
    public boolean deleted;
    public long timestamp;
    public String message;
    public String from;
    public ChatTags tags;
}
