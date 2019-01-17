package Recording;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public interface Recorder {

    void recordForLimitedTime(int amount, TimeUnit unit) throws IOException, InterruptedException;

    void startRecording() throws IOException;

    void stopRecording() throws IOException, InterruptedException;

    void setFps(int fps);

    boolean setDirectoryToSaveRecordings(Path directoryPath);

    boolean isRecording();
}
