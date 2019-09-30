package Config.RecorderSpecific.Writer;

import Config.ConfigurationFileWriter;
import Config.RecorderSpecific.RecorderJsonKeyConstants;

public class RecorderConfigurationFromFileWriterImpl implements RecorderConfigurationWriter {
    private static ConfigurationFileWriter configurationFileWriter = ConfigurationFileWriter.getInstance();

    @Override
    public void setProperty(RecorderJsonKeyConstants key, String newValue) {
        configurationFileWriter.writeNewConfigurationItem(key.getJsonKey(), newValue);
    }
}
