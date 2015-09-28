package trello;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by karl on 9/24/15.
 */
public class Trello {
    public static final String baseURI = "https://trello.com/1/";
    public static final List<String> oauthMethods = Arrays.asList(
            "https://trello.com/1/OAuthGetRequestToken",
            "https://trello.com/1/OAuthGetAccessToken",
            "https://trello.com/1/OAuthAuthorizeToken"
    );
}
