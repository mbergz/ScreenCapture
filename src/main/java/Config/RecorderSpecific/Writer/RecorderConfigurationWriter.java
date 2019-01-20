package Config.RecorderSpecific.Writer;

import java.nio.file.Path;

public interface RecorderConfigurationWriter {

    public void setAutoRemovalOfOldRecording(boolean shouldRemove);

    public void setDirectoryToSaveRecordings(Path directoryPath);

    public void setFfmpegBinPath(Path ffmpegBinPath);

    public void setFps(int fps);

    public void setMovieName(String newName);

}
