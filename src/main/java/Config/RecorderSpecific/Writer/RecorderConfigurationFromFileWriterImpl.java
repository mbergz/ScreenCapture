package Config.RecorderSpecific.Writer;

import Config.ConfigurationFileWriter;
import Config.RecorderSpecific.RecorderJsonKeyConstants;
import Config.RecorderSpecific.RecorderValueDynamicConstans;
import Eventhandlers.EventHandler;

import java.nio.file.Path;

import static Eventhandlers.Event.*;

public class RecorderConfigurationFromFileWriterImpl implements RecorderConfigurationWriter {
    private static ConfigurationFileWriter configurationFileWriter = ConfigurationFileWriter.getInstance();
    private static EventHandler eventHandler = EventHandler.getInstance();

    public void setAutoRemovalOfOldRecording(boolean shouldRemove) {
        configurationFileWriter.writeNewConfigurationItem(RecorderJsonKeyConstants.AUTO_REMOVE_OLD_RECORDING.getJsonKey(), shouldRemove);
        eventHandler.dispatchEvent(RECORDING_NEW_CONFIGURATION_AUTO_REMOVE_CHANGED);
    }

    public void setDirectoryToSaveRecordings(Path directoryPath) {
        configurationFileWriter.writeNewConfigurationItem(RecorderJsonKeyConstants.PATH_TO_SAVED_RECORDING.getJsonKey(), directoryPath);
        eventHandler.dispatchEvent(RECORDING_NEW_CONFIGURATION_SAVE_DIR_CHANGED);
    }

    public void setFfmpegBinPath(Path ffmpegBinPath) {
        configurationFileWriter.writeNewConfigurationItem(RecorderJsonKeyConstants.FFMPEG_PATH_BIN.getJsonKey(), ffmpegBinPath);
        eventHandler.dispatchEvent(RECORDING_NEW_CONFIGURATION_FFMPEG_BIN_PATH_CHANGED);
    }

    public void setFps(int fps) {
        configurationFileWriter.writeNewConfigurationItem(RecorderJsonKeyConstants.FPS.getJsonKey(), String.valueOf(fps));
        eventHandler.dispatchEvent(RECORDING_NEW_CONFIGURATION_FPS_CHANGED);
    }
}
