package Config.RecorderSpecific.Reader;

import Config.ConfigurationFileReader;
import Config.RecorderSpecific.RecorderJsonKeyConstants;
import Config.RecorderSpecific.RecorderValueDynamicConstans;

import java.nio.file.Path;

public class RecorderConfigurationFromFileReaderImpl implements RecorderConfigurationReader{
    private ConfigurationFileReader configurationFileReader = ConfigurationFileReader.getInstance();

    @Override
    public boolean isShouldAutoRemoveOld() {
        var shouldRemove = configurationFileReader.getPropertyAsBoolean(RecorderJsonKeyConstants.AUTO_REMOVE_OLD_RECORDING.getJsonKey());
        return shouldRemove.orElseThrow(() -> new ConfigValueCouldNotBeFoundException(RecorderJsonKeyConstants.AUTO_REMOVE_OLD_RECORDING.getJsonKey()));
    }

    @Override
    public Path getDirPathToSavedRecordings() {
        var dirPath = configurationFileReader.getPropertyAsPath(RecorderJsonKeyConstants.PATH_TO_SAVED_RECORDING.getJsonKey());
        return dirPath.orElseThrow(() -> new ConfigValueCouldNotBeFoundException(RecorderJsonKeyConstants.PATH_TO_SAVED_RECORDING.getJsonKey()));
    }

    @Override
    public Path getFfmpegBinPath() {
        var ffmpegConfigValue = configurationFileReader.getPropertyAsPath(RecorderJsonKeyConstants.FFMPEG_PATH_BIN.getJsonKey());
        return ffmpegConfigValue.isPresent() ? ffmpegConfigValue.get() : RecorderValueDynamicConstans.getFfmpegBinPath();
    }

    @Override
    public int getFps() {
        var fpsConfigValue = configurationFileReader.getPropertyAsInteger(RecorderJsonKeyConstants.FPS.getJsonKey());
        return fpsConfigValue.orElseThrow(() -> new ConfigValueCouldNotBeFoundException(RecorderJsonKeyConstants.FPS.getJsonKey()));
    }

    private static class ConfigValueCouldNotBeFoundException extends RuntimeException {

        ConfigValueCouldNotBeFoundException(String message) {
            super(createMessageExceptionString(message));
        }

        ConfigValueCouldNotBeFoundException(String message, Throwable cause) {
            super(createMessageExceptionString(message), cause);
        }

        private static String createMessageExceptionString(String missingConfigValue) {
            return "Could not locate config value: " + missingConfigValue + " in the config file, please add it";
        }

    }
}
