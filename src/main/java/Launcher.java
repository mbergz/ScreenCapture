import java.io.*;
import java.util.concurrent.TimeUnit;

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
        FfmpegRecorder recorder = new FfmpegRecorder(30);

        //recorder.startRecording();
        //Thread.sleep(4000);
        //recorder.stopRecording();
        recorder.recordForLimitedTime(5, TimeUnit.SECONDS);
    }
}
