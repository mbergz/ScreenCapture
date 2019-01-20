package Recording;

import Config.RecorderSpecific.Reader.RecorderConfigurationReader;
import Eventhandlers.Event;
import Eventhandlers.EventHandler;
import Eventhandlers.Payload.RecordingStoppedEventPayload;
import Eventhandlers.SubscribeEvent;

import java.io.*;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class FfmpegRecorder implements Recorder{
    private static final  String RECORDING_STARTED = "Recording started";
    private static final String RECORDING_STOPPED = "Recording stopped";

    // ------- Config variables -------
    private RecorderConfigurationReader recorderConfiguration;
    private int framerate;
    private Path ffmpegPath;
    private Path saveDirPath;
    private String movieName;
    // --------------------------------

    private EventHandler eventHandler;

    private volatile boolean isRecording = false;

    // ------- Process stuff -------
    private ProcessBuilder pb;
    private Process p;
    private InputStream errStream;
    private InputStream inStream;
    private OutputStream outStream;
    // --------------------------------

    public FfmpegRecorder(RecorderConfigurationReader configuration) {
        this.recorderConfiguration = configuration;
        setUpDefaultConfiguration();
        eventHandler = EventHandler.getInstance();
        setUpFfmpeg();
    }

    private void setUpDefaultConfiguration() {
        ffmpegPath = recorderConfiguration.getFfmpegBinPath();
        framerate = recorderConfiguration.getFps();
        saveDirPath = recorderConfiguration.getDirPathToSavedRecordings();
        movieName = recorderConfiguration.getMovieName();
    }

    // TODO
    @SubscribeEvent(event = {Event.RECORDING_NEW_CONFIGURATION_SAVE_DIR_CHANGED})
    public void dirToSaveRecordingsChanged() {
        System.out.println("received a setDirToSaveRecofgin event");
        Path path = recorderConfiguration.getDirPathToSavedRecordings();
        File dir = path.toFile();
        if (dir.exists() && dir.isDirectory()) {
            pb.directory(dir);
            //return true;
        }
        //return false;
    }

    // TODO more configurationEvents

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
        pb = new ProcessBuilder(ffmpegPath.toString(), "-f", "gdigrab", "-framerate", Integer.toString(framerate) , "-i", "desktop", movieName);
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
    public boolean isRecording() {
        return isRecording;
    }
}
