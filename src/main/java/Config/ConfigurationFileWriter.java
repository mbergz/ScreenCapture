package Config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigurationFileWriter {
    private static ConfigurationFileWriter instance;

    public static ConfigurationFileWriter getInstance(){
        if(instance == null){
            instance = new ConfigurationFileWriter();
            return instance;
        }
        return instance;
    }

    private ConfigurationFileWriter(){}

    public void writeNewConfigurationItem(String key, String value) {
        Path path = Paths.get("config.json");
        try {
            String jsonString = FileUtils.readFileToString(path.toFile(), Charset.defaultCharset());
            JsonParser parser = new JsonParser();
            JsonObject object = parser.parse(jsonString).getAsJsonObject();
            JsonElement element = object.get(key);
            if (element == null) {
                object.add(key, parser.parse(value));
            } else {
                // exists already
                object.remove(key);
                object.add(key, parser.parse(value));
            }
            FileUtils.writeStringToFile(path.toFile(), object.getAsString(), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
