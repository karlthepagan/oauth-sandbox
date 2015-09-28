package bnet;

import java.util.Arrays;
import java.util.List;

/**
 * Created by karl on 9/29/15.
 */
public class BattleNet {
    public static final String baseURI = "https://us.api.battle.net/";
    public static final List<String> oauthMethods = Arrays.asList(
            "https://us.battle.net/oauth/token",
            "https://us.battle.net/oauth/token",
            "https://us.battle.net/oauth/authorize"
    );
}
