package Recording;

import Config.ConfigurationHandler;
import Eventhandlers.Event;
import Eventhandlers.EventHandler;
import Eventhandlers.Payload.RecordingStoppedEventPayload;

import java.io.*;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class FfmpegRecorder implements Recorder{
    private static int DEFAULT_FRAMERATE = 30;
    private static String RECORDING_STARTED = "Recording started";
    private static String RECORDING_STOPPED = "Recording stopped";

    private String ffmpegPath = "c:\\Users\\Martin\\Documents\\ffmpeg\\ffmpeg-20190112-1ea5529-win64-static\\bin\\ffmpeg.exe";
    private int framerate = DEFAULT_FRAMERATE;
    private ProcessBuilder pb;
    private Process p;
    private InputStream errStream;
    private InputStream inStream;
    private OutputStream outStream;
    private EventHandler eventHandler;
    private String movieName = "myTest123.mov";

    private volatile boolean isRecording = false;

    public FfmpegRecorder() throws FileNotFoundException {
        eventHandler = EventHandler.getInstance();
        setUpFfmpegPathBin();
        setUpFfmpeg();
    }

    private void setUpFfmpegPathBin() throws FileNotFoundException {
        Optional<String> ffmpegConfigValue = ConfigurationHandler.getInstance()
                .getProperty("ffmpegPathBin");
        if (ffmpegConfigValue.isPresent()){
            ffmpegPath = ffmpegConfigValue.get();
            return;
        }
        final String OS = System.getProperty("os.name").toLowerCase();
        if (OS.contains("nix") || OS.contains("nux") || OS.contains("aix")) {
            ffmpegPath = "ffmpeg";
        } else if (OS.contains("win")) {
            Optional<Path> ffmpegPathOnWin = SetFfmpegBinHelper.setLocalFfmpegOnWindows();
            if (ffmpegPathOnWin.isPresent()) {
                ffmpegPath = ffmpegPathOnWin.get().toString();
            } else {
                throw new FileNotFoundException("Could not locate ffmpeg.exe on windows C:// drive");
            }
        }
    }

    @Override
    public boolean setDirectoryToSaveRecordings(Path directoryPath) {
        File dir = directoryPath.toFile();
        if (dir.exists() && dir.isDirectory()) {
            pb.directory(dir);
            return true;
        }
        return false;
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
        pb = new ProcessBuilder(ffmpegPath, "-f", "gdigrab", "-framerate", Integer.toString(framerate) , "-i", "desktop", movieName);
        pb.directory(new File("."));
    }

    public void recordForLimitedTime(int amount, TimeUnit unit) throws IOException, InterruptedException {
        startRecording();
        Thread.sleep(unit.toMillis(amount));
        stopRecording();
    }

    public void startRecording() throws IOException {
        // TODO
        // Set UUID on movie name, add option to autoremove
        eventHandler.dispatchEvent(Event.RECORDING_STARTED, () -> RECORDING_STARTED);
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
            eventHandler.dispatchEvent(Event.RECORDING_STOPPED,
                    () -> "Ffmpeg process aborted, could not finalize recording");
        } else {
            eventHandler.dispatchEvent(Event.RECORDING_STOPPED, new RecordingStoppedEventPayload()
                    .setMessage(RECORDING_STOPPED)
                    .setPathToRecordedFile(getFfmpegCwdAsString()));
        }
        isRecording = false;
    }

    private String getFfmpegCwdAsString() {
        String pbDir = pb.directory().getPath();
        if (pbDir.equalsIgnoreCase(".")) {
            return new File("").getAbsolutePath() + File.separator + movieName;
        }
        return pbDir + File.separator + movieName;
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

    @Override
    public void setAutoRemovalOfOldRecording(boolean shouldRemove) {
        // TODO
    }
}
