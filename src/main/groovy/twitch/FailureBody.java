package twitch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by karl on 9/3/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FailureBody {
    public List<ResponseError> errors;
}
