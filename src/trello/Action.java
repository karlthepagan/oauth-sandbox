package trello;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.joda.time.DateTime;
import org.joda.time.Instant;

import java.util.Map;

/**
 * Created by karl on 9/18/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Action {
    public enum Type {
        updateCard,
        createCard,
        updateList,
        createList,
        createBoard,
    }

    public Map<String,Object> data;

    public Type type;
    public DateTime date;

    @JsonProperty("memberCreator")
    public Member creator;
}
