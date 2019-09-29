package Config.RecorderSpecific.Writer;

import Config.ConfigurationFileWriter;
import Config.RecorderSpecific.RecorderJsonKeyConstants;

public class RecorderConfigurationFromFileWriterImpl implements RecorderConfigurationWriter {
    private static ConfigurationFileWriter configurationFileWriter = ConfigurationFileWriter.getInstance();

    public void setAutoRemovalOfOldRecording(String shouldRemove) {
        configurationFileWriter.writeNewConfigurationItem(RecorderJsonKeyConstants.AUTO_REMOVE_OLD_RECORDING.getJsonKey(), shouldRemove);
    }

    public void setDirectoryToSaveRecordings(String directoryPath) {
        configurationFileWriter.writeNewConfigurationItem(RecorderJsonKeyConstants.PATH_TO_SAVED_RECORDING.getJsonKey(), directoryPath);
    }

    public void setFfmpegBinPath(String ffmpegBinPath) {
        configurationFileWriter.writeNewConfigurationItem(RecorderJsonKeyConstants.FFMPEG_PATH_BIN.getJsonKey(), ffmpegBinPath);
    }

    public void setFps(String fps) {
        configurationFileWriter.writeNewConfigurationItem(RecorderJsonKeyConstants.FPS.getJsonKey(), fps);
    }
}
