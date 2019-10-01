package Config.RecorderSpecific.ffmpeg.Writer;

import Config.ConfigurationFileWriter;
import Config.RecorderSpecific.ffmpeg.FfmpegProcessArgument;

public class RecorderConfigurationFromFileWriterImpl implements RecorderConfigurationWriter {
    private static ConfigurationFileWriter configurationFileWriter = ConfigurationFileWriter.getInstance();

    @Override
    public void setProcessArgumentProperty(FfmpegProcessArgument key, String newValue) {
        configurationFileWriter.writeNewConfigurationItem(key.key(), newValue);
    }

    @Override
    public void setGeneralProperty(String key, String newValue) {
        configurationFileWriter.writeNewConfigurationItem(key, newValue);
    }
}
