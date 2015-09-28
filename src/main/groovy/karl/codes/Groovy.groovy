package karl.codes

import org.codehaus.groovy.runtime.DefaultGroovyMethods

/**
 * Created by karl on 9/28/15.
 */
class Groovy {
    public static final ConfigObject properties(String path) throws IOException {
        return properties(path,Collections.emptyMap());
    }

    public static final ConfigObject properties(String path, Map required) throws IOException {
        Properties props = new Properties();
        FileInputStream file = new FileInputStream(new File(path));
        props.load(file);

        ConfigObject config = new ConfigSlurper().parse(props);

        if(!required.isEmpty()) {
            require(config,required);
        }

        return config;
    }

    static final void require(ConfigObject config, Map required) {
        List<String> errors = new ArrayList<String>();
        require(errors,'',config,required);
        if(errors) {
            System.err.println('***ERRORS***')
            errors.each {
                System.err.println(it)
            }
            throw new RuntimeException("${errors.size()} errors")
        }
    }

    static final void require(List<String> errors, String baseName, ConfigObject config, Map required) {
        required.each { key, value ->
            String printKey = baseName ? "$baseName.$key" : key
            if(value instanceof Map && config[key] instanceof ConfigObject)
                require(errors,printKey,config[key] as ConfigObject,value as Map)
            else if(!config[key] || config[key] instanceof ConfigObject)
                errors << "$printKey=$value"
        }
    }
}
