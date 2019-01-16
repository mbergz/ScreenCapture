import java.io.IOException;

import Recording.FfmpegRecorder;
import ui.RecorderSystemTray;

public class Launcher {

    public static void main(String[] args) throws IOException, InterruptedException {
        /*Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                new ProcessBuilder("taskkill", "/f", "/im", "ffmpeg.exe").start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        */
        FfmpegRecorder recorder = new FfmpegRecorder();
        new RecorderSystemTray(recorder);
    }
}
