import Config.RecorderSpecific.Reader.RecorderConfigurationFromFileReaderImpl;
import Recording.FfmpegRecorder;
import ui.RecorderSystemTray;

import java.io.IOException;

public class Launcher {

    public static void main(String[] args) throws IOException, InterruptedException {
        FfmpegRecorder recorder = new FfmpegRecorder(new RecorderConfigurationFromFileReaderImpl());
        new RecorderSystemTray(recorder);
    }

}
