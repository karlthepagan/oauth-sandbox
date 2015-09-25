package trello;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.joda.time.DateTime;
import trello.actions.CreateCard;
import trello.actions.UpdateCard;

import java.util.Map;

/**
 * Created by karl on 9/18/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.EXISTING_PROPERTY, property="type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "updateCard", value = UpdateCard.class),
        @JsonSubTypes.Type(name = "createCard", value = CreateCard.class)
})
public class Action {
    public Map<String,Object> data;

    public String type;
    public DateTime date;

    @JsonProperty("memberCreator")
    public Member creator;
}
