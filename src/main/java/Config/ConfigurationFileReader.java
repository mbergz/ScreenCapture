package Config;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;

public class ConfigurationFileReader {
    private static ConfigurationFileReader instance;

    public static ConfigurationFileReader getInstance(){
        if(instance == null){
            instance = new ConfigurationFileReader();
            return instance;
        }
        return instance;
    }

    private ConfigurationFileReader(){}

    public Optional<Integer> getPropertyAsInteger(String key) {
        return getPropertyAs(key, Integer::parseInt);
    }

    public Optional<Path> getPropertyAsPath(String key) {
        return getPropertyAs(key, Paths::get);
    }

    public Optional<Boolean> getPropertyAsBoolean(String key) {
        return getPropertyAs(key, Boolean::valueOf);
    }

    public Optional<String> getPropertyAsString(String key) {
        return getPropertyAs(key, Function.identity());
    }

    private <T> Optional<T> getPropertyAs(String key, Function<String, T> converterFunction) {
        String element = getElementFromFile(key);
        if (element != null && element.length() > 0) {
            return Optional.of(converterFunction.apply(element));
        }
        return Optional.empty();
    }

    private String getElementFromFile(String key) {
        Path path = Paths.get("config.json");
        try {
            String jsonString = FileUtils.readFileToString(path.toFile(), Charset.defaultCharset());
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonString);
            return (String) json.get(key);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}