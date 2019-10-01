package Config;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigurationFileWriter {
    private static ConfigurationFileWriter instance;

    public static ConfigurationFileWriter getInstance(){
        if (instance == null){
            instance = new ConfigurationFileWriter();
            return instance;
        }
        return instance;
    }

    private ConfigurationFileWriter(){}

    /**
     * Writes new value to existing key, or creates a new if none exists
     *
     * @param key - json key
     * @param value - value to write
     */
    @SuppressWarnings("unchecked")
    public void writeNewConfigurationItem(String key, String value) {
        Path path = Paths.get("config.json");
        try {
            String jsonString = FileUtils.readFileToString(path.toFile(), Charset.defaultCharset());
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonString);
            json.put(key, value);
            FileUtils.writeStringToFile(path.toFile(), json.toJSONString(), Charset.defaultCharset());
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
}
