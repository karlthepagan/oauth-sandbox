package soundcloud;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URI;
import java.net.URL;

/**
 * Created by karl on 5/8/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Me {
    public int id;
    public String permalink;
    public String username;
    public URI uri;
    public URL permalink_url;
    public URL avatar_url;
    public String country;
    public String full_name;
    public String city;
    public String description;
    @JsonProperty("discogs-name")
    public String discogsName;
    @JsonProperty("myspace-name")
    public String myspaceName;
}
