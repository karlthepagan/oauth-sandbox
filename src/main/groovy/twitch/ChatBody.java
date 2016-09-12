package twitch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by karl on 9/4/16.
 */
@JsonIgnoreProperties({})
public class ChatBody {
    public List<ChatEvent> data;
    public ChatMeta meta;
}
