package Recording;

import Config.ConfigurationFileReader;
import Config.RecorderSpecific.ffmpeg.FfmpegProcessArgument;
import Config.RecorderSpecific.ffmpeg.Writer.RecorderConfigurationWriter;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static Config.GeneralConfigProperty.AUTO_REMOVE_OLD_RECORDING;

/**
 *  Ffmpeg recorder.
 *
 */
public class FfmpegRecorder implements ProcessRecorder {

    private ConfigurationFileReader recorderConfigurationReader;
    private RecorderConfigurationWriter recorderConfigurationWriter;
    private FFmpegRecorderProcessBuilderProvider myProcessBuilderProvider;

    private boolean shouldAutoRemoveOld;
    private Path previousRecordingPath;
    private List<RecorderEventListener> recorderEventListeners = new ArrayList<>();
    private volatile boolean isRecording = false;

    // ------- Process stuff -------
    private ProcessBuilder pb;
    private Process p;
    private InputStream errStream;
    private InputStream inStream;
    private OutputStream outStream;
    // --------------------------------

    public FfmpegRecorder(ConfigurationFileReader recConfigReader,
                          RecorderConfigurationWriter recConfigWriter) {
        this.recorderConfigurationReader = recConfigReader;
        this.recorderConfigurationWriter = recConfigWriter;
        myProcessBuilderProvider = new FFmpegRecorderProcessBuilderProvider(recConfigReader, recConfigWriter);

        setUpFfmpeg();
        setUpDefaultNonProcessConfig();
    }

    private void setUpDefaultNonProcessConfig() {
        shouldAutoRemoveOld = recorderConfigurationReader.getPropertyAsBoolean(AUTO_REMOVE_OLD_RECORDING.key());
    }

    private void generateNewUuidMovieName() {
        String newMovieName = "screen_capture" + UUID.randomUUID() + ".mov";
        pb = myProcessBuilderProvider.getConfiguredFfmpegPBBuilder(newMovieName);
    }
    private synchronized void shutDownIfActive() {
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
        String startingMovieName = "screen_capture" + UUID.randomUUID() + ".mp4";
        pb = myProcessBuilderProvider.getConfiguredFfmpegPBBuilder(startingMovieName);
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

    public synchronized void startRecording() throws IOException {
        if (isRecording) {
            throw new RuntimeException("Recorder is already running");
        }
        removePreviousRecordingIfExist();
        recorderEventListeners.forEach(listener -> listener.onRecorderEvent(
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

    public synchronized void stopRecording() throws IOException, InterruptedException {
        previousRecordingPath = Paths.get(getPathToRecording());

        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outStream));
        bufferedWriter.write("q");
        bufferedWriter.flush();
        bufferedWriter.close();
        if (!p.waitFor(5, TimeUnit.SECONDS)){
            System.out.println("Ffmpeg process did not exit properly after 5 seconds");
            p.destroy();

            System.err.println("Ffmpeg process aborted, could not finalize recording");
            recorderEventListeners.forEach(listener -> listener.onRecorderEvent(
                    new RecorderEventMessage(RecorderEvent.RECORDING_STOPPED))
            );
            removePreviousRecordingIfExist();
        } else {
            recorderEventListeners.forEach(listener -> listener.onRecorderEvent(
                    new RecorderEventMessage(RecorderEvent.RECORDING_STOPPED, getPathToRecording()))
            );
            generateNewUuidMovieName();
        }
        isRecording = false;
    }

    private String getPathToRecording() {
        String currentMovieName = myProcessBuilderProvider.getMovieName();
        String pbDir = pb.directory().getPath();
        if (pbDir.equalsIgnoreCase(".")) {
            return new File("").getAbsolutePath() + File.separator + currentMovieName;
        }
        return pbDir + File.separator + currentMovieName;
    }

    @Override
    public boolean isRecording() {
        return isRecording;
    }

    @Override
    public void addRecorderEventListener(RecorderEventListener recorderEventListener) {
        recorderEventListeners.add(recorderEventListener);
    }

    @Override
    public void setProcessConfigurationValue(FfmpegProcessArgument key, String value) {
        pb = myProcessBuilderProvider.setNewProcessCmdArgumentValue(key, value);
    }

    @Override
    public void toggleAutoRemoveOldRecording(boolean value) {
        recorderConfigurationWriter.setGeneralProperty(AUTO_REMOVE_OLD_RECORDING.key(), Boolean.toString(value));
        shouldAutoRemoveOld = value;
    }
}
