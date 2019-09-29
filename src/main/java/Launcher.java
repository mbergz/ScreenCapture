import Config.RecorderSpecific.Reader.RecorderConfigurationFromFileReaderImpl;
import Config.RecorderSpecific.Writer.RecorderConfigurationFromFileWriterImpl;
import Recording.FfmpegRecorder;
import ui.RecorderSystemTray;

public class Launcher {

    public static void main(String[] args) {
        FfmpegRecorder recorder = new FfmpegRecorder(
                new RecorderConfigurationFromFileReaderImpl(),
                new RecorderConfigurationFromFileWriterImpl());
        new RecorderSystemTray(recorder);
    }

}
