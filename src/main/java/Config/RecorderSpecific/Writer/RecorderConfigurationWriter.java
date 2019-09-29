package Config.RecorderSpecific.Writer;

import java.nio.file.Path;

public interface RecorderConfigurationWriter {

    public void setAutoRemovalOfOldRecording(String shouldRemove);

    public void setDirectoryToSaveRecordings(String directoryPath);

    public void setFfmpegBinPath(String ffmpegBinPath);

    public void setFps(String fps);

}
