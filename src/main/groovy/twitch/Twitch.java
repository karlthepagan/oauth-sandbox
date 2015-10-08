package twitch;

import java.util.Arrays;
import java.util.List;

/**
 * Created by kpauls.nbe on 10/5/2015.
 */
public class Twitch {
    public static final String baseURI = "https://api.twitch.tv/kraken";
    public static final List<String> oauthMethods = Arrays.asList(
            "https://api.twitch.tv/kraken/oauth2/token",
            "https://api.twitch.tv/kraken/oauth2/token",
            "https://api.twitch.tv/kraken/oauth2/authorize"
    );
}
