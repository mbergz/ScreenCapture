package Recording;

import Config.ConfigurationFileReader;
import Config.RecorderSpecific.ffmpeg.FfmpegProcessArgument;
import Config.RecorderSpecific.ffmpeg.Writer.RecorderConfigurationWriter;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

import static Config.RecorderSpecific.ffmpeg.FfmpegProcessArgument.*;

/**
 * Responsible for providing a {@link ProcessBuilder} containing a fully configured
 * ffmpeg process with arguments defined in the json configuration file
 */
class FFmpegRecorderProcessBuilderProvider {
    private ConfigurationFileReader recorderConfigurationReader;
    private RecorderConfigurationWriter recorderConfigurationWriter;
    private Map<FfmpegProcessArgument, Runnable> onNewConfigRunners = new HashMap<>();

    private Path ffmpegPath;
    private Path saveDirPath;
    private String movieName;

    private Map<String, String> processCmdArguments = new HashMap<>();
    private Function<String, ProcessBuilder> ffmpegPBSupplier = (movieName) -> new ProcessBuilder(getArguments(movieName))
                .directory(new File(saveDirPath.toString()));

    FFmpegRecorderProcessBuilderProvider(ConfigurationFileReader configurationFileReader,
                                         RecorderConfigurationWriter recorderConfigurationWriter) {
        this.recorderConfigurationReader = configurationFileReader;
        this.recorderConfigurationWriter = recorderConfigurationWriter;
        setUpDefaultConfiguration();
        setUpOnNewConfigRunners();
    }

    /**
     * Configure a new processBuilder with the configuration values defined
     * in the json configuration file
     *
     * @return a new ffmpeg process builder
     */
    ProcessBuilder getConfiguredFfmpegPBBuilder(String movieName) {
        this.movieName = movieName;
        return ffmpegPBSupplier.apply(movieName);
    }

    /**
     * @return the current movie name
     */
    String getMovieName() {
        return movieName;
    }

    /**
     * Sets a new configuration value to be used. Will write to json configuration file.
     *
     * @param key json key to use
     * @param value the new value to set
     * @return a new configured ffmpeg process builder
     */
    ProcessBuilder setNewProcessCmdArgumentValue(FfmpegProcessArgument key, String value) {
        recorderConfigurationWriter.setProcessArgumentProperty(key, value);
        Runnable runner = onNewConfigRunners.get(key);
        if (runner == null)
        {
            throw new RuntimeException("No runner set up for key: " + key);
        }
        runner.run();
        return ffmpegPBSupplier.apply(movieName);
    }

    private List<String> getArguments(String movieName) {
        List<String> list = new ArrayList<>();

        list.add(ffmpegPath.toString());
        list.add("-f");
        list.add("gdigrab");
        processCmdArguments.forEach((key, value) -> {
            list.add("-" + key);
            list.add(value);
        });
        list.add("-i");
        list.add("desktop");
        list.add(movieName);

        return list;
    }

    private void setUpDefaultConfiguration() {
        ffmpegPath = recorderConfigurationReader.getPropertyAsPath(FfmpegProcessArgument.FFMPEG_PATH_BIN.key());
        saveDirPath = recorderConfigurationReader.getPropertyAsPath(FfmpegProcessArgument.PATH_TO_SAVED_RECORDING.key());

        String videoSize = recorderConfigurationReader.getPropertyAsString(FfmpegProcessArgument.VIDEO_SIZE.key());
        processCmdArguments.put(FfmpegProcessArgument.VIDEO_SIZE.key(), videoSize);

        String fps = recorderConfigurationReader.getPropertyAsString(FfmpegProcessArgument.FPS.key());
        processCmdArguments.put(FfmpegProcessArgument.FPS.key(), fps);
    }

    private void setUpOnNewConfigRunners() {
        onNewConfigRunners.put(FPS, this::newFps);
        onNewConfigRunners.put(FFMPEG_PATH_BIN, this::newFfmpegBinPath);
        onNewConfigRunners.put(PATH_TO_SAVED_RECORDING, this::newDirToSaveRecordings);
        onNewConfigRunners.put(VIDEO_SIZE, this::newVideoSize);
    }

    private void newDirToSaveRecordings() {
        System.out.println("dirToSaveRecording changed");
        Path path = recorderConfigurationReader.getPropertyAsPath(FfmpegProcessArgument.PATH_TO_SAVED_RECORDING.key());
        File dir = path.toFile();
        if (dir.exists() && dir.isDirectory()) {
            saveDirPath = dir.toPath();
        }
        else {
            System.err.println("Could not set new dir to save recordings in");
        }
    }

    private void newFfmpegBinPath() {
        System.out.println("newFfmpegBinPath changed");
        Path path = recorderConfigurationReader.getPropertyAsPath(FfmpegProcessArgument.FFMPEG_PATH_BIN.key());
        File dir = path.toFile();
        if (dir.exists() && dir.isFile()) {
            ffmpegPath = path;
        }
        else {
            System.err.println("Could not set new ffmpeg bin path");
        }
    }

    private void newFps() {
        System.out.println("fps changed");
        String fpsKey = FfmpegProcessArgument.FPS.key();
        String newFPs = recorderConfigurationReader.getPropertyAsString(fpsKey);
        processCmdArguments.put(fpsKey, newFPs);
    }

    private void newVideoSize() {
        System.out.println("video size changed");
        String videoSizeKey = FfmpegProcessArgument.VIDEO_SIZE.key();
        String newVidSize = recorderConfigurationReader.getPropertyAsString(videoSizeKey);
        processCmdArguments.put(videoSizeKey, newVidSize);
    }

}
