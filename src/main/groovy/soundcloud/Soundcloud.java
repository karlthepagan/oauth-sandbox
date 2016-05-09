package soundcloud;

import java.util.Arrays;
import java.util.List;

/**
 * Created by karl on 5/8/2016.
 */
public class Soundcloud {
    public static final String baseURI = "http://api.soundcloud.com/";
    public static final List<String> oauthMethods = Arrays.asList(
            "http://api.soundcloud.com/oauth2/token",
            "http://api.soundcloud.com/oauth2/token",
            "http://api.soundcloud.com/oauth2/authorize"
    );
}
