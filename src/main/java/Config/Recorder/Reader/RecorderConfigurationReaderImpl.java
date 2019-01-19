package Config.Recorder.Reader;

import Config.ConfigurationFileReader;

import java.nio.file.Path;

public class RecorderConfigurationReaderImpl implements RecorderConfigurationReader{
    private ConfigurationFileReader configurationFileReader = ConfigurationFileReader.getInstance();

    // TODO
    @Override
    public boolean isShouldAutoRemoveOld() {
        return false;
    }

    // TODO
    @Override
    public Path getDirPathToSavedRecordings() {
        return null;
    }

    // TODO
    @Override
    public Path getFfmpegBinPath() {
        return null;
    }

    // TODO
    @Override
    public int getFps() {
        return 0;
    }

    // TODO
    @Override
    public String getMovieName() {
        return null;
    }
}
