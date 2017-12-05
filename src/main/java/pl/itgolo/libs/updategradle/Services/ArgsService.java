package pl.itgolo.libs.updategradle.Services;

import java.util.HashMap;
import java.util.Map;

/**
 * The type Args service.
 */
public class ArgsService {

    /**
     * The Map args.
     */
    Map<String, Object> mapArgs;

    /**
     * The Args.
     */
    String[] args;

    /**
     * Instantiates a new Args service.
     *
     * @param args the args
     */
    public ArgsService(String[] args) {
        this.args = args;
        mapArgs = new HashMap<>();
        createMapArgs();
    }

    private void createMapArgs() {
        for (int i =0 ; i < args.length ; i++){
            String arg = args[i];
            if (arg.equals("=")){
                continue;
            } else if (!arg.contains("=")) {
                mapArgs.put(arg, "");
            } else if (arg.startsWith("=")){
                continue;
            } else if (arg.contains("=")){
                String[] pair = arg.split("=", 2);
                String key = pair[0];
                if (pair.length == 1){
                    mapArgs.put(key, "");
                } else {
                    String valueArg = pair[1];
                    mapArgs.put(key, valueArg);
                }
            }
        }
    }

    /**
     * To map map.
     *
     * @return the map
     */
    public Map<String,Object> toMap() {
        return mapArgs;
    }

    /**
     * Has arg boolean.
     *
     * @param keyArg the key arg
     * @return the boolean
     */
    public Boolean hasArg(String keyArg){
        return mapArgs.containsKey(keyArg);
    }

    /**
     * Get value arg string.
     *
     * @param keyArg the key arg
     * @return the string
     */
    public String getValueArg(String keyArg){
        if (hasArg(keyArg)){
            return ((String) mapArgs.get(keyArg));
        } else {
            return null;
        }
    }
}
