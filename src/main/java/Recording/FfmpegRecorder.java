package Recording;

import Config.RecorderSpecific.Reader.RecorderConfigurationReader;
import Eventhandlers.Event;
import Eventhandlers.EventHandler;
import Eventhandlers.Payload.RecordingStoppedEventPayload;
import Eventhandlers.SubscribeEvent;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class FfmpegRecorder implements Recorder{
    private static final  String RECORDING_STARTED = "Recording started";
    private static final String RECORDING_STOPPED = "Recording stopped";

    // ------- Config variables -------
    private RecorderConfigurationReader recorderConfiguration;
    private int framerate;
    private boolean shouldAutoRemoveOld;
    private Path ffmpegPath;
    private Path saveDirPath;
    // --------------------------------

    private EventHandler eventHandler;
    private Path previousRecordingPath;
    private String movieName = "screen_capture" + UUID.randomUUID() + ".mov";


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

    public void generateNewUuidMovieName() {
        movieName = "screen_capture" + UUID.randomUUID() + ".mov";
    }

    private void setUpDefaultConfiguration() {
        ffmpegPath = recorderConfiguration.getFfmpegBinPath();
        framerate = recorderConfiguration.getFps();
        saveDirPath = recorderConfiguration.getDirPathToSavedRecordings();
        shouldAutoRemoveOld = recorderConfiguration.isShouldAutoRemoveOld();
    }

    // ------- CONFIG STUFF -------
    @SubscribeEvent(event = {Event.RECORDING_NEW_CONFIGURATION_SAVE_DIR_CHANGED})
        public void dirToSaveRecordingsChanged() {
        System.out.println("received a setDirToSaveRecording event");
        Path path = recorderConfiguration.getDirPathToSavedRecordings();
        File dir = path.toFile();
        if (dir.exists() && dir.isDirectory()) {
            pb.directory(dir);
            setUpFfmpeg();
        }
        else {
            System.err.println("Could not sed new dir to save recordings in");
        }
    }

    @SubscribeEvent(event = {Event.RECORDING_NEW_CONFIGURATION_AUTO_REMOVE_CHANGED})
    public void autoRemoveChanged() {
        System.out.println("received a autoRemoveChanged event");
        shouldAutoRemoveOld = recorderConfiguration.isShouldAutoRemoveOld();
    }

    @SubscribeEvent(event = {Event.RECORDING_NEW_CONFIGURATION_FFMPEG_BIN_PATH_CHANGED})
    public void newFfmpegBinPath() {
        System.out.println("received a newFfmprgbinPAth event");
        Path path = recorderConfiguration.getFfmpegBinPath();
        File dir = path.toFile();
        if (dir.exists() && dir.isFile()) {
            ffmpegPath = path;
            setUpFfmpeg();
        }
        else {
            System.err.println("Could not set new ffmpeg bin path");
        }
    }

    @SubscribeEvent(event = {Event.RECORDING_NEW_CONFIGURATION_FPS_CHANGED})
    public void fpsChanged() {
        System.out.println("received a fpsChanged event");
        framerate = recorderConfiguration.getFps();
        setUpFfmpeg();
    }
    // ----------------------------

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

    private void removePreviousRecordingIfExist() {
        if (previousRecordingPath == null || previousRecordingPath.toFile().isDirectory()) {
            return;
        }
        if (shouldAutoRemoveOld) {
            FileUtils.deleteQuietly(previousRecordingPath.toFile());
        }
        setUpFfmpeg();
    }

    public void startRecording() throws IOException {
        removePreviousRecordingIfExist();
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
        // Set previousrecodingPath to equal this new recording
        previousRecordingPath = Paths.get(getPathToRecording());

        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outStream));
        bufferedWriter.write("q");
        bufferedWriter.flush();
        bufferedWriter.close();
        if (!p.waitFor(5, TimeUnit.SECONDS)){
            System.out.println("ffmpeg process did not exit properly after 5 seconds");
            p.destroy();
            eventHandler.dispatchEvent(Event.RECORDING_STOPPED,
                    () -> "Ffmpeg process aborted, could not finalize recording");
            removePreviousRecordingIfExist();
        } else {
            eventHandler.dispatchEvent(Event.RECORDING_STOPPED, new RecordingStoppedEventPayload()
                    .setMessage(RECORDING_STOPPED)
                    .setPathToRecordedFile(getPathToRecording()));
            generateNewUuidMovieName();
        }
        isRecording = false;
    }

    private String getPathToRecording() {
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
