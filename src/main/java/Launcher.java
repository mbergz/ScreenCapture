import Recording.FfmpegRecorder;
import ui.RecorderSystemTray;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class Launcher {
    private static int DEFAULT_FRAMERATE = 30;

    public static void main(String[] args) throws IOException, InterruptedException {
        /*Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                new ProcessBuilder("taskkill", "/f", "/im", "ffmpeg.exe").start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        */
        FfmpegRecorder recorder = new FfmpegRecorder(DEFAULT_FRAMERATE);
        new RecorderSystemTray(recorder);
    }
}
