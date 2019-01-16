package Recording;

import Eventhandlers.Event;
import Eventhandlers.EventHandler;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class FfmpegRecorder implements Recorder{
    private static int DEFAULT_FRAMERATE = 30;
    private final String ffmpegPath = "c:\\Users\\Martin\\Documents\\ffmpeg\\ffmpeg-20190112-1ea5529-win64-static\\bin\\ffmpeg.exe";
    private int framerate = DEFAULT_FRAMERATE;
    private ProcessBuilder pb;
    private Process p;
    private InputStream errStream;
    private InputStream inStream;
    private OutputStream outStream;
    private EventHandler eventHandler;

    private volatile boolean isRecording = false;

    public FfmpegRecorder() {
        eventHandler = EventHandler.getInstance();
        setUpFfmpeg();
    }

    public FfmpegRecorder(int framerate) {
        this.framerate = framerate;
        setUpFfmpeg();
    }

    private void shutDownIfActive() {
        if (pb != null) {
            if (isRecording){
                try {
                    stopRecording();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (p != null) {
                p.destroy();
            }
        }
    }

    private void setUpFfmpeg() {
        shutDownIfActive();
        pb = new ProcessBuilder(ffmpegPath, "-f", "gdigrab", "-framerate", Integer.toString(framerate) , "-i", "desktop", "myTest123.mov");
    }

    /**
     * Must be called before recording is started
     *
     * @param dir
     */
    public void setWorkingDirectory(String dir) {
        pb.directory(new File("c:\\Users\\Martin\\Documents\\ffmpeg\\ffmpeg-20190112-1ea5529-win64-static\\bin"));
    }

    public void recordForLimitedTime(int amount, TimeUnit unit) throws IOException, InterruptedException {
        startRecording();
        Thread.sleep(unit.toMillis(amount));
        stopRecording();
    }

    public void startRecording() throws IOException {
        eventHandler.dispatchEvent(Event.RECORDING, "test1231313131");
        isRecording = true;
        p = pb.start();
        errStream = p.getErrorStream();
        inStream = p.getInputStream();
        outStream = p.getOutputStream();
        new Thread(() -> {
            try {
                while(errStream.read() != -1);
            } catch (IOException e){}
        }).start();
        new Thread(() -> {
            try {
                while (inStream.read() != -1);
            } catch (IOException e){}
        }).start();
    }

    public void stopRecording() throws IOException, InterruptedException {
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outStream));
        bufferedWriter.write("q");
        bufferedWriter.flush();
        bufferedWriter.close();
        if (!p.waitFor(5, TimeUnit.SECONDS)){
            System.out.println("ffmpeg process did not exit properly after 5 seconds");
            p.destroy();
        }
        isRecording = false;
    }

    @Override
    public void setFps(int fps) {
        this.framerate = fps;
        setUpFfmpeg();
    }

    @Override
    public boolean isRecording() {
        return isRecording;
    }
}
