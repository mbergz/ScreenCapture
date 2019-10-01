package Config;

import java.nio.file.Path;

public interface ConfigurationFileReader {

    int getPropertyAsInt(String key);

    Path getPropertyAsPath(String key);

    boolean getPropertyAsBoolean(String key);

    String getPropertyAsString(String key);
}
