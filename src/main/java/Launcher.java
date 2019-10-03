import Config.ConfigurationFileReader;
import Config.ConfigurationFileReaderJsonImpl;
import Config.RecorderSpecific.ffmpeg.Writer.RecorderConfigurationFromFileWriterImpl;
import Recording.FfmpegRecorder;
import Recording.ProcessRecorder;
import ui.RecorderSystemTray;

public class Launcher {

    public static void main(String[] args) {
        ConfigurationFileReader configurationFileReader = ConfigurationFileReaderJsonImpl.getInstance("config.json");
        // verify config and default config exists

        ProcessRecorder recorder = new FfmpegRecorder(
                configurationFileReader,
                new RecorderConfigurationFromFileWriterImpl());
        new RecorderSystemTray(recorder, configurationFileReader);
    }

}
