package karl.codes;

import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by karl on 9/24/15.
 */
public class Groovy {
    public static ConfigObject properties(String path) throws IOException {
        Properties props = new Properties();
        FileInputStream file = new FileInputStream(new File(path));
        props.load(file);

        return new ConfigSlurper().parse(props);
    }
}
