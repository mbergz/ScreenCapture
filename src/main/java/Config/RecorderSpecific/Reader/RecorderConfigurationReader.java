package Config.RecorderSpecific.Reader;

import java.nio.file.Path;

public interface RecorderConfigurationReader {

    public boolean isShouldAutoRemoveOld();

    public Path getDirPathToSavedRecordings();

    public Path getFfmpegBinPath();

    public int getFps();

}
