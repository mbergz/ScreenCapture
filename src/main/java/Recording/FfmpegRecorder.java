package Recording;

import java.io.*;
import java.util.concurrent.TimeUnit;

public class FfmpegRecorder implements Recorder{
    final String ffmpegPath = "c:\\Users\\Martin\\Documents\\ffmpeg\\ffmpeg-20190112-1ea5529-win64-static\\bin\\ffmpeg.exe";
    private int framerate = 10;
    private ProcessBuilder pb;
    private Process p;
    private InputStream errStream;
    private InputStream inStream;
    private OutputStream outStream;

    private volatile boolean isRecording = false;

    public FfmpegRecorder() {
        setUpFfmpeg();
    }

    public FfmpegRecorder(int framerate) {
        this.framerate = framerate;
        setUpFfmpeg();
    }

    private void setUpFfmpeg() {
        if (pb != null) {
            if (isRecording){
                try {
                    stopRecording();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            p.destroy();
        }
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
