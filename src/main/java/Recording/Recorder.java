package Recording;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

// TODO
// Break out RecorderOptions to own interface/class, how to connect
public interface Recorder {

    void recordForLimitedTime(int amount, TimeUnit unit) throws IOException, InterruptedException;

    void startRecording() throws IOException;

    void stopRecording() throws IOException, InterruptedException;

    void setFps(int fps);

    boolean setDirectoryToSaveRecordings(Path directoryPath);

    boolean isRecording();

    void setAutoRemovalOfOldRecording(boolean shouldRemove);
}
