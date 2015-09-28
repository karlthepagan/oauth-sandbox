package trello;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by karl on 9/18/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Member {
    public String username;
}
