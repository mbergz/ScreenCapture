package Config.RecorderSpecific.Writer;

import Config.ConfigurationFileWriter;
import Eventhandlers.EventHandler;

import java.nio.file.Path;

import static Eventhandlers.Event.*;

public class RecorderConfigurationFromFileWriterImpl implements RecorderConfigurationWriter {
    private static ConfigurationFileWriter configurationFileWriter = ConfigurationFileWriter.getInstance();
    private static EventHandler eventHandler = EventHandler.getInstance();

    public void setAutoRemovalOfOldRecording(boolean shouldRemove) {
        eventHandler.dispatchEvent(RECORDING_NEW_CONFIGURATION_AUTO_REMOVE_CHANGED);
    }

    public void setDirectoryToSaveRecordings(Path directoryPath) {
        eventHandler.dispatchEvent(RECORDING_NEW_CONFIGURATION_SAVE_DIR_CHANGED);
    }

    public void setFfmpegBinPath(Path ffmpegBinPath) {
        eventHandler.dispatchEvent(RECORDING_NEW_CONFIGURATION_FFMPEG_BIN_PATH_CHANGED);
    }

    public void setFps(int fps) {
        eventHandler.dispatchEvent(RECORDING_NEW_CONFIGURATION_FPS_CHANGED);
    }

    public void setMovieName(String newName) {
        eventHandler.dispatchEvent(RECORDING_NEW_CONFIGURATION_MOVIE_NAME_CHANGED);
    }

}
