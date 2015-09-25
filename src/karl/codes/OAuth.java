package karl.codes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by karl on 9/24/15.
 */
public class OAuth {
    public static final List<String> signer(Map<String,String> properties) {
        return Arrays.asList(
                properties.get("consumerKey"),
                properties.get("consumerSecret"),
                properties.get("accessToken"),
                properties.get("secretToken"));
    }

    public static final List<String> pairs(Map<String,String> properties) {
        ArrayList<String> out = new ArrayList<>(properties.size() * 2);

        for(Map.Entry<String,String> e : properties.entrySet()) {
            out.add(e.getKey());
            out.add(e.getValue());
        }

        return out;
    }
}
