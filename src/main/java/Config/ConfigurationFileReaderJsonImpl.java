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

/**
 * Singleton configuration file reader to read json a json config file
 */
public class ConfigurationFileReaderJsonImpl implements ConfigurationFileReader {
    private static ConfigurationFileReaderJsonImpl instance;
    private String configFileName;

    public static ConfigurationFileReaderJsonImpl getInstance(String configFileName){
        if (instance == null){
            instance = new ConfigurationFileReaderJsonImpl(configFileName);
            return instance;
        }
        return instance;
    }

    private ConfigurationFileReaderJsonImpl(String configFileName){
        this.configFileName = configFileName;
    }

    @Override
    public int getPropertyAsInt(String key) {
        return getPropertyAs(key, Integer::parseInt)
                .orElseThrow(() -> new ConfigValueCouldNotBeFoundException(key, configFileName));
    }

    @Override
    public Path getPropertyAsPath(String key) {
        return getPropertyAs(key, Paths::get)
                .orElseThrow(() -> new ConfigValueCouldNotBeFoundException(key, configFileName));
    }

    @Override
    public boolean getPropertyAsBoolean(String key) {
        return getPropertyAs(key, Boolean::valueOf)
                .orElseThrow(() -> new ConfigValueCouldNotBeFoundException(key, configFileName));
    }

    @Override
    public String getPropertyAsString(String key) {
        return getPropertyAs(key, Function.identity())
                .orElseThrow(() -> new ConfigValueCouldNotBeFoundException(key, configFileName));
    }

    private <T> Optional<T> getPropertyAs(String key, Function<String, T> converterFunction) {
        String element = getElementFromFile(key);
        if (element != null && element.length() > 0) {
            return Optional.of(converterFunction.apply(element));
        }
        return Optional.empty();
    }

    private String getElementFromFile(String key) {
        Path path = Paths.get(configFileName);
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

    private static class ConfigValueCouldNotBeFoundException extends RuntimeException {

        ConfigValueCouldNotBeFoundException(String message, String configFile) {
            super(createMessageExceptionString(message, configFile));
        }

        ConfigValueCouldNotBeFoundException(String message, String configFile, Throwable cause) {
            super(createMessageExceptionString(message, configFile), cause);
        }

        private static String createMessageExceptionString(String missingConfigValue, String configFile) {
            return "Could not locate config value: " + missingConfigValue + " in the config file, please add it to config file: " + configFile;
        }
    }
}