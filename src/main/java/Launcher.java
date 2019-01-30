import Config.RecorderSpecific.Reader.RecorderConfigurationFromFileReaderImpl;
import Recording.FfmpegRecorder;
import ui.RecorderSystemTray;

public class Launcher {

    public static void main(String[] args) {
        FfmpegRecorder recorder = new FfmpegRecorder(new RecorderConfigurationFromFileReaderImpl());
        new RecorderSystemTray(recorder);
    }

}
