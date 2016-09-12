package twitch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by karl on 9/4/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatTags {
    @JsonProperty("display-name")
    public String displayName;
}
