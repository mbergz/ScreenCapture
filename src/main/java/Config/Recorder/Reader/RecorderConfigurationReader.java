package Config.Recorder.Reader;

import java.nio.file.Path;

public interface RecorderConfigurationReader {

    public boolean isShouldAutoRemoveOld();

    public Path getDirPathToSavedRecordings();

    public Path getFfmpegBinPath();

    public int getFps();

    public String getMovieName();

}
