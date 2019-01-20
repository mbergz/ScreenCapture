package Config.RecorderSpecific.Reader;

import Config.ConfigurationFileReader;
import Config.RecorderSpecific.RecorderJsonKeyConstants;

import java.nio.file.Path;

public class RecorderConfigurationFromFileReaderImpl implements RecorderConfigurationReader{
    private ConfigurationFileReader configurationFileReader = ConfigurationFileReader.getInstance();

    @Override
    public boolean isShouldAutoRemoveOld() {
        var shouldRemove = configurationFileReader.getPropertyAsBoolean(RecorderJsonKeyConstants.AUTO_REMOVE_OLD_RECORDING.getJsonKey());
        return shouldRemove.orElse(RecorderDefaultValueConstans.SHOULD_AUTOREMOVE);
    }

    @Override
    public Path getDirPathToSavedRecordings() {
        var dirPath = configurationFileReader.getPropertyAsPath(RecorderJsonKeyConstants.PATH_TO_SAVED_RECORDING.getJsonKey());
        return dirPath.orElse(RecorderDefaultValueConstans.DIR_TO_SAVED_RECORDING);
    }

    @Override
    public Path getFfmpegBinPath() {
        var ffmpegConfigValue = configurationFileReader.getPropertyAsPath(RecorderJsonKeyConstants.FFMPEG_PATH_BIN.getJsonKey());
        return ffmpegConfigValue.isPresent() ? ffmpegConfigValue.get() : RecorderDefaultValueConstans.getFfmpegBinPath();
    }

    @Override
    public int getFps() {
        return RecorderDefaultValueConstans.FRAMERATE;
    }

    @Override
    public String getMovieName() {
        return RecorderDefaultValueConstans.MOVIE_NAMME;
    }

}
