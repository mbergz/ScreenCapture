package Recording;

import Config.RecorderSpecific.Reader.RecorderConfigurationReader;
import Config.RecorderSpecific.RecorderJsonKeyConstants;
import Config.RecorderSpecific.Writer.RecorderConfigurationWriter;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static Config.RecorderSpecific.RecorderJsonKeyConstants.*;

public class FfmpegRecorder implements Recorder{
    // ------- Config variables -------
    private RecorderConfigurationReader recorderConfigurationReader;
    private RecorderConfigurationWriter recorderConfigurationWriter;
    private int framerate;
    private boolean shouldAutoRemoveOld;
    private Path ffmpegPath;
    private Path saveDirPath;
    private String videoSize;
    // --------------------------------

    private Path previousRecordingPath;
    private String movieName = "screen_capture" + UUID.randomUUID() + ".mp4";
    private List<RecorderEventListener> recorderEventListeners = new ArrayList<>();

    private volatile boolean isRecording = false;

    // ------- Process stuff -------
    private ProcessBuilder pb;
    private Process p;
    private InputStream errStream;
    private InputStream inStream;
    private OutputStream outStream;
    // --------------------------------

    public FfmpegRecorder(RecorderConfigurationReader configuration,
                          RecorderConfigurationWriter recorderConfigurationWriter) {
        this.recorderConfigurationReader = configuration;
        this.recorderConfigurationWriter = recorderConfigurationWriter;
        setUpDefaultConfiguration();
        setUpFfmpeg();
    }

    public void generateNewUuidMovieName() {
        movieName = "screen_capture" + UUID.randomUUID() + ".mov";
    }

    private void setUpDefaultConfiguration() {
        ffmpegPath = recorderConfigurationReader.getFfmpegBinPath();
        framerate = recorderConfigurationReader.getFps();
        saveDirPath = recorderConfigurationReader.getDirPathToSavedRecordings();
        shouldAutoRemoveOld = recorderConfigurationReader.isShouldAutoRemoveOld();
        videoSize = recorderConfigurationReader.getVideoSize();
    }

    // ------- CONFIG STUFF -------
        public void dirToSaveRecordingsChanged() {
        System.out.println("received a setDirToSaveRecording event");
        Path path = recorderConfigurationReader.getDirPathToSavedRecordings();
        File dir = path.toFile();
        if (dir.exists() && dir.isDirectory()) {
            pb.directory(dir);
            setUpFfmpeg();
        }
        else {
            System.err.println("Could not set new dir to save recordings in");
        }
    }

    public void autoRemoveChanged() {
        System.out.println("received a autoRemoveChanged event");
        shouldAutoRemoveOld = recorderConfigurationReader.isShouldAutoRemoveOld();
    }

    public void newFfmpegBinPath() {
        System.out.println("received a newFfmprgbinPAth event");
        Path path = recorderConfigurationReader.getFfmpegBinPath();
        File dir = path.toFile();
        if (dir.exists() && dir.isFile()) {
            ffmpegPath = path;
            setUpFfmpeg();
        }
        else {
            System.err.println("Could not set new ffmpeg bin path");
        }
    }

    public void fpsChanged() {
        System.out.println("received a fpsChanged event");
        framerate = recorderConfigurationReader.getFps();
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
        pb = new ProcessBuilder(ffmpegPath.toString(), "-f", "gdigrab", "-framerate", Integer.toString(framerate)
                ,"-video_size" ,videoSize, "-i", "desktop", movieName);
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
        recorderEventListeners.forEach(listerner -> listerner.onRecorderEvent(
                new RecorderEventMessage(RecorderEvent.RECORDING_STARTED))
        );
        isRecording = true;
        p = pb.start();
        errStream = p.getErrorStream();
        inStream = p.getInputStream();
        outStream = p.getOutputStream();
        new Thread(() -> {
            Scanner sc = new Scanner(errStream);
            while(sc.hasNextLine())
            {
                System.err.println(sc.nextLine());
            }
        }).start();
        new Thread(() -> {
            try {
                while (inStream.read() != -1);
            } catch (IOException e){}
        }).start();
    }

    public void stopRecording() throws IOException, InterruptedException {
        previousRecordingPath = Paths.get(getPathToRecording());

        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outStream));
        bufferedWriter.write("q");
        bufferedWriter.flush();
        bufferedWriter.close();
        if (!p.waitFor(5, TimeUnit.SECONDS)){
            System.out.println("ffmpeg process did not exit properly after 5 seconds");
            p.destroy();

            System.err.println("Ffmpeg process aborted, could not finalize recording");
            recorderEventListeners.forEach(listerner -> listerner.onRecorderEvent(
                    new RecorderEventMessage(RecorderEvent.RECORDING_STOPPED))
            );
            removePreviousRecordingIfExist();
        } else {
            recorderEventListeners.forEach(listerner -> listerner.onRecorderEvent(
                    new RecorderEventMessage(RecorderEvent.RECORDING_STOPPED, getPathToRecording()))
            );
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

    @Override
    public void addRecorderEventListner(RecorderEventListener recorderEventListener) {
        recorderEventListeners.add(recorderEventListener);
    }

    // Keep this or seperate methods for each one?
    @Override
    public void setConfigurationValue(RecorderJsonKeyConstants key, String value) {
        switch (key)
        {
            case FPS:
                fpsChanged();
                break;
            case MOVIE_NAME:
                // TODO ?
                break;
            case FFMPEG_PATH_BIN:
                newFfmpegBinPath();
                break;
            case PATH_TO_SAVED_RECORDING:
                dirToSaveRecordingsChanged();
                break;
            case AUTO_REMOVE_OLD_RECORDING:
                autoRemoveChanged();
                break;
        }
        recorderConfigurationWriter.setProperty(key, value);
    }

    @Override
    public RecorderConfigurationReader getConfigurationReader() {
        return recorderConfigurationReader;
    }
}
