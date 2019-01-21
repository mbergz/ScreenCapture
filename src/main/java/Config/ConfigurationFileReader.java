package Config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

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
        JsonElement element = getElementFromFile(key);
        if (element != null && !element.isJsonNull() && element.getAsString().length() >0) {
            return Optional.of(element.getAsInt());
        }
        return Optional.empty();
    }

    public Optional<Path> getPropertyAsPath(String key) {
        JsonElement element = getElementFromFile(key);
        if (element != null && !element.isJsonNull() && element.getAsString().length() >0) {
            return Optional.of(Paths.get(element.getAsString()));
        }
        return Optional.empty();
    }

    public Optional<Boolean> getPropertyAsBoolean(String key) {
        JsonElement element = getElementFromFile(key);
        if (element != null && !element.isJsonNull() && element.getAsString().length() >0) {
            return Optional.of(element.getAsBoolean());
        }
        return Optional.empty();
    }

    public Optional<String> getPropertyAsString(String key) {
        JsonElement element = getElementFromFile(key);
        if (element != null && !element.isJsonNull() && element.getAsString().length() >0) {
            return Optional.of(element.getAsString());
        }
        return Optional.empty();
    }

    private JsonElement getElementFromFile(String key) {
        Path path = Paths.get("config.json");
        try {
            String jsonString = FileUtils.readFileToString(path.toFile(), Charset.defaultCharset());
            JsonParser parser = new JsonParser();
            JsonObject object = parser.parse(jsonString).getAsJsonObject();
            return object.get(key);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}