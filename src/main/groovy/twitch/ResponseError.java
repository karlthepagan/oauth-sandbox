package twitch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by karl on 9/3/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseError {
    public int status;
    public String detail;
}
