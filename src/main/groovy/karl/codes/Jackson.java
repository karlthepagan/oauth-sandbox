package karl.codes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * Created by karl on 9/20/15.
 */
public interface Jackson {
    ObjectMapper json = new ObjectMapper().registerModule(new JodaModule());
}
